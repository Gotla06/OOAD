public class ChequeAccount extends Account implements Withdrawable {
    private String employerName;
    private String employerAddress;

    public ChequeAccount(String accountNumber, double initialBalance, String branch,
                         Customer customer, String employerName, String employerAddress) {
        super(accountNumber, initialBalance, branch, customer);
        this.employerName = employerName;
        this.employerAddress = employerAddress;

        // Validate employment for individual customers
        if (customer instanceof IndividualCustomer) {
            IndividualCustomer individual = (IndividualCustomer) customer;
            if (!individual.canOpenAccount(AccountType.CHEQUE)) {
                throw new IllegalArgumentException("Individual customer must be employed to open a cheque account");
            }
        }
        System.out.println("🏦 Created Cheque Account: " + accountNumber + " for " + customer.getFullName());
    }

    @Override
    public String getAccountType() {
        return "Cheque";
    }

    @Override
    public boolean canCloseAccount() {
        return true;
    }

    @Override
    public boolean withdraw(double amount) {
        if (amount > 0 && amount <= getBalance()) {
            setBalance(getBalance() - amount);
            System.out.println("💸 Withdrawn from Cheque Account " + getAccountNumber() +
                    ": BWP " + amount + " | New Balance: BWP " + getBalance());
            return true;
        }
        System.out.println("❌ Withdrawal failed from Cheque Account " + getAccountNumber() +
                ": Insufficient funds or invalid amount");
        return false;
    }

    // Getters
    public String getEmployerName() { return employerName; }
    public String getEmployerAddress() { return employerAddress; }

    @Override
    public String toString() {
        return "ChequeAccount{" +
                "accountNumber='" + getAccountNumber() + '\'' +
                ", balance=BWP " + getBalance() +
                ", employer='" + employerName + '\'' +
                '}';
    }
}