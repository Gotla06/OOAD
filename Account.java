import java.util.Date;

public abstract class Account {
    private String accountNumber;
    private double balance;
    private String branch;
    private Customer customer;
    private Date openingDate;
    private boolean active;

    public Account(String accountNumber, double balance, String branch, Customer customer) {
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.branch = branch;
        this.customer = customer;
        this.openingDate = new Date();
        this.active = true;
    }

    // Common method for all accounts - DEPOSIT
    public void deposit(double amount) {
        if (amount > 0) {
            balance += amount;
            System.out.println("Deposited: BWP " + amount + " | Account: " + accountNumber +
                    " | New Balance: BWP " + balance);
        } else {
            System.out.println("Invalid deposit amount: " + amount);
        }
    }

    public double getBalance() {
        return balance;
    }

    protected void setBalance(double balance) {
        this.balance = balance;
    }

    // Abstract methods to be implemented by subclasses
    public abstract String getAccountType();
    public abstract boolean canCloseAccount();

    // Getters
    public String getAccountNumber() { return accountNumber; }
    public Customer getCustomer() { return customer; }
    public Date getOpeningDate() { return openingDate; }
    public boolean isActive() { return active; }
    public String getBranch() { return branch; }

    public void setActive(boolean active) { this.active = active; }

    @Override
    public String toString() {
        return "Account{" +
                "accountNumber='" + accountNumber + '\'' +
                ", type='" + getAccountType() + '\'' +
                ", balance=BWP " + balance +
                ", customer=" + customer.getFullName() +
                '}';
    }
}
