import java.time.LocalDate;

public class AccountController {
    private BankController bankController;
    private Bank bank;

    public AccountController(BankController bankController, Bank bank) {
        this.bankController = bankController;
        this.bank = bank;
    }

    public void handleCreateAccount(boolean isIndividual, String accountType,
                                    AccountCreationView.IndividualFormData individualData,
                                    AccountCreationView.CompanyFormData companyData) {
        try {
            // First validate input
            if (!validateInput(isIndividual, individualData, companyData)) {
                return;
            }

            String customerId = null;
            boolean success;
            String customerName = "";
            String accountNumber = "";

            if (isIndividual) {
                CreateAccountResult result = createIndividualAccount(individualData, accountType);
                customerId = result.customerId;
                success = result.success;
                customerName = individualData.firstName + " " + individualData.lastName;
                accountNumber = result.accountNumber;
            } else {
                CreateAccountResult result = createCompanyAccount(companyData, accountType);
                customerId = result.customerId;
                success = result.success;
                customerName = companyData.companyName;
                accountNumber = result.accountNumber;
            }

            if (success && customerId != null) {
                // SHOW COMPLETE SUCCESS MESSAGE
                displaySuccessMessage(customerName, customerId, accountType, accountNumber);

                // Show success message and return to login
                String successMessage = String.format(
                        "Account created successfully!\nCustomer: %s\nCustomer ID: %s\nUse this ID to login",
                        customerName, customerId
                );
                bankController.showLoginViewWithMessage(successMessage);
            } else {
                System.out.println("Failed to create account. Please check the requirements.");
            }

        } catch (Exception e) {
            System.out.println("Error creating account: " + e.getMessage());
        }
    }

    public void handleBackToLogin() {
        bankController.showLoginView();
    }

    private boolean validateInput(boolean isIndividual,
                                  AccountCreationView.IndividualFormData individualData,
                                  AccountCreationView.CompanyFormData companyData) {
        if (isIndividual) {
            return validateIndividualData(individualData);
        } else {
            return validateCompanyData(companyData);
        }
    }

    private boolean validateIndividualData(AccountCreationView.IndividualFormData data) {
        if (data.firstName == null || data.firstName.trim().isEmpty()) {
            System.out.println("First name is required");
            return false;
        }
        if (data.lastName == null || data.lastName.trim().isEmpty()) {
            System.out.println("Last name is required");
            return false;
        }
        if (data.address == null || data.address.trim().isEmpty()) {
            System.out.println("Address is required");
            return false;
        }
        if (data.idNumber == null || data.idNumber.trim().isEmpty()) {
            System.out.println("ID number is required");
            return false;
        }
        if (data.dateOfBirth == null) {
            System.out.println("Date of birth is required");
            return false;
        }
        // Additional validation for cheque accounts
        if (data.employed && (data.employerName == null || data.employerName.trim().isEmpty())) {
            System.out.println("Employer name is required for employed individuals");
            return false;
        }
        return true;
    }

    private boolean validateCompanyData(AccountCreationView.CompanyFormData data) {
        if (data.companyName == null || data.companyName.trim().isEmpty()) {
            System.out.println("Company name is required");
            return false;
        }
        if (data.registrationNumber == null || data.registrationNumber.trim().isEmpty()) {
            System.out.println("Registration number is required");
            return false;
        }
        if (data.contactPerson == null || data.contactPerson.trim().isEmpty()) {
            System.out.println("Contact person is required");
            return false;
        }
        if (data.address == null || data.address.trim().isEmpty()) {
            System.out.println("Address is required");
            return false;
        }
        return true;
    }

    private CreateAccountResult createIndividualAccount(AccountCreationView.IndividualFormData data, String accountType) {
        try {
            String customerId = bank.generateCustomerId();
            java.util.Date dob = java.sql.Date.valueOf(data.dateOfBirth);

            IndividualCustomer customer = new IndividualCustomer(
                    customerId, data.firstName, data.lastName, data.address,
                    data.idNumber, dob, data.employed, data.employerName, data.employerAddress
            );

            // Validate if customer can open this account type
            AccountType accType = AccountType.valueOf(accountType.toUpperCase());
            if (!customer.canOpenAccount(accType)) {
                System.out.println("Customer cannot open " + accountType + " account. Employment required for cheque accounts.");
                return new CreateAccountResult(false, null, null);
            }

            bank.addCustomer(customer);

            String accNum = bank.generateAccountNumber();
            double initialBalance = accountType.equalsIgnoreCase("investment") ?
                    InvestmentAccount.getMinOpeningBalance() : 0.0;

            Account account = createAccount(accountType, accNum, initialBalance, "Gaborone Main", customer);
            boolean accountOpened = bank.openAccount(account);

            if (accountOpened) {
                return new CreateAccountResult(true, customerId, accNum);
            }
            return new CreateAccountResult(false, null, null);

        } catch (Exception e) {
            System.out.println("Error creating individual account: " + e.getMessage());
            return new CreateAccountResult(false, null, null);
        }
    }

    private CreateAccountResult createCompanyAccount(AccountCreationView.CompanyFormData data, String accountType) {
        try {
            String customerId = bank.generateCustomerId();

            CompanyCustomer company = new CompanyCustomer(
                    customerId, data.companyName, data.registrationNumber, data.address, data.contactPerson
            );

            bank.addCustomer(company);

            String accNum = bank.generateAccountNumber();
            double initialBalance = accountType.equalsIgnoreCase("investment") ?
                    InvestmentAccount.getMinOpeningBalance() : 0.0;

            Account account = createAccount(accountType, accNum, initialBalance, "Gaborone Main", company);
            boolean accountOpened = bank.openAccount(account);

            if (accountOpened) {
                return new CreateAccountResult(true, customerId, accNum);
            }
            return new CreateAccountResult(false, null, null);

        } catch (Exception e) {
            System.out.println("Error creating company account: " + e.getMessage());
            return new CreateAccountResult(false, null, null);
        }
    }

    private Account createAccount(String type, String accNum, double balance,
                                  String branch, Customer customer) {
        switch (type.toLowerCase()) {
            case "savings":
                return new SavingsAccount(accNum, balance, branch, customer);
            case "investment":
                return new InvestmentAccount(accNum, balance, branch, customer);
            case "cheque":
                if (customer instanceof IndividualCustomer) {
                    IndividualCustomer individual = (IndividualCustomer) customer;
                    return new ChequeAccount(accNum, balance, branch, customer,
                            individual.getEmployerName(), individual.getEmployerAddress());
                } else {
                    return new ChequeAccount(accNum, balance, branch, customer,
                            "Self", customer.getAddress());
                }
            default:
                throw new IllegalArgumentException("Invalid account type: " + type);
        }
    }

    private void displaySuccessMessage(String customerName, String customerId, String accountType, String accountNumber) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("🎉 ACCOUNT CREATION SUCCESSFUL!");
        System.out.println("=".repeat(60));
        System.out.println("Customer Name: " + customerName);
        System.out.println("Customer ID: " + customerId);
        System.out.println("Account Type: " + accountType);
        System.out.println("Account Number: " + accountNumber);
        System.out.println("=".repeat(60));
        System.out.println("💡 IMPORTANT: Use Customer ID '" + customerId + "' to login!");
        System.out.println("📝 Password can be any text (system uses simple validation)");
        System.out.println("=".repeat(60) + "\n");
    }

    // Helper class to return multiple values from account creation
    private static class CreateAccountResult {
        boolean success;
        String customerId;
        String accountNumber;

        CreateAccountResult(boolean success, String customerId, String accountNumber) {
            this.success = success;
            this.customerId = customerId;
            this.accountNumber = accountNumber;
        }
    }
}