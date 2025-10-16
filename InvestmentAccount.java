package banking.entities;
import banking.interfaces.InterestBearing;

public class InvestmentAccount extends Account implements InterestBearing {
    private static final double INTEREST_RATE = 0.05;
    private static final double MIN_INITIAL_DEPOSIT = 500.0;

    public InvestmentAccount(String accountNumber, double balance, String branch, Customer customer) {
        super(accountNumber, balance, branch, customer);
        if (balance < MIN_INITIAL_DEPOSIT) {
            throw new IllegalArgumentException("Minimum deposit BWP 500 required");
        }
    }

    @Override
    public void withdraw(double amount) {
        if (amount > 0 && amount <= balance) {
            balance -= amount;
        }
    }

    @Override
    public void applyMonthlyInterest() {
        double interest = balance * INTEREST_RATE;
        balance += interest;
    }

    @Override
    public double getInterestRate() {
        return INTEREST_RATE;
    }
}