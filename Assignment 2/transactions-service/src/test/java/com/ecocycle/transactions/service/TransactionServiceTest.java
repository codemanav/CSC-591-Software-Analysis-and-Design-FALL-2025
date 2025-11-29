package com.ecocycle.transactions.service;

import com.ecocycle.transactions.client.UsersClient;
import com.ecocycle.transactions.dto.TransactionDto;
import com.ecocycle.transactions.dto.UpdateTransactionStatusRequest;
import com.ecocycle.transactions.exception.GreenScoreUpdateException;
import com.ecocycle.transactions.model.Transaction;
import com.ecocycle.transactions.model.TransactionStatus;
import com.ecocycle.transactions.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Test class for TransactionService.updateStatus() method
 * 
 * This test suite includes:
 * - 5 Black-Box Test Cases (Equivalence Class Partitioning)
 * - 5 White-Box Test Cases (Control Flow Testing)
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("TransactionService.updateStatus() Tests")
class TransactionServiceTest {

    @Mock
    private TransactionRepository repository;

    @Mock
    private UsersClient usersClient;

    @InjectMocks
    private TransactionService transactionService;

    private Transaction testTransaction;
    private Long testTransactionId;
    private Long testBuyerId;
    private Long testSellerId;

    @BeforeEach
    void setUp() {
        testTransactionId = 1L;
        testBuyerId = 100L;
        testSellerId = 200L;

        testTransaction = new Transaction();
        testTransaction.setId(testTransactionId);
        testTransaction.setListingId(10L);
        testTransaction.setBuyerId(testBuyerId);
        testTransaction.setSellerId(testSellerId);
        testTransaction.setStatus(TransactionStatus.PENDING);
        testTransaction.setAgreedPrice(new BigDecimal("50.00"));
        testTransaction.setCreatedAt(Instant.now());
        testTransaction.setUpdatedAt(Instant.now());
    }

    // ============================================================================
    // BLACK-BOX TEST CASES (Equivalence Class Partitioning)
    // ============================================================================

    /**
     * BLACK-BOX TEST CASE 1: Valid Transaction ID + COMPLETED Status
     * 
     * Equivalence Class: Valid inputs
     * - Valid transaction ID (exists in repository)
     * - Valid status: COMPLETED
     * 
     * Expected: Transaction status updated, green scores incremented, transaction saved
     */
    @Test
    @DisplayName("BB-1: Update status to COMPLETED with valid transaction ID - should update scores")
    void testUpdateStatus_ValidId_CompletedStatus_ShouldUpdateScores() {
        // Arrange
        UpdateTransactionStatusRequest request = new UpdateTransactionStatusRequest(TransactionStatus.COMPLETED);
        when(repository.findById(testTransactionId)).thenReturn(Optional.of(testTransaction));
        when(repository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));
        doNothing().when(usersClient).incrementGreenScore(anyLong(), anyInt());

        // Act
        TransactionDto result = transactionService.updateStatus(testTransactionId, request);

        // Assert
        assertNotNull(result);
        assertEquals(TransactionStatus.COMPLETED, result.status());
        verify(repository, times(1)).findById(testTransactionId);
        verify(usersClient, times(1)).incrementGreenScore(testBuyerId, 5);
        verify(usersClient, times(1)).incrementGreenScore(testSellerId, 10);
        verify(repository, times(1)).save(testTransaction);
    }

    /**
     * BLACK-BOX TEST CASE 2: Valid Transaction ID + Non-COMPLETED Status
     * 
     * Equivalence Class: Valid inputs, different status
     * - Valid transaction ID (exists in repository)
     * - Valid status: PENDING, CONFIRMED, or CANCELLED (not COMPLETED)
     * 
     * Expected: Transaction status updated, green scores NOT incremented, transaction saved
     */
    @Test
    @DisplayName("BB-2: Update status to PENDING with valid transaction ID - should not update scores")
    void testUpdateStatus_ValidId_NonCompletedStatus_ShouldNotUpdateScores() {
        // Arrange
        UpdateTransactionStatusRequest request = new UpdateTransactionStatusRequest(TransactionStatus.PENDING);
        when(repository.findById(testTransactionId)).thenReturn(Optional.of(testTransaction));
        when(repository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        TransactionDto result = transactionService.updateStatus(testTransactionId, request);

        // Assert
        assertNotNull(result);
        assertEquals(TransactionStatus.PENDING, result.status());
        verify(repository, times(1)).findById(testTransactionId);
        verify(usersClient, never()).incrementGreenScore(anyLong(), anyInt());
        verify(repository, times(1)).save(testTransaction);
    }

    /**
     * BLACK-BOX TEST CASE 3: Invalid Transaction ID (Not Found)
     * 
     * Equivalence Class: Invalid inputs
     * - Invalid transaction ID (does not exist in repository)
     * - Valid status: any status
     * 
     * Expected: RuntimeException thrown with "Transaction not found"
     */
    @Test
    @DisplayName("BB-3: Update status with non-existent transaction ID - should throw exception")
    void testUpdateStatus_InvalidId_ShouldThrowException() {
        // Arrange
        Long nonExistentId = 999L;
        UpdateTransactionStatusRequest request = new UpdateTransactionStatusRequest(TransactionStatus.COMPLETED);
        when(repository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            transactionService.updateStatus(nonExistentId, request);
        });

        assertEquals("Transaction not found", exception.getMessage());
        verify(repository, times(1)).findById(nonExistentId);
        verify(usersClient, never()).incrementGreenScore(anyLong(), anyInt());
        verify(repository, never()).save(any(Transaction.class));
    }

    /**
     * BLACK-BOX TEST CASE 4: Valid Transaction ID + COMPLETED Status + UsersClient Exception
     * 
     * Equivalence Class: Valid inputs, external service failure
     * - Valid transaction ID (exists in repository)
     * - Valid status: COMPLETED
     * - UsersClient throws exception when updating scores
     * 
     * Expected: RuntimeException thrown with message about failed score update
     */
    @Test
    @DisplayName("BB-4: Update status to COMPLETED but UsersClient fails - should throw exception")
    void testUpdateStatus_ValidId_CompletedStatus_UsersClientFails_ShouldThrowException() {
        // Arrange
        UpdateTransactionStatusRequest request = new UpdateTransactionStatusRequest(TransactionStatus.COMPLETED);
        when(repository.findById(testTransactionId)).thenReturn(Optional.of(testTransaction));
        doThrow(new RuntimeException("Service unavailable")).when(usersClient).incrementGreenScore(testBuyerId, 5);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            transactionService.updateStatus(testTransactionId, request);
        });

        assertTrue(exception.getMessage().contains("Failed to update user scores"));
        verify(repository, times(1)).findById(testTransactionId);
        verify(usersClient, times(1)).incrementGreenScore(testBuyerId, 5);
        verify(usersClient, never()).incrementGreenScore(testSellerId, 10);
        verify(repository, never()).save(any(Transaction.class));
    }

    /**
     * BLACK-BOX TEST CASE 5: Valid Transaction ID + All Status Types (Boundary Testing)
     * 
     * Equivalence Class: Valid inputs, all enum values
     * - Valid transaction ID (exists in repository)
     * - All possible status values: PENDING, CONFIRMED, COMPLETED, CANCELLED
     * 
     * Expected: Each status updates correctly, COMPLETED triggers score updates
     */
    @Test
    @DisplayName("BB-5: Update status with all possible status values - boundary testing")
    void testUpdateStatus_ValidId_AllStatusTypes_ShouldHandleAllStatuses() {
        // Test PENDING
        testTransaction.setStatus(TransactionStatus.PENDING);
        UpdateTransactionStatusRequest requestPending = new UpdateTransactionStatusRequest(TransactionStatus.PENDING);
        when(repository.findById(testTransactionId)).thenReturn(Optional.of(testTransaction));
        when(repository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));
        
        TransactionDto resultPending = transactionService.updateStatus(testTransactionId, requestPending);
        assertEquals(TransactionStatus.PENDING, resultPending.status());
        verify(usersClient, never()).incrementGreenScore(anyLong(), anyInt());
        reset(repository, usersClient);

        // Test CONFIRMED
        testTransaction.setStatus(TransactionStatus.CONFIRMED);
        UpdateTransactionStatusRequest requestConfirmed = new UpdateTransactionStatusRequest(TransactionStatus.CONFIRMED);
        when(repository.findById(testTransactionId)).thenReturn(Optional.of(testTransaction));
        when(repository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));
        
        TransactionDto resultConfirmed = transactionService.updateStatus(testTransactionId, requestConfirmed);
        assertEquals(TransactionStatus.CONFIRMED, resultConfirmed.status());
        verify(usersClient, never()).incrementGreenScore(anyLong(), anyInt());
        reset(repository, usersClient);

        // Test CANCELLED
        testTransaction.setStatus(TransactionStatus.CANCELLED);
        UpdateTransactionStatusRequest requestCancelled = new UpdateTransactionStatusRequest(TransactionStatus.CANCELLED);
        when(repository.findById(testTransactionId)).thenReturn(Optional.of(testTransaction));
        when(repository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));
        
        TransactionDto resultCancelled = transactionService.updateStatus(testTransactionId, requestCancelled);
        assertEquals(TransactionStatus.CANCELLED, resultCancelled.status());
        verify(usersClient, never()).incrementGreenScore(anyLong(), anyInt());
        reset(repository, usersClient);

        // Test COMPLETED (should update scores)
        testTransaction.setStatus(TransactionStatus.COMPLETED);
        UpdateTransactionStatusRequest requestCompleted = new UpdateTransactionStatusRequest(TransactionStatus.COMPLETED);
        when(repository.findById(testTransactionId)).thenReturn(Optional.of(testTransaction));
        when(repository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));
        doNothing().when(usersClient).incrementGreenScore(anyLong(), anyInt());
        
        TransactionDto resultCompleted = transactionService.updateStatus(testTransactionId, requestCompleted);
        assertEquals(TransactionStatus.COMPLETED, resultCompleted.status());
        verify(usersClient, times(1)).incrementGreenScore(testBuyerId, 5);
        verify(usersClient, times(1)).incrementGreenScore(testSellerId, 10);
    }

    // ============================================================================
    // WHITE-BOX TEST CASES (Control Flow Testing)
    // ============================================================================

    /**
     * WHITE-BOX TEST CASE 1: Path 1 - Transaction found, status != COMPLETED
     * 
     * Control Flow Path: 
     * 1. repo.findById(id) → returns Optional<Transaction>
     * 2. tx.setStatus(req.status())
     * 3. tx.setUpdatedAt(Instant.now())
     * 4. if (tx.getStatus() == COMPLETED) → FALSE (skip block)
     * 5. repo.save(tx)
     * 6. return TransactionDto.from(tx)
     * 
     * Coverage: Branch where status is not COMPLETED
     */
    @Test
    @DisplayName("WB-1: Path - Transaction found, status CONFIRMED (not COMPLETED)")
    void testUpdateStatus_WhiteBox_Path1_StatusNotCompleted() {
        // Arrange
        UpdateTransactionStatusRequest request = new UpdateTransactionStatusRequest(TransactionStatus.CONFIRMED);
        when(repository.findById(testTransactionId)).thenReturn(Optional.of(testTransaction));
        when(repository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        TransactionDto result = transactionService.updateStatus(testTransactionId, request);

        // Assert - Verify path taken
        assertNotNull(result);
        assertEquals(TransactionStatus.CONFIRMED, result.status());
        verify(repository).findById(testTransactionId);
        verify(repository).save(testTransaction);
        verify(usersClient, never()).incrementGreenScore(anyLong(), anyInt());
    }

    /**
     * WHITE-BOX TEST CASE 2: Path 2 - Transaction found, status == COMPLETED, scores updated successfully
     * 
     * Control Flow Path:
     * 1. repo.findById(id) → returns Optional<Transaction>
     * 2. tx.setStatus(req.status()) → COMPLETED
     * 3. tx.setUpdatedAt(Instant.now())
     * 4. if (tx.getStatus() == COMPLETED) → TRUE (enter block)
     * 5. try { users.incrementGreenScore(buyerId, 5) } → success
     * 6. users.incrementGreenScore(sellerId, 10) → success
     * 7. catch block → NOT executed
     * 8. repo.save(tx)
     * 9. return TransactionDto.from(tx)
     * 
     * Coverage: Branch where status is COMPLETED and scores update successfully
     */
    @Test
    @DisplayName("WB-2: Path - Transaction found, status COMPLETED, scores updated successfully")
    void testUpdateStatus_WhiteBox_Path2_StatusCompleted_ScoresUpdated() {
        // Arrange
        UpdateTransactionStatusRequest request = new UpdateTransactionStatusRequest(TransactionStatus.COMPLETED);
        when(repository.findById(testTransactionId)).thenReturn(Optional.of(testTransaction));
        when(repository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));
        doNothing().when(usersClient).incrementGreenScore(anyLong(), anyInt());

        // Act
        TransactionDto result = transactionService.updateStatus(testTransactionId, request);

        // Assert - Verify complete path taken
        assertNotNull(result);
        assertEquals(TransactionStatus.COMPLETED, result.status());
        verify(repository).findById(testTransactionId);
        verify(usersClient).incrementGreenScore(testBuyerId, 5);
        verify(usersClient).incrementGreenScore(testSellerId, 10);
        verify(repository).save(testTransaction);
    }

    /**
     * WHITE-BOX TEST CASE 3: Path 3 - Transaction found, status == COMPLETED, UsersClient throws exception
     * 
     * Control Flow Path:
     * 1. repo.findById(id) → returns Optional<Transaction>
     * 2. tx.setStatus(req.status()) → COMPLETED
     * 3. tx.setUpdatedAt(Instant.now())
     * 4. if (tx.getStatus() == COMPLETED) → TRUE (enter block)
     * 5. try { users.incrementGreenScore(buyerId, 5) } → throws Exception
     * 6. catch (Exception e) → executed
     * 7. throw new GreenScoreUpdateException("Failed to update user scores", e)
     * 8. repo.save(tx) → NOT executed
     * 9. return → NOT executed
     * 
     * Coverage: Exception handling path when UsersClient fails
     */
    @Test
    @DisplayName("WB-3: Path - Transaction found, status COMPLETED, UsersClient throws exception")
    void testUpdateStatus_WhiteBox_Path3_StatusCompleted_UsersClientException() {
        // Arrange
        UpdateTransactionStatusRequest request = new UpdateTransactionStatusRequest(TransactionStatus.COMPLETED);
        when(repository.findById(testTransactionId)).thenReturn(Optional.of(testTransaction));
        RuntimeException clientException = new RuntimeException("Network error");
        doThrow(clientException).when(usersClient).incrementGreenScore(testBuyerId, 5);

        // Act & Assert
        GreenScoreUpdateException exception = assertThrows(GreenScoreUpdateException.class, () -> {
            transactionService.updateStatus(testTransactionId, request);
        });

        // Verify exception path
        assertEquals("Failed to update user scores", exception.getMessage());
        assertEquals(clientException, exception.getCause());
        verify(repository).findById(testTransactionId);
        verify(usersClient).incrementGreenScore(testBuyerId, 5);
        verify(usersClient, never()).incrementGreenScore(testSellerId, 10);
        verify(repository, never()).save(any(Transaction.class));
    }

    /**
     * WHITE-BOX TEST CASE 4: Path 4 - Transaction not found
     * 
     * Control Flow Path:
     * 1. repo.findById(id) → returns Optional.empty()
     * 2. .orElseThrow(() -> new RuntimeException("Transaction not found")) → throws exception
     * 3. Rest of method → NOT executed
     * 
     * Coverage: Early exit path when transaction doesn't exist
     */
    @Test
    @DisplayName("WB-4: Path - Transaction not found, early exception")
    void testUpdateStatus_WhiteBox_Path4_TransactionNotFound() {
        // Arrange
        Long nonExistentId = 999L;
        UpdateTransactionStatusRequest request = new UpdateTransactionStatusRequest(TransactionStatus.COMPLETED);
        when(repository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            transactionService.updateStatus(nonExistentId, request);
        });

        // Verify early exit path
        assertEquals("Transaction not found", exception.getMessage());
        verify(repository).findById(nonExistentId);
        verify(usersClient, never()).incrementGreenScore(anyLong(), anyInt());
        verify(repository, never()).save(any(Transaction.class));
    }

    /**
     * WHITE-BOX TEST CASE 5: Path 5 - Transaction found, status == COMPLETED, second score update fails
     * 
     * Control Flow Path:
     * 1. repo.findById(id) → returns Optional<Transaction>
     * 2. tx.setStatus(req.status()) → COMPLETED
     * 3. tx.setUpdatedAt(Instant.now())
     * 4. if (tx.getStatus() == COMPLETED) → TRUE (enter block)
     * 5. try { users.incrementGreenScore(buyerId, 5) } → success
     * 6. users.incrementGreenScore(sellerId, 10) → throws Exception
     * 7. catch (Exception e) → executed
     * 8. throw new RuntimeException("Failed to update user scores: " + e.getMessage(), e)
     * 9. repo.save(tx) → NOT executed
     * 
     * Coverage: Exception handling when second score update fails
     */
    @Test
    @DisplayName("WB-5: Path - Transaction found, status COMPLETED, second score update fails")
    void testUpdateStatus_WhiteBox_Path5_StatusCompleted_SecondScoreUpdateFails() {
        // Arrange
        UpdateTransactionStatusRequest request = new UpdateTransactionStatusRequest(TransactionStatus.COMPLETED);
        when(repository.findById(testTransactionId)).thenReturn(Optional.of(testTransaction));
        doNothing().when(usersClient).incrementGreenScore(testBuyerId, 5);
        RuntimeException secondException = new RuntimeException("Database error");
        doThrow(secondException).when(usersClient).incrementGreenScore(testSellerId, 10);

        // Act & Assert
        GreenScoreUpdateException exception = assertThrows(GreenScoreUpdateException.class, () -> {
            transactionService.updateStatus(testTransactionId, request);
        });

        // Verify exception path after first success
        assertEquals("Failed to update user scores", exception.getMessage());
        assertEquals(secondException, exception.getCause());
        verify(repository).findById(testTransactionId);
        verify(usersClient).incrementGreenScore(testBuyerId, 5);
        verify(usersClient).incrementGreenScore(testSellerId, 10);
        verify(repository, never()).save(any(Transaction.class));
    }
}

