# Report Reference Guide - Where to Find Everything

This document shows exactly where each required component is documented for your Assignment 4 report.

---

## 📍 Location of Documentation

### Main Documentation File
**File**: `TEST_CASES_DOCUMENTATION.md`

This file contains:
- ✅ Equivalence Classes (with detailed tables)
- ✅ Test Case to Path Mapping (with CFG node references)
- ✅ Control Flow Graph (CFG)
- ✅ All 10 test cases with descriptions

---

## 📊 Equivalence Classes Documentation

**Location**: `TEST_CASES_DOCUMENTATION.md` → Section: "Black-Box Test Cases (Equivalence Class Partitioning)"

**What's Included**:
1. **Equivalence Classes Table** - Shows all 6 equivalence classes (EC1-EC6) with:
   - Description
   - Valid/Invalid classification
   - Justification

2. **Boundary Conditions** - Lists all boundary test cases

3. **Test Cases Table** - Maps each test case to its equivalence classes

---

## 🔀 Test Case to Path Mapping

**Location**: `TEST_CASES_DOCUMENTATION.md` → Section: "White-Box Test Cases (Control Flow Testing)"

**What's Included**:
1. **Control Flow Graph (CFG)** - ASCII diagram showing all paths
2. **Independent Paths Table** - Lists all 5 paths with:
   - Path description
   - CFG nodes covered
   - Branch coverage type
3. **Test Case to Path Mapping Table** - Shows:
   - Which test case covers which path
   - CFG nodes traversed
   - Coverage type achieved

---

## 📝 Test Code

**Location**: `src/test/java/com/ecocycle/transactions/service/TransactionServiceTest.java`

**Contains**:
- All 10 test implementations
- Detailed comments explaining each test
- Mock setup and verification

---

## 📈 Coverage Report

**Location**: `target/site/jacoco/index.html`

**To Generate**:
```bash
cd transactions-service
mvn clean test -Dtest=TransactionServiceTest jacoco:report
open target/site/jacoco/index.html
```

---

## 📋 Quick Reference Tables

### Equivalence Classes Summary

| EC ID | Class Name | Covered By Tests |
|-------|------------|------------------|
| EC1 | Valid Transaction ID | BB-1, BB-2, BB-4, BB-5 |
| EC2 | Invalid Transaction ID | BB-3 |
| EC3 | COMPLETED Status | BB-1, BB-4, BB-5 |
| EC4 | Non-COMPLETED Status | BB-2, BB-5 |
| EC5 | UsersClient Success | BB-1 |
| EC6 | UsersClient Failure | BB-4 |

### Path Coverage Summary

| Path ID | Path Description | Covered By Test |
|---------|------------------|-----------------|
| Path 1 | Status != COMPLETED | WB-1 |
| Path 2 | COMPLETED, scores succeed | WB-2 |
| Path 3 | COMPLETED, first score fails | WB-3 |
| Path 4 | Transaction not found | WB-4 |
| Path 5 | COMPLETED, second score fails | WB-5 |

---

## 🎯 For Your Report

### Section 1: Black-Box Testing
- **Equivalence Classes**: Copy tables from `TEST_CASES_DOCUMENTATION.md`
- **Test Cases**: Use the "Black-Box Test Cases Table"
- **Justifications**: Included in each test case description

### Section 2: White-Box Testing
- **CFG**: Copy ASCII diagram from `TEST_CASES_DOCUMENTATION.md`
- **Paths**: Use "Independent Paths Identified" table
- **Mapping**: Use "Test Case to Path Mapping" table

### Section 3: Coverage Report
- **Screenshot**: Take screenshot of `target/site/jacoco/index.html`
- **Table**: Extract coverage percentages from the report

---

## 📂 File Structure

```
transactions-service/
├── src/
│   ├── main/java/com/ecocycle/transactions/service/
│   │   └── TransactionService.java (method under test)
│   └── test/java/com/ecocycle/transactions/service/
│       └── TransactionServiceTest.java (all 10 tests)
├── target/site/jacoco/
│   └── index.html (coverage report)
├── TEST_CASES_DOCUMENTATION.md (main documentation)
├── TESTING_SETUP.md (testing framework guide)
└── REPORT_REFERENCE.md (this file)
```

---

## ✅ Checklist for Report

- [x] Equivalence classes identified and documented
- [x] 5 Black-box test cases designed and implemented
- [x] Control Flow Graph created
- [x] 5 White-box test cases designed and implemented
- [x] Test case to path mapping documented
- [x] All tests passing
- [x] Coverage report generated
