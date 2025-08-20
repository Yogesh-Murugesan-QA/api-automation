# API Automation with RestAssured

## Objective
Automated tests for Intergalactic Bank API hosted at https://template.postman-echo.com

## Test Scenarios
1. Valid Transaction (happy path)
   - Create two accounts
   - Perform fund transfer
   - Verify balances

2. Invalid Transaction (negative flow)
   - Attempt transfer with insufficient funds
   - Validate error message

## How to Run
1. Clone repo
2. Install dependencies: `mvn clean install`
3. Run tests: `mvn test`

## Tech Stack
- Java 17
- RestAssured 5.x
- TestNG
- Maven
