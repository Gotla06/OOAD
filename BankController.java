import javafx.stage.Stage;

public class BankController {
    private Stage primaryStage;
    private Bank bank;
    private LoginController loginController;
    private AccountController accountController;
    private DashboardController dashboardController;
    private TransactionController transactionController;

    public BankController() {
        this.bank = new Bank();
        initializeControllers();
    }

    private void initializeControllers() {
        this.loginController = new LoginController(this, bank);
        this.accountController = new AccountController(this, bank);
        this.dashboardController = new DashboardController(this, bank);
        this.transactionController = new TransactionController(this, bank);
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void showLoginView() {
        LoginView loginView = new LoginView(loginController);
        primaryStage.setScene(loginView.getScene());
        primaryStage.setTitle("Banking System - Login");
        primaryStage.show();
    }

    public void showLoginViewWithMessage(String message) {
        LoginView loginView = new LoginView(loginController);
        loginView.setMessage(message, true);
        primaryStage.setScene(loginView.getScene());
        primaryStage.setTitle("Banking System - Login");
        primaryStage.show();
    }

    public void showAccountCreationView() {
        AccountCreationView accountView = new AccountCreationView(accountController);
        primaryStage.setScene(accountView.getScene());
        primaryStage.setTitle("Create Bank Account");
    }

    public void showDashboardView(String customerId) {
        Customer customer = bank.getCustomer(customerId);
        if (customer != null) {
            dashboardController.setCurrentCustomer(customerId);
            DashboardView dashboardView = new DashboardView(dashboardController, customer.getFullName(), customerId);
            dashboardView.displayAccounts(dashboardController.getCustomerAccountsInfo());
            dashboardView.displayTransactions(dashboardController.getCustomerTransactionsInfo());
            primaryStage.setScene(dashboardView.getScene());
            primaryStage.setTitle("Banking System - Dashboard - " + customer.getFullName());
        } else if ("admin".equals(customerId)) {
            dashboardController.setCurrentCustomer("admin");
            DashboardView dashboardView = new DashboardView(dashboardController, "Administrator", "admin");
            dashboardView.displayAccounts(getAllAccountsInfo());
            primaryStage.setScene(dashboardView.getScene());
            primaryStage.setTitle("Banking System - Admin Dashboard");
        } else {
            System.out.println("Customer not found: " + customerId);
            showLoginViewWithMessage("Customer ID not found. Please try again.");
        }
    }

    public void showDepositView(String customerId) {
        transactionController.setCurrentCustomer(customerId);
        DepositView depositView = new DepositView(transactionController, customerId);
        primaryStage.setScene(depositView.getScene());
        primaryStage.setTitle("Deposit Funds - " + getCustomerName(customerId));
    }

    public void showDepositViewWithMessage(String message, boolean isSuccess) {
        String customerId = transactionController.getCurrentCustomerId();
        DepositView depositView = new DepositView(transactionController, customerId);
        depositView.setMessage(message, isSuccess);
        primaryStage.setScene(depositView.getScene());
        primaryStage.setTitle("Deposit Funds - " + getCustomerName(customerId));
    }

    public void showWithdrawalView(String customerId) {
        transactionController.setCurrentCustomer(customerId);
        WithdrawalView withdrawalView = new WithdrawalView(transactionController, customerId);
        primaryStage.setScene(withdrawalView.getScene());
        primaryStage.setTitle("Withdraw Funds - " + getCustomerName(customerId));
    }

    public void showWithdrawalViewWithMessage(String message, boolean isSuccess) {
        String customerId = transactionController.getCurrentCustomerId();
        WithdrawalView withdrawalView = new WithdrawalView(transactionController, customerId);
        withdrawalView.setMessage(message, isSuccess);
        primaryStage.setScene(withdrawalView.getScene());
        primaryStage.setTitle("Withdraw Funds - " + getCustomerName(customerId));
    }

    private String getCustomerName(String customerId) {
        Customer customer = bank.getCustomer(customerId);
        return customer != null ? customer.getFullName() : "Unknown Customer";
    }

    private String getAllAccountsInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("ADMIN VIEW - ALL ACCOUNTS\n");
        sb.append("========================\n\n");

        if (bank.getAllCustomers().isEmpty()) {
            sb.append("No customers found in the system.\n");
            sb.append("Create accounts using the 'Create New Account' feature.\n");
        } else {
            sb.append("EXISTING CUSTOMERS:\n");
            sb.append("-------------------\n");
            for (Customer customer : bank.getAllCustomers()) {
                sb.append("ID: ").append(customer.getCustomerId())
                        .append(" - ").append(customer.getFullName())
                        .append(" (").append(customer.getCustomerType()).append(")\n");
            }
            sb.append("\nACCOUNT DETAILS:\n");
            sb.append("----------------\n");

            for (Customer customer : bank.getAllCustomers()) {
                sb.append("\n").append(customer.getFullName())
                        .append(" (").append(customer.getCustomerId()).append("):\n");

                java.util.List<Account> accounts = bank.getCustomerAccounts(customer.getCustomerId());
                if (accounts.isEmpty()) {
                    sb.append("  No accounts\n");
                } else {
                    for (Account account : accounts) {
                        sb.append("  - ").append(account.getAccountType())
                                .append(": ").append(account.getAccountNumber())
                                .append(" | Balance: BWP ").append(String.format("%.2f", account.getBalance()))
                                .append(" | Branch: ").append(account.getBranch())
                                .append("\n");
                    }
                }
            }
        }

        return sb.toString();
    }

    public void displayAllCustomerIDs() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("AVAILABLE CUSTOMER IDs FOR LOGIN");
        System.out.println("=".repeat(50));

        if (bank.getAllCustomers().isEmpty()) {
            System.out.println("No customers found. Create accounts first!");
        } else {
            for (Customer customer : bank.getAllCustomers()) {
                System.out.println("ID: " + customer.getCustomerId() +
                        " - " + customer.getFullName() +
                        " (" + customer.getCustomerType() + ")");
            }
        }
        System.out.println("Admin: admin / admin");
        System.out.println("=".repeat(50) + "\n");
    }
}