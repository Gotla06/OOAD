package banking.entities;

public class ChequeAccount extends Account {
    private String employer;
    private String companyAddress;

    public ChequeAccount(String accountNumber, double balance, String branch,
                         Customer customer, String employer, String companyAddress) {
        super(accountNumber, balance, branch, customer);
        this.employer = employer;
        this.companyAddress = companyAddress;
    }

    @Override
    public void withdraw(double amount) {
        if (amount > 0 && amount <= balance) {
            balance -= amount;
        }
    }

    public String getEmployer() { return employer; }
    public String getCompanyAddress() { return companyAddress; }
}
