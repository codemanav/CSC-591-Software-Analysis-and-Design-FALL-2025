# Code Smells Refactoring Plan

## Summary

**Method Under Analysis**: `TransactionService.updateStatus()`

**Total Smells to Refactor**: 5

---

## Smell 1: Magic Number (Buyer Score - 5)

### Before Refactoring
```java
users.incrementGreenScore(tx.getBuyerId(), 5);
```

### Detection
- **Tool**: DesigniteJava
- **File**: `implementationCodeSmells.csv`, Line 6
- **Type**: Magic Number
- **Location**: `TransactionService.updateStatus()`, Line 81

### Refactoring
- **Type**: Extract Constant
- **Action**: Create constant `BUYER_GREEN_SCORE_INCREMENT = 5`

### After Refactoring
```java
private static final int BUYER_GREEN_SCORE_INCREMENT = 5;
// ...
users.incrementGreenScore(tx.getBuyerId(), BUYER_GREEN_SCORE_INCREMENT);
```

---

## Smell 2: Magic Number (Seller Score - 10)

### Before Refactoring
```java
users.incrementGreenScore(tx.getSellerId(), 10);
```

### Detection
- **Tool**: DesigniteJava
- **File**: `implementationCodeSmells.csv`, Line 7
- **Type**: Magic Number
- **Location**: `TransactionService.updateStatus()`, Line 82

### Refactoring
- **Type**: Extract Constant
- **Action**: Create constant `SELLER_GREEN_SCORE_INCREMENT = 10`

### After Refactoring
```java
private static final int SELLER_GREEN_SCORE_INCREMENT = 10;
// ...
users.incrementGreenScore(tx.getSellerId(), SELLER_GREEN_SCORE_INCREMENT);
```

---

## Smell 3: Long Method

### Before Refactoring
- Method has 16 lines and handles multiple responsibilities

### Detection
- **Tool**: Manual Analysis + Method Metrics
- **File**: `methodMetrics.csv`
- **Type**: Long Method
- **Location**: `TransactionService.updateStatus()`
- **Evidence**: LOC=16, handles 5 different concerns

### Refactoring
- **Type**: Extract Method
- **Action**: Extract green score update logic into `updateGreenScoresForCompletedTransaction()`

### After Refactoring
```java
private void updateGreenScoresForCompletedTransaction(Transaction tx) {
    try {
        users.incrementGreenScore(tx.getBuyerId(), BUYER_GREEN_SCORE_INCREMENT);
        users.incrementGreenScore(tx.getSellerId(), SELLER_GREEN_SCORE_INCREMENT);
    } catch (Exception e) {
        throw new GreenScoreUpdateException("Failed to update user scores", e);
    }
}
```

---

## Smell 4: Feature Envy

### Before Refactoring
- Method directly calls UsersClient multiple times

### Detection
- **Tool**: Manual Analysis
- **Type**: Feature Envy
- **Location**: Lines 81-82
- **Evidence**: Method is more interested in UsersClient than its own class

### Refactoring
- **Type**: Extract Method (same as Smell 3)
- **Action**: Encapsulate UsersClient calls in separate method

### After Refactoring
- Same as Smell 3 - extracting method reduces feature envy

---

## Smell 5: Poor Error Handling

### Before Refactoring
```java
throw new RuntimeException("Failed to update user scores: " + e.getMessage(), e);
```

### Detection
- **Tool**: Manual Analysis
- **Type**: Poor Exception Handling / Primitive Obsession
- **Location**: Line 84
- **Evidence**: Generic RuntimeException with string concatenation

### Refactoring
- **Type**: Replace Exception with Custom Exception
- **Action**: Create `GreenScoreUpdateException` class

### After Refactoring
```java
// New exception class
public class GreenScoreUpdateException extends RuntimeException {
    public GreenScoreUpdateException(String message, Throwable cause) {
        super(message, cause);
    }
}

// Usage
throw new GreenScoreUpdateException("Failed to update user scores", e);
```

---

## Verification Steps

After each refactoring:
1. Run tests: `mvn test -Dtest=TransactionServiceTest`
2. Re-run DesigniteJava: `./run_designite.sh`
3. Verify smell is removed from output
4. Take screenshot of before/after

---

## Expected Outcomes

- ✅ Magic Numbers replaced with named constants
- ✅ Method length reduced (extracted method)
- ✅ Better error handling with custom exception
- ✅ Reduced feature envy
- ✅ All tests still passing
- ✅ Code smells removed from DesigniteJava output
