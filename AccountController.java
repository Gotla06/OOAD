import java.time.LocalDate;

public class AccountController {
    private BankController bankController;
    private Bank bank;

    public AccountController(BankController bankController, Bank bank) {
        this.bankController = bankController;
        this.bank = bank;
    }

    // Business logic for account creation
    public void handleCreateAccount(boolean isIndividual, String accountType,
                                    AccountCreationView.IndividualFormData individualData,
                                    AccountCreationView.CompanyFormData companyData) {
        try {
            // Input validation
            if (!validateInput(isIndividual, individualData, companyData)) {
                return;
            }

            // Business logic - create account
            boolean success;
            if (isIndividual) {
                success = createIndividualAccount(individualData, accountType);
            } else {
                success = createCompanyAccount(companyData, accountType);
            }

            if (success) {
                System.out.println("Account created successfully!");
                bankController.showLoginView();
            } else {
                System.out.println("Failed to create account");
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
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

    private boolean createIndividualAccount(AccountCreationView.IndividualFormData data, String accountType) {
        try {
            String customerId = "CUST" + System.currentTimeMillis();
            java.util.Date dob = java.sql.Date.valueOf(data.dateOfBirth);

            IndividualCustomer customer = new IndividualCustomer(
                    customerId, data.firstName, data.lastName, data.address, data.idNumber, dob
            );

            bank.addCustomer(customer);

            String accNum = "ACC" + System.currentTimeMillis();
            double initialBalance = accountType.equals("Investment") ? 500.00 : 0.0;

            Account account = createAccount(accountType, accNum, initialBalance, "Gaborone Main", customer);
            return bank.openAccount(account);

        } catch (Exception e) {
            System.out.println("Error creating individual account: " + e.getMessage());
            return false;
        }
    }

    private boolean createCompanyAccount(AccountCreationView.CompanyFormData data, String accountType) {
        try {
            String customerId = "CUST" + System.currentTimeMillis();

            CompanyCustomer company = new CompanyCustomer(
                    customerId, data.companyName, data.registrationNumber, data.address, data.contactPerson
            );

            bank.addCustomer(company);

            String accNum = "ACC" + System.currentTimeMillis();
            double initialBalance = accountType.equals("Investment") ? 500.00 : 0.0;

            Account account = createAccount(accountType, accNum, initialBalance, "Gaborone Main", company);
            return bank.openAccount(account);

        } catch (Exception e) {
            System.out.println("Error creating company account: " + e.getMessage());
            return false;
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
                return new ChequeAccount(accNum, balance, branch, customer);
            default:
                throw new IllegalArgumentException("Invalid account type: " + type);
        }
    }
}
