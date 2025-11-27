import java.util.List;

public class TransactionController {
    private BankController bankController;
    private Bank bank;
    private String currentCustomerId;

    public TransactionController(BankController bankController, Bank bank) {
        this.bankController = bankController;
        this.bank = bank;
    }

    public void setCurrentCustomer(String customerId) {
        this.currentCustomerId = customerId;
    }

    public String getCurrentCustomerId() {
        return currentCustomerId;
    }

    public void handleDeposit(String accountNumber, double amount) {
        try {
            if (amount <= 0) {
                bankController.showDepositViewWithMessage("Deposit amount must be greater than zero", false);
                return;
            }

            boolean success = bank.deposit(accountNumber, amount);

            if (success) {
                String message = String.format("Successfully deposited BWP %.2f to account %s", amount, accountNumber);
                bankController.showDepositViewWithMessage(message, true);
            } else {
                bankController.showDepositViewWithMessage("Deposit failed. Please try again.", false);
            }
        } catch (Exception e) {
            bankController.showDepositViewWithMessage("Error during deposit: " + e.getMessage(), false);
        }
    }

    public void handleWithdraw(String accountNumber, double amount) {
        try {
            if (amount <= 0) {
                bankController.showWithdrawalViewWithMessage("Withdrawal amount must be greater than zero", false);
                return;
            }

            Account account = bank.getAccount(accountNumber);
            if (account == null) {
                bankController.showWithdrawalViewWithMessage("Account not found", false);
                return;
            }

            // Check if account allows withdrawals
            if (account.getAccountType().equals("Savings")) {
                bankController.showWithdrawalViewWithMessage("Withdrawals not allowed from Savings accounts", false);
                return;
            }

            // Check sufficient funds
            if (amount > account.getBalance()) {
                bankController.showWithdrawalViewWithMessage("Insufficient funds for withdrawal", false);
                return;
            }

            boolean success = bank.withdraw(accountNumber, amount);

            if (success) {
                String message = String.format("Successfully withdrew BWP %.2f from account %s", amount, accountNumber);
                bankController.showWithdrawalViewWithMessage(message, true);
            } else {
                bankController.showWithdrawalViewWithMessage("Withdrawal failed. Please try again.", false);
            }
        } catch (Exception e) {
            bankController.showWithdrawalViewWithMessage("Error during withdrawal: " + e.getMessage(), false);
        }
    }

    public void handleBackToDashboard() {
        bankController.showDashboardView(currentCustomerId);
    }

    public List<Account> getCustomerAccounts(String customerId) {
        return bank.getCustomerAccounts(customerId);
    }

    public Account getAccount(String accountNumber) {
        return bank.getAccount(accountNumber);
    }
}