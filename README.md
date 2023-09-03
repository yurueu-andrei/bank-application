## Project Overview

The CleverBank Application is a robust and efficient banking system designed to manage multiple banks,
user accounts, and transactions. This project focuses on fulfilling the requirements and standards set forth while
providing a clear, maintainable codebase.

## Features

The CleverBank Web Application offers several key features:

1. **Deposit and Withdrawal**: Perform deposit and withdrawal operations on ONLY CLEVERBANK accounts.

2. **Fund Transfer**: Transfer funds between accounts, including interbank transfers, with built-in thread safety.

3. **Interest Accrual**: Automatically apply percents on account balances at the end of each month.

4. **Statements and Checks Generation**: Automatically generate check files for each transaction and account and money statements saving them into tomcat directory

## Endpoints

### Statements
- **GET /statements/money/{accountNumber}?from={dateFrom}&to={dateTo}**
Used to generate money statement for given account(only Cleverbank)
- **GET /statements/account/{accountNumber}?from={dateFrom}&to={dateTo}**
Used to generate account statement for given account(only Cleverbank)

### Accounts

- **GET /accounts/{id}**
Used to find account by id. Returns AccountResponseDto. Throws EntityNotFoundException if no account is found + status 404
- **GET /accounts?page={page}&size={size}**
Used to find multiple accounts with pagination. Params: page - page number(starts with 0), size - page size. Returns List of AccountResponseDto
- **PUT /accounts/{accountNumber}/withdraw?amount={amount}**
Used to withdraw money from CLEVERBANK account. Amount can't be greater than account balance. Saves transaction and generates check in case of successful call
- **PUT /accounts/{accountNumber}/deposit?amount={amount}**
Used to deposit money into CLEVERBANK account. Saves transaction and generates check in case of successful call
- **PUT /accounts/{senderAccountNumber}/transfer/{receiverAccountNumber}?amount={amount}**
Used to transfer money FROM or INTO CLEVERBANK account. Receiver or sender must be CleverBank. Amount can't be greater than sender account balance. Saves transaction and generates check in case of successful call
- **DELETE /accounts/{id}**
Used to delete account. Returns 204 status if successful. If account is already doesn't exist throws EntityNotFoundException + status 404. Only CleverBank accounts can be deleted


### Transactions

- **GET /transactions/{id}**
Used to find transaction by id. Returns TransactionResponseDto. Throws EntityNotFoundException if no transaction is found + status 404
- **GET /transactions?page={page}&size={size}**
Used to find multiple transactions with pagination. Params: page - page number(starts with 0), size - page size. Returns List of TransactionResponseDto

### Banks

- **GET /banks/{id}**
Used to find bank by id. Returns BankResponseDto. Throws EntityNotFoundException if no bank is found + status 404
- **GET /banks?page={page}&size={size}**
Used to find multiple banks with pagination. Params: page - page number(starts with 0), size - page size. Returns List of BankResponseDto
- **POST /banks**
Used to add bank. Body format - BankRequestDto. Response - BankResponseDto
- **PUT /banks/{id}**
Used to update bank. Body format - BankRequestDto. Response - BankResponseDto
- **DELETE /banks/{id}**
Used to delete bank. Returns 204 status if successful. If bank is already doesn't exist, throws EntityNotFoundException + status 404. Deletes all banks's accounts

### Users

- **GET /users/{id}**
Used to find user by id. Returns UserResponseDto. Throws EntityNotFoundException if no user is found + status 404
- **GET /users?page={page}&size={size}**
Used to find multiple users with pagination. Params: page - page number(starts with 0), size - page size. Returns List of UserResponseDto
- **POST /users**
Used to add user. Body format - UserRequestDto. Response - UserResponseDto
- **PUT /users/{id}**
Used to update user. Body format - UserRequestDto. Response - UserResponseDto
- **DELETE /users/{id}**
Used to delete user. Returns 204 status if successful. If user is already doesn't exist, throws EntityNotFoundException + status 404. Deletes all user's accounts


## Key Components

The application consists of the following primary components:

- **Banks**: Manage multiple banks with distinct identifiers and details.

- **Users**: Create and manage user profiles, each associated with one or more accounts.

- **Accounts**: Handle user accounts, storing balance information and enabling transaction operations.

- **Transactions**: Record and track transactions, including deposit, withdrawal, and fund transfers.

## Running the Application

To run the application:

1. Clone this repository to your local machine.

2. Configure Postgres settings in the `application.yml` file.

3. Build the project using Gradle: `./gradlew build`.

4. Run the application with: `./gradlew run`.

## Testing

Run unit tests using the following command:

```bash
./gradlew test
```