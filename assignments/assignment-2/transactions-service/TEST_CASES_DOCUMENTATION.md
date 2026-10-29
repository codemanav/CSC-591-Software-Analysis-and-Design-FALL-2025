# Test Cases Documentation for `updateStatus()` Method

## Method Under Test

**Method**: `TransactionService.updateStatus(Long id, UpdateTransactionStatusRequest req)`

**Location**: `com.ecocycle.transactions.service.TransactionService`

**Purpose**: Updates the status of a transaction. If the status is set to COMPLETED, it also increments the green scores for both buyer and seller.

---

## Test Results Summary

✅ **All 10 tests PASSED**

- **5 Black-Box Test Cases** (Equivalence Class Partitioning)
- **5 White-Box Test Cases** (Control Flow Testing)

---

## Black-Box Test Cases (Equivalence Class Partitioning)

### Equivalence Classes Identified

#### Input Parameter 1: Transaction ID (`Long id`)

| Equivalence Class | Description | Valid/Invalid | Justification |
|-------------------|-------------|---------------|---------------|
| **EC1**: Valid Transaction ID | ID exists in repository (positive Long > 0) | Valid | Normal operation requires existing transaction |
| **EC2**: Invalid Transaction ID | ID does not exist in repository (null or non-existent) | Invalid | Should throw exception when transaction not found |

#### Input Parameter 2: Transaction Status (`UpdateTransactionStatusRequest.status`)

| Equivalence Class | Description | Valid/Invalid | Justification |
|-------------------|-------------|---------------|---------------|
| **EC3**: COMPLETED Status | Status = COMPLETED | Valid | Triggers special logic (green score updates) |
| **EC4**: Non-COMPLETED Status | Status = PENDING, CONFIRMED, or CANCELLED | Valid | Normal status update without score changes |

#### External Dependency: UsersClient Service

| Equivalence Class | Description | Valid/Invalid | Justification |
|-------------------|-------------|---------------|---------------|
| **EC5**: UsersClient Success | Both score updates succeed | Valid | Happy path for COMPLETED status |
| **EC6**: UsersClient Failure | Exception thrown during score update | Invalid | Error handling path when external service fails |

### Boundary Conditions

- **Boundary 1**: All enum values tested (PENDING, CONFIRMED, COMPLETED, CANCELLED)
- **Boundary 2**: Transaction ID = 0 (edge case, but handled as invalid)
- **Boundary 3**: First score update succeeds, second fails (partial success scenario)

### Black-Box Test Cases Table

| Test ID | Equivalence Classes Covered | Input Values | Expected Output | Test Result |
|---------|----------------------------|--------------|-----------------|-------------|
| **BB-1** | EC1 (Valid ID), EC3 (COMPLETED), EC5 (UsersClient Success) | id=1L, status=COMPLETED | Status updated to COMPLETED, buyer score +5, seller score +10, transaction saved | ✅ PASSED |
| **BB-2** | EC1 (Valid ID), EC4 (Non-COMPLETED) | id=1L, status=PENDING | Status updated to PENDING, NO score updates, transaction saved | ✅ PASSED |
| **BB-3** | EC2 (Invalid ID) | id=999L (non-existent) | RuntimeException("Transaction not found") | ✅ PASSED |
| **BB-4** | EC1 (Valid ID), EC3 (COMPLETED), EC6 (UsersClient Failure) | id=1L, status=COMPLETED, UsersClient throws exception | RuntimeException("Failed to update user scores: ...") | ✅ PASSED |
| **BB-5** | EC1 (Valid ID), EC4 (All non-COMPLETED), EC3 (COMPLETED) | id=1L, status=PENDING/CONFIRMED/CANCELLED/COMPLETED | Each status updates correctly; only COMPLETED triggers scores | ✅ PASSED |

### Detailed Test Case Descriptions

#### Test Case BB-1: Valid Transaction ID + COMPLETED Status
- **Equivalence Classes**: EC1, EC3, EC5
- **Input**: Valid transaction ID (1L), status = COMPLETED
- **Expected**: Status updated, buyer score +5, seller score +10, transaction saved
- **Justification**: Tests the happy path where transaction exists and status is COMPLETED, triggering score updates
- **Result**: ✅ PASSED

#### Test Case BB-2: Valid Transaction ID + Non-COMPLETED Status
- **Equivalence Classes**: EC1, EC4
- **Input**: Valid transaction ID (1L), status = PENDING
- **Expected**: Status updated, NO score updates, transaction saved
- **Justification**: Tests that non-COMPLETED statuses don't trigger score updates
- **Result**: ✅ PASSED

#### Test Case BB-3: Invalid Transaction ID (Not Found)
- **Equivalence Classes**: EC2
- **Input**: Non-existent transaction ID (999L)
- **Expected**: RuntimeException("Transaction not found")
- **Justification**: Tests error handling when transaction doesn't exist
- **Result**: ✅ PASSED

#### Test Case BB-4: Valid Transaction ID + COMPLETED Status + UsersClient Exception
- **Equivalence Classes**: EC1, EC3, EC6
- **Input**: Valid transaction ID (1L), status = COMPLETED, UsersClient throws exception
- **Expected**: RuntimeException with message about failed score update
- **Justification**: Tests error handling when external service fails during score update
- **Result**: ✅ PASSED

#### Test Case BB-5: Valid Transaction ID + All Status Types (Boundary Testing)
- **Equivalence Classes**: EC1, EC4 (all values), EC3
- **Input**: Valid transaction ID (1L), all status values (PENDING, CONFIRMED, CANCELLED, COMPLETED)
- **Expected**: Each status updates correctly; only COMPLETED triggers score updates
- **Justification**: Boundary testing - ensures all enum values are handled correctly
- **Result**: ✅ PASSED

---

## White-Box Test Cases (Control Flow Testing)

### Control Flow Graph (CFG) for `updateStatus()`

```
START
  ↓
[1] repo.findById(id)
  ↓
[2] Optional.empty? ──YES──→ [EXCEPTION: "Transaction not found"] ──→ END
  ↓ NO
[3] tx.setStatus(req.status())
  ↓
[4] tx.setUpdatedAt(Instant.now())
  ↓
[5] if (tx.getStatus() == COMPLETED)?
  ↓ NO                    ↓ YES
[8] repo.save(tx)    [6] try {
  ↓                      users.incrementGreenScore(buyerId, 5)
[9] return DTO            users.incrementGreenScore(sellerId, 10)
  ↓                    } catch (Exception e) {
END                         throw RuntimeException
                          }
                          ↓
                      [7] repo.save(tx)
                          ↓
                      [9] return DTO
                          ↓
                      END
```

### Independent Paths Identified

| Path ID | Path Description | CFG Nodes Covered | Branch Coverage |
|---------|------------------|-------------------|-----------------|
| **Path 1** | Transaction found → status != COMPLETED → save → return | [1]→[2]→[3]→[4]→[5]→[8]→[9] | False branch of if (status == COMPLETED) |
| **Path 2** | Transaction found → status == COMPLETED → scores updated → save → return | [1]→[2]→[3]→[4]→[5]→[6]→[7]→[9] | True branch, no exception |
| **Path 3** | Transaction found → status == COMPLETED → exception in try block → catch → throw | [1]→[2]→[3]→[4]→[5]→[6]→catch→throw | Exception on first score update |
| **Path 4** | Transaction not found → throw exception | [1]→[2]→exception | Early exit path |
| **Path 5** | Transaction found → status == COMPLETED → first score succeeds, second fails → catch → throw | [1]→[2]→[3]→[4]→[5]→[6]→catch→throw | Exception on second score update |

### Test Case to Path Mapping

| Test ID | Path Covered | CFG Nodes | Coverage Type | Test Result |
|---------|--------------|-----------|---------------|-------------|
| **WB-1** | Path 1 | [1]→[2]→[3]→[4]→[5]→[8]→[9] | Branch coverage (false branch) | ✅ PASSED |
| **WB-2** | Path 2 | [1]→[2]→[3]→[4]→[5]→[6]→[7]→[9] | Path coverage (happy path) | ✅ PASSED |
| **WB-3** | Path 3 | [1]→[2]→[3]→[4]→[5]→[6]→catch→throw | Exception path (first call) | ✅ PASSED |
| **WB-4** | Path 4 | [1]→[2]→exception | Early exit path | ✅ PASSED |
| **WB-5** | Path 5 | [1]→[2]→[3]→[4]→[5]→[6]→catch→throw | Exception path (second call) | ✅ PASSED |

### Detailed White-Box Test Case Descriptions

#### Test Case WB-1: Path 1 - Status Not COMPLETED
- **Path Covered**: Path 1
- **CFG Flow**: Transaction found → status CONFIRMED → skip COMPLETED block → save → return
- **Coverage**: Branch coverage - tests the false branch of `if (status == COMPLETED)`
- **Justification**: Ensures non-COMPLETED statuses bypass the score update logic
- **Result**: ✅ PASSED

#### Test Case WB-2: Path 2 - Status COMPLETED, Scores Updated Successfully
- **Path Covered**: Path 2
- **CFG Flow**: Transaction found → status COMPLETED → both scores updated → save → return
- **Coverage**: Path coverage - tests the complete happy path for COMPLETED status
- **Justification**: Verifies all steps execute correctly when status is COMPLETED and scores update successfully
- **Result**: ✅ PASSED

#### Test Case WB-3: Path 3 - Status COMPLETED, UsersClient Exception
- **Path Covered**: Path 3
- **CFG Flow**: Transaction found → status COMPLETED → first score update throws exception → catch → throw RuntimeException
- **Coverage**: Exception handling path when UsersClient fails on first call
- **Justification**: Tests error handling when the first score update fails
- **Result**: ✅ PASSED

#### Test Case WB-4: Path 4 - Transaction Not Found
- **Path Covered**: Path 4
- **CFG Flow**: repo.findById returns empty → orElseThrow → throw exception
- **Coverage**: Early exit path when transaction doesn't exist
- **Justification**: Tests the guard clause that prevents processing when transaction is not found
- **Result**: ✅ PASSED

#### Test Case WB-5: Path 5 - Status COMPLETED, Second Score Update Fails
- **Path Covered**: Path 5
- **CFG Flow**: Transaction found → status COMPLETED → first score succeeds → second score fails → catch → throw
- **Coverage**: Exception handling when second score update fails
- **Justification**: Tests partial success scenario where first update succeeds but second fails
- **Result**: ✅ PASSED

---

## Code Coverage

Coverage report generated at: `target/site/jacoco/index.html`

To view coverage:
```bash
cd transactions-service
open target/site/jacoco/index.html
```

---

## Running the Tests

```bash
# Run all tests
mvn test -Dtest=TransactionServiceTest

# Run tests with coverage report
mvn clean test -Dtest=TransactionServiceTest jacoco:report

# View coverage report
open target/site/jacoco/index.html
```

---

## Test File Location

`src/test/java/com/ecocycle/transactions/service/TransactionServiceTest.java`
