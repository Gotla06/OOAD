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
            System.out.println("🔧 Starting account creation process...");
            System.out.println("   Customer Type: " + (isIndividual ? "Individual" : "Company"));
            System.out.println("   Requested Account Type: " + accountType);

            // First validate input
            if (!validateInput(isIndividual, individualData, companyData, accountType)) {
                System.out.println("❌ Validation failed");
                return;
            }

            String customerId = null;
            boolean success;
            String customerName = "";
            String createdAccountType = "";

            if (isIndividual) {
                CreateAccountResult result = createIndividualAccount(individualData, accountType);
                customerId = result.customerId;
                success = result.success;
                customerName = individualData.firstName + " " + individualData.lastName;
                createdAccountType = result.accountType;
            } else {
                CreateAccountResult result = createCompanyAccount(companyData, accountType);
                customerId = result.customerId;
                success = result.success;
                customerName = companyData.companyName;
                createdAccountType = result.accountType;
            }

            if (success && customerId != null) {
                // SHOW COMPLETE SUCCESS MESSAGE
                displaySuccessMessage(customerName, customerId, createdAccountType);

                // Show success message and return to login
                String successMessage = String.format(
                        "Account created successfully!\nCustomer: %s\nCustomer ID: %s\nAccount Type: %s\nUse this ID to login",
                        customerName, customerId, createdAccountType
                );
                bankController.showLoginViewWithMessage(successMessage);

                // Debug: Print all accounts to verify
                bank.debugPrintAllAccounts();
            } else {
                System.out.println("❌ Failed to create account. Please check the requirements.");
            }

        } catch (Exception e) {
            System.out.println("❌ Error creating account: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void handleBackToLogin() {
        bankController.showLoginView();
    }

    private boolean validateInput(boolean isIndividual,
                                  AccountCreationView.IndividualFormData individualData,
                                  AccountCreationView.CompanyFormData companyData, String accountType) {
        if (isIndividual) {
            return validateIndividualData(individualData, accountType);
        } else {
            return validateCompanyData(companyData);
        }
    }

    private boolean validateIndividualData(AccountCreationView.IndividualFormData data, String accountType) {
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
        if (accountType.equals("Cheque") && data.employed &&
                (data.employerName == null || data.employerName.trim().isEmpty())) {
            System.out.println("Employer name is required for employed individuals opening cheque accounts");
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
                return new CreateAccountResult(false, null, null, null);
            }

            bank.addCustomer(customer);

            String accNum = bank.generateAccountNumber();
            double initialBalance = accountType.equalsIgnoreCase("investment") ?
                    InvestmentAccount.getMinOpeningBalance() : 0.0;

            System.out.println("🔧 Creating " + accountType + " account for " + customer.getFullName());
            System.out.println("   Account Number: " + accNum);
            System.out.println("   Initial Balance: BWP " + initialBalance);

            Account account = createAccount(accountType, accNum, initialBalance, "Gaborone Main", customer);
            boolean accountOpened = bank.openAccount(account);

            if (accountOpened) {
                System.out.println("✅ Successfully created " + accountType + " account: " + accNum);
                return new CreateAccountResult(true, customerId, accountType, accNum);
            } else {
                System.out.println("❌ Failed to create " + accountType + " account");
            }
            return new CreateAccountResult(false, null, null, null);

        } catch (Exception e) {
            System.out.println("❌ Error creating individual account: " + e.getMessage());
            e.printStackTrace();
            return new CreateAccountResult(false, null, null, null);
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

            System.out.println("🔧 Creating " + accountType + " account for " + company.getFullName());
            System.out.println("   Account Number: " + accNum);
            System.out.println("   Initial Balance: BWP " + initialBalance);

            Account account = createAccount(accountType, accNum, initialBalance, "Gaborone Main", company);
            boolean accountOpened = bank.openAccount(account);

            if (accountOpened) {
                System.out.println("✅ Successfully created " + accountType + " account: " + accNum);
                return new CreateAccountResult(true, customerId, accountType, accNum);
            }
            return new CreateAccountResult(false, null, null, null);

        } catch (Exception e) {
            System.out.println("❌ Error creating company account: " + e.getMessage());
            e.printStackTrace();
            return new CreateAccountResult(false, null, null, null);
        }
    }

    private Account createAccount(String type, String accNum, double balance,
                                  String branch, Customer customer) {
        System.out.println("🔧 createAccount() called with type: '" + type + "'");

        switch (type.toLowerCase()) {
            case "savings":
                System.out.println("💰 Creating Savings Account: " + accNum);
                return new SavingsAccount(accNum, balance, branch, customer);
            case "investment":
                System.out.println("📈 Creating Investment Account: " + accNum);
                return new InvestmentAccount(accNum, balance, branch, customer);
            case "cheque":
                System.out.println("🏦 Creating Cheque Account: " + accNum);
                if (customer instanceof IndividualCustomer) {
                    IndividualCustomer individual = (IndividualCustomer) customer;
                    return new ChequeAccount(accNum, balance, branch, customer,
                            individual.getEmployerName(), individual.getEmployerAddress());
                } else {
                    // For companies, use company name as employer
                    return new ChequeAccount(accNum, balance, branch, customer,
                            ((CompanyCustomer) customer).getFullName(), customer.getAddress());
                }
            default:
                System.out.println("❌ Unknown account type: " + type);
                throw new IllegalArgumentException("Invalid account type: " + type);
        }
    }

    private void displaySuccessMessage(String customerName, String customerId, String accountType) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("🎉 ACCOUNT CREATION SUCCESSFUL!");
        System.out.println("=".repeat(60));
        System.out.println("Customer Name: " + customerName);
        System.out.println("Customer ID: " + customerId);
        System.out.println("Account Type: " + accountType);
        System.out.println("=".repeat(60));
        System.out.println("💡 IMPORTANT: Use Customer ID '" + customerId + "' to login!");
        System.out.println("📝 Password can be any text (system uses simple validation)");
        System.out.println("=".repeat(60) + "\n");
    }

    // Helper class to return multiple values from account creation
    private static class CreateAccountResult {
        boolean success;
        String customerId;
        String accountType;
        String accountNumber;

        CreateAccountResult(boolean success, String customerId, String accountType, String accountNumber) {
            this.success = success;
            this.customerId = customerId;
            this.accountType = accountType;
            this.accountNumber = accountNumber;
        }
    }
}