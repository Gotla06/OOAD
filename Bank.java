package banking;
import banking.entities.*;
import banking.interfaces.InterestBearing;
import java.util.ArrayList;
import java.util.List;

public class Bank {
    private String name;
    private List<Customer> customers;
    private List<Account> accounts;

    public Bank(String name) {
        this.name = name;
        this.customers = new ArrayList<>();
        this.accounts = new ArrayList<>();
    }

    public Customer createCustomer(String firstName, String surname, String address) {
        Customer customer = new Customer(firstName, surname, address);
        customers.add(customer);
        return customer;
    }

    public Account openSavingsAccount(Customer customer, String accNum, double deposit, String branch) {
        Account account = new SavingsAccount(accNum, deposit, branch, customer);
        customer.addAccount(account);
        accounts.add(account);
        return account;
    }

    public Account openInvestmentAccount(Customer customer, String accNum, double deposit, String branch) {
        if (deposit < 500) throw new IllegalArgumentException("Minimum BWP 500 required");
        Account account = new InvestmentAccount(accNum, deposit, branch, customer);
        customer.addAccount(account);
        accounts.add(account);
        return account;
    }

    public Account openChequeAccount(Customer customer, String accNum, double deposit,
                                     String branch, String employer, String companyAddress) {
        Account account = new ChequeAccount(accNum, deposit, branch, customer, employer, companyAddress);
        customer.addAccount(account);
        accounts.add(account);
        return account;
    }

    public void applyMonthlyInterest() {
        for (Account account : accounts) {
            if (account instanceof InterestBearing) {
                ((InterestBearing) account).applyMonthlyInterest();
            }
        }
    }

    // Getters
    public List<Customer> getCustomers() { return new ArrayList<>(customers); }
    public List<Account> getAccounts() { return new ArrayList<>(accounts); }
    public String getName() { return name; }
}