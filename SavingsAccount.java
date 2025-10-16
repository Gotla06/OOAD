public class SavingsAccount extends Account implements InterestBearing {
    private static final double MONTHLY_INTEREST_RATE = 0.0005; // 0.05%

    public SavingsAccount(String accountNumber, double initialBalance, String branch, Customer customer) {
        super(accountNumber, initialBalance, branch, customer);
    }

    @Override
    public String getAccountType() {
        return "Savings";
    }

    @Override
    public boolean canCloseAccount() {
        return true;
    }

    @Override
    public double calculateMonthlyInterest() {
        return getBalance() * MONTHLY_INTEREST_RATE;
    }

    @Override
    public void applyMonthlyInterest() {
        double interest = calculateMonthlyInterest();
        deposit(interest);
        System.out.println("Interest applied to Savings Account " + getAccountNumber() +
                ": BWP " + interest);
    }

    // Savings account does not allow withdrawals (as per requirements)
    public boolean withdraw(double amount) {
        System.out.println("Error: Withdrawals not allowed from Savings account " + getAccountNumber());
        return false;
    }
}