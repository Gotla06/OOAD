import java.util.*;
import java.text.SimpleDateFormat;

public class Bank {
    private List<Customer> customers;
    private List<Account> accounts;

    public Bank() {
        this.customers = new ArrayList<>();
        this.accounts = new ArrayList<>();
    }

    // Customer management
    public void addCustomer(Customer customer) {
        customers.add(customer);
        System.out.println("Customer added: " + customer.getFullName());
    }

    public Customer findCustomerById(String customerId) {
        return customers.stream()
                .filter(c -> c.getCustomerId().equals(customerId))
                .findFirst()
                .orElse(null);
    }

    // Account management
    public void openAccount(Account account) {
        accounts.add(account);
        System.out.println("Account opened: " + account.getAccountNumber() +
                " (" + account.getAccountType() + ")");
    }

    public List<Account> getCustomerAccounts(String customerId) {
        List<Account> customerAccounts = new ArrayList<>();
        for (Account account : accounts) {
            if (account.getCustomer().getCustomerId().equals(customerId)) {
                customerAccounts.add(account);
            }
        }
        return customerAccounts;
    }

    public Account findAccountByNumber(String accountNumber) {
        return accounts.stream()
                .filter(a -> a.getAccountNumber().equals(accountNumber))
                .findFirst()
                .orElse(null);
    }

    // Transaction methods
    public void depositToAccount(String accountNumber, double amount) {
        Account account = findAccountByNumber(accountNumber);
        if (account != null) {
            account.deposit(amount);
        } else {
            System.out.println("Account not found: " + accountNumber);
        }
    }

    public void withdrawFromAccount(String accountNumber, double amount) {
        Account account = findAccountByNumber(accountNumber);
        if (account instanceof Withdrawable) {
            ((Withdrawable) account).withdraw(amount);
        } else {
            System.out.println("Withdrawals not allowed for this account type");
        }
    }

    // Interest application
    public void applyMonthlyInterest() {
        System.out.println("\n=== APPLYING MONTHLY INTEREST ===");
        for (Account account : accounts) {
            if (account instanceof InterestBearing) {
                ((InterestBearing) account).applyMonthlyInterest();
            }
        }
    }

    // Display methods
    public void displayAllCustomers() {
        System.out.println("\n=== ALL CUSTOMERS ===");
        for (Customer customer : customers) {
            System.out.println(customer);
        }
    }

    public void displayAllAccounts() {
        System.out.println("\n=== ALL ACCOUNTS ===");
        for (Account account : accounts) {
            System.out.println(account);
        }
    }

    public void displayCustomerAccounts(String customerId) {
        Customer customer = findCustomerById(customerId);
        if (customer != null) {
            System.out.println("\n=== ACCOUNTS FOR " + customer.getFullName() + " ===");
            List<Account> customerAccounts = getCustomerAccounts(customerId);
            for (Account account : customerAccounts) {
                System.out.println(account);
            }
        } else {
            System.out.println("Customer not found: " + customerId);
        }
    }

    // Main method for testing
    public static void main(String[] args) throws Exception {
        Bank bank = new Bank();

        // Create date format for date of birth
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        // Create individual customers
        IndividualCustomer john = new IndividualCustomer("CUST001", "John", "Doe",
                "123 Main St, Gaborone", "19901234567", sdf.parse("1990-05-15"));
        IndividualCustomer mary = new IndividualCustomer("CUST002", "Mary", "Smith",
                "456 Broad St, Francistown", "19911234568", sdf.parse("1991-08-20"));

        // Create company customer
        CompanyCustomer abcCorp = new CompanyCustomer("CUST003", "ABC Corporation",
                "CORP123456", "789 Business Park, Gaborone", "Mr. James Brown");

        // Add customers to bank
        bank.addCustomer(john);
        bank.addCustomer(mary);
        bank.addCustomer(abcCorp);

        // Create accounts for John
        SavingsAccount johnSavings = new SavingsAccount("ACC001", 1000.00, "Gaborone Main", john);
        InvestmentAccount johnInvestment = new InvestmentAccount("ACC002", 500.00, "Gaborone Main", john);
        ChequeAccount johnCheque = new ChequeAccount("ACC003", 2000.00, "Gaborone Main", john,
                "Tech Solutions", "123 Tech Park, Gaborone");

        // Create accounts for Mary
        SavingsAccount marySavings = new SavingsAccount("ACC004", 1500.00, "Francistown", mary);
        InvestmentAccount maryInvestment = new InvestmentAccount("ACC005", 800.00, "Francistown", mary);

        // Create account for ABC Corporation
        ChequeAccount abcCheque = new ChequeAccount("ACC006", 5000.00, "Gaborone Main", abcCorp,
                "ABC Corporation", "789 Business Park, Gaborone");

        // Open accounts
        bank.openAccount(johnSavings);
        bank.openAccount(johnInvestment);
        bank.openAccount(johnCheque);
        bank.openAccount(marySavings);
        bank.openAccount(maryInvestment);
        bank.openAccount(abcCheque);

        // Display all customers and accounts
        bank.displayAllCustomers();
        bank.displayAllAccounts();

        // Test transactions
        System.out.println("\n=== TESTING TRANSACTIONS ===");

        // Deposits
        bank.depositToAccount("ACC001", 500.00);  // John's savings
        bank.depositToAccount("ACC006", 2000.00); // ABC Corp cheque

        // Withdrawals
        bank.withdrawFromAccount("ACC001", 200.00); // Should fail - savings account
        bank.withdrawFromAccount("ACC002", 200.00); // John's investment
        bank.withdrawFromAccount("ACC003", 500.00); // John's cheque
        bank.withdrawFromAccount("ACC006", 1000.00); // ABC Corp cheque

        // Apply monthly interest
        bank.applyMonthlyInterest();

        // Display customer accounts
        bank.displayCustomerAccounts("CUST001");
        bank.displayCustomerAccounts("CUST003");

        // Test account balances
        System.out.println("\n=== FINAL BALANCES ===");
        System.out.println("John's Savings: BWP " + johnSavings.getBalance());
        System.out.println("John's Investment: BWP " + johnInvestment.getBalance());
        System.out.println("John's Cheque: BWP " + johnCheque.getBalance());
        System.out.println("ABC Corp Cheque: BWP " + abcCheque.getBalance());
    }
}
