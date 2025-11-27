public class DashboardController {
    private BankController bankController;
    private Bank bank;
    private String currentCustomerId;

    public DashboardController(BankController bankController, Bank bank) {
        this.bankController = bankController;
        this.bank = bank;
    }

    public void setCurrentCustomer(String customerId) {
        this.currentCustomerId = customerId;
    }

    public void handleLogout() {
        bankController.showLoginView();
    }

    public void handleRefresh() {
        System.out.println("Refreshing dashboard for customer: " + currentCustomerId);
    }

    public void handleAddAccount() {
        bankController.showAccountCreationView();
    }

    // NEW METHODS FOR DEPOSIT AND WITHDRAWAL
    public void handleDeposit() {
        bankController.showDepositView(currentCustomerId);
    }

    public void handleWithdraw() {
        bankController.showWithdrawalView(currentCustomerId);
    }

    public void handleApplyInterest() {
        bank.applyMonthlyInterest();
        System.out.println("Monthly interest applied to all accounts");
    }

    public String getCustomerAccountsInfo() {
        StringBuilder sb = new StringBuilder();
        Customer customer = bank.getCustomer(currentCustomerId);

        if (customer != null) {
            sb.append("Customer: ").append(customer.getFullName()).append("\n");
            sb.append("Customer ID: ").append(customer.getCustomerId()).append("\n");
            sb.append("Customer Type: ").append(customer.getCustomerType()).append("\n");
            sb.append("Address: ").append(customer.getAddress()).append("\n\n");
            sb.append("ACCOUNTS:\n");
            sb.append("=========\n");

            java.util.List<Account> accounts = bank.getCustomerAccounts(currentCustomerId);
            if (accounts.isEmpty()) {
                sb.append("No accounts found.\n");
            } else {
                for (Account account : accounts) {
                    sb.append("• ").append(account.getAccountType())
                            .append(" Account: ").append(account.getAccountNumber())
                            .append("\n   Balance: BWP ").append(String.format("%.2f", account.getBalance()))
                            .append("\n   Branch: ").append(account.getBranch())
                            .append("\n   Status: ").append(account.isActive() ? "Active" : "Inactive")
                            .append("\n\n");
                }
            }
        } else {
            sb.append("Customer not found: ").append(currentCustomerId);
        }

        return sb.toString();
    }

    public String getCustomerTransactionsInfo() {
        StringBuilder sb = new StringBuilder();
        java.util.List<Account> accounts = bank.getCustomerAccounts(currentCustomerId);

        sb.append("RECENT TRANSACTIONS:\n");
        sb.append("===================\n");

        for (Account account : accounts) {
            java.util.List<Transaction> transactions = bank.getAccountTransactions(account.getAccountNumber());
            if (!transactions.isEmpty()) {
                sb.append(account.getAccountType()).append(" (").append(account.getAccountNumber()).append("):\n");
                for (Transaction transaction : transactions) {
                    sb.append("  - ").append(transaction.getType())
                            .append(": BWP ").append(String.format("%.2f", transaction.getAmount()))
                            .append(" - ").append(transaction.getTimestamp())
                            .append("\n");
                }
                sb.append("\n");
            }
        }

        if (sb.toString().equals("RECENT TRANSACTIONS:\n===================\n")) {
            sb.append("No transactions found.\n");
        }

        return sb.toString();
    }
}