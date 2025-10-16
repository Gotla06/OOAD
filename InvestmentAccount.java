public class InvestmentAccount extends Account implements InterestBearing, Withdrawable {
    private static final double MONTHLY_INTEREST_RATE = 0.05; // 5%
    private static final double MIN_OPENING_BALANCE = 500.00;

    public InvestmentAccount(String accountNumber, double initialBalance, String branch, Customer customer) {
        super(accountNumber, initialBalance, branch, customer);
        if (initialBalance < MIN_OPENING_BALANCE) {
            throw new IllegalArgumentException("Minimum opening balance for Investment account is BWP " + MIN_OPENING_BALANCE);
        }
    }

    @Override
    public String getAccountType() {
        return "Investment";
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
        System.out.println("Interest applied to Investment Account " + getAccountNumber() +
                ": BWP " + interest);
    }

    @Override
    public boolean withdrawable(double amount) {
        return false;
    }

    @Override
    public boolean withdraw(double amount) {
        if (amount > 0 && amount <= getBalance()) {
            setBalance(getBalance() - amount);
            System.out.println("Withdrawn from Investment Account " + getAccountNumber() +
                    ": BWP " + amount + " | New Balance: BWP " + getBalance());
            return true;
        }
        System.out.println("Withdrawal failed from Investment Account " + getAccountNumber() +
                ": Insufficient funds or invalid amount");
        return false;
    }
}
