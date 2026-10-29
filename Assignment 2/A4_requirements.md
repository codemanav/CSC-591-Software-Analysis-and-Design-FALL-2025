# A4: Testing, Code Smells, and Refactoring — Full Marks Requirements

This document summarizes *exactly* what must be completed to score **full marks** on Assignment 4 for the Software Analysis and Design course (CSC491/CSC591). All requirements are derived strictly from the assignment PDF.

---

## ✅ 1. Select One Method in Your EcoCycle Microservice

Choose ONE method from any of your services (marketplace-service, users-service, or transactions-service).
All test cases, coverage, code smell analysis, and refactoring will be based on this single method.

---

## ✅ 2. Design 10 Total Test Cases

### **A. 5 Black-Box Test Cases (Equivalence Class Partitioning)**
Your report must include:
- Identification of equivalence classes
- Valid and invalid partitions
- Boundary conditions
- Justification for each class
- 5 complete test cases with expected outputs

---

### **B. 5 White-Box Test Cases (Control Flow Testing)**
Your report must include:
- Control Flow Graph (CFG) of the method
- Identification of independent paths
- Coverage rationale (path or branch coverage)
- Mapping each test case to its covered path
- 5 detailed test cases

---

## ✅ 3. Implement Tests Using a Testing Framework

You must:
- Write real test code (JUnit recommended)
- Execute your tests
- Generate a **coverage report** (Jacoco, IntelliJ, Maven plugin, etc.)
- Include a screenshot/table of coverage % in your PDF

---

## ✅ 4. Detect at Least 5 Code Smells

Use a tool such as:
- **DesigniteJava**
- **SonarQube / SonarCloud**
- **RefactoringMiner**

Your report must include:
- At least **5 code smells**
- For each smell:
  - File name
  - Smell type (e.g., Long Method, Feature Envy)
  - Description
  - Screenshot or tool output snippet

---

## ✅ 5. Refactor the Code Smells

For each of the 5 code smells, you must provide:

### **A. Before Refactoring**
- Screenshot of the tool detecting the smell
- Explanation of why it is a smell

### **B. Refactoring Performed**
Example refactorings:
- Extract Method
- Extract Class
- Replace Conditional with Polymorphism
- Introduce Parameter Object
- Simplify Conditional Logic
- Remove Dead Code
- Dependency Injection
- Rename for clarity
- Reduce parameter list

Must include *clear explanation* of what you changed and why.

### **C. After Refactoring**
- Run the same smell detection tool again
- Provide screenshot/output showing the smell is REMOVED

This is necessary to receive credit.

---

## ✅ 6. Final PDF Report Requirements

Your submitted PDF must include:

### **A. Title Page**
- Name
- Unity ID
- Course: Software Analysis and Design
- Assignment 4
- Date

### **B. Selected Method Description**
- Microservice where it exists
- Purpose of the method
- Code snippet (main report or appendix)

### **C. Testing Sections**
#### **Black-Box ECP**
- Equivalence classes
- 5 test cases

#### **White-Box Control Flow**
- Control flow graph
- Independent paths
- 5 test cases

#### **Coverage Report**
- Table or screenshot

### **D. Code Smell and Refactoring Section**
For each of 5 smells:
1. Tool detection output (before)
2. Explanation of smell
3. Refactoring method used
4. Tool output showing smell removed (after)

### **E. Conclusion**

---

## ⭐ Grading Criteria (for Full Marks)

You must demonstrate:

- Correct use of **Equivalence Class Partitioning**
- Correct use of **Control Flow–based testing**
- Correct identification of code smells
- Correct and effective refactoring
- Proper documentation showing BEFORE and AFTER refactoring

---

## 🧨 Additional Expectations (Not Explicitly Mentioned but Required)

- The selected method must contain meaningful logic—not trivial getters/setters.
- The 10 test cases must be completely distinct.
- All tests must PASS both before and after refactoring.
- Include full test code in appendices.
- Report length typically 7–12 pages.

---
