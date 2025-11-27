public class SavingsAccount extends Account implements InterestBearing {
    private static final double MONTHLY_INTEREST_RATE = 0.0005; // 0.05%
    private static final double MINIMUM_FIRST_DEPOSIT = 500.0; // 500 BWP
    private boolean firstDepositMade = false;

    public SavingsAccount(String accountNumber, double initialBalance, String branch, Customer customer) {
        super(accountNumber, initialBalance, branch, customer);
        System.out.println("💰 Created Savings Account: " + accountNumber +
                " for " + customer.getFullName() + " with initial balance: BWP " + initialBalance);

        // If initial balance is provided during creation, validate it meets minimum
        if (initialBalance > 0 && initialBalance < MINIMUM_FIRST_DEPOSIT) {
            throw new IllegalArgumentException("❌ First deposit must be at least BWP " +
                    MINIMUM_FIRST_DEPOSIT + ". Provided: BWP " + initialBalance);
        }

        // Mark first deposit as made if initial balance meets requirement
        if (initialBalance >= MINIMUM_FIRST_DEPOSIT) {
            firstDepositMade = true;
            System.out.println("✅ Minimum first deposit requirement satisfied.");
        }
    }

    @Override
    public void deposit(double amount) {
        // Check if this is the first deposit and validate minimum requirement
        if (!firstDepositMade) {
            if (amount < MINIMUM_FIRST_DEPOSIT) {
                throw new IllegalArgumentException("❌ First deposit to savings account must be at least BWP " +
                        MINIMUM_FIRST_DEPOSIT + ". Attempted: BWP " + amount);
            }
            firstDepositMade = true;
            System.out.println("✅ Minimum first deposit requirement satisfied with deposit of BWP " + amount);
        }

        super.deposit(amount);
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
        if (interest > 0) {
            deposit(interest);
            System.out.println("💹 Interest applied to Savings Account " + getAccountNumber() +
                    ": BWP " + String.format("%.4f", interest));
        }
    }

    // Helper method to check if minimum deposit requirement is met
    public boolean isMinimumDepositMet() {
        return firstDepositMade;
    }

    // Getter for minimum deposit requirement
    public static double getMinimumFirstDeposit() {
        return MINIMUM_FIRST_DEPOSIT;
    }

    // Method to get requirement status
    public String getRequirementStatus() {
        if (firstDepositMade) {
            return "✅ Minimum deposit requirement satisfied";
        } else {
            return "⚠️  Pending: First deposit of BWP " + MINIMUM_FIRST_DEPOSIT + " required";
        }
    }
}