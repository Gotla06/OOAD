import java.util.ArrayList;
import java.util.List;

// Interface
interface BankingOperations {
    void deposit(double amount);
    boolean withdraw(double amount);
    double getBalance();
    String getAccountInfo();
}

// Abstract class
abstract class Account implements BankingOperations {
    private String accountNumber;
    private double balance;
    private String branch;
    private Customer customer;

    // Constructor
    public Account(String accountNumber, double balance, String branch, Customer customer) {
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.branch = branch;
        this.customer = customer;
    }

    // Getters and Setters
    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    // Implement BankingOperations methods
    @Override
    public void deposit(double amount) {
        if (amount > 0) {
            this.balance += amount;
        }
    }

    @Override
    public boolean withdraw(double amount) {
        if (amount > 0 && amount <= balance) {
            this.balance -= amount;
            return true;
        }
        return false;
    }

    @Override
    public String getAccountInfo() {
        return String.format("Account %s: Balance = $%.2f, Branch = %s",
                accountNumber, balance, branch);
    }

    // Abstract methods
    public abstract void payInterest();
    public abstract String getAccountType();
}

// SavingsAccount class
class SavingsAccount extends Account {
    private static final double MONTHLY_INTEREST_RATE = 0.0005;

    public SavingsAccount(String accountNumber, double balance, String branch, Customer customer) {
        super(accountNumber, balance, branch, customer);
    }

    @Override
    public void payInterest() {
        double interest = getBalance() * MONTHLY_INTEREST_RATE;
        deposit(interest);
    }

    @Override
    public String getAccountType() {
        return "Savings Account";
    }

    @Override
    public boolean withdraw(double amount) {
        // Savings account might have withdrawal restrictions
        if (amount > 0 && amount <= getBalance()) {
            setBalance(getBalance() - amount);
            return true;
        }
        return false;
    }
}

// InvestmentAccount class
class InvestmentAccount extends Account {
    private static final double MONTHLY_INTEREST_RATE = 0.05;
    private static final double MIN_OPENING_BALANCE = 500.00;

    public InvestmentAccount(String accountNumber, double balance, String branch, Customer customer) {
        super(accountNumber, balance, branch, customer);
    }

    public static double getMinOpeningBalance() {
        return MIN_OPENING_BALANCE;
    }

    @Override
    public void payInterest() {
        double interest = getBalance() * MONTHLY_INTEREST_RATE;
        deposit(interest);
    }

    @Override
    public String getAccountType() {
        return "Investment Account";
    }
}

// ChequeAccount class
class ChequeAccount extends Account {
    private String companyName;
    private String companyAddress;

    public ChequeAccount(String accountNumber, double balance, String branch,
                         Customer customer, String companyName, String companyAddress) {
        super(accountNumber, balance, branch, customer);
        this.companyName = companyName;
        this.companyAddress = companyAddress;
    }

    // Getters and Setters
    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyAddress() {
        return companyAddress;
    }

    public void setCompanyAddress(String companyAddress) {
        this.companyAddress = companyAddress;
    }

    @Override
    public void payInterest() {
        // Cheque accounts typically don't pay interest
        // Or might have a different interest calculation
    }

    @Override
    public String getAccountType() {
        return "Cheque Account";
    }

    @Override
    public String getAccountInfo() {
        return String.format("%s - Company: %s", super.getAccountInfo(), companyName);
    }
}

// Customer class
class Customer {
    private String customerId;
    private String firstName;
    private String surname;
    private String address;
    private List<Account> accounts;

    public Customer(String customerId, String firstName, String surname, String address) {
        this.customerId = customerId;
        this.firstName = firstName;
        this.surname = surname;
        this.address = address;
        this.accounts = new ArrayList<>();
    }

    // Getters and Setters
    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<Account> getAccounts() {
        return new ArrayList<>(accounts); // Return copy for encapsulation
    }

    // Business methods
    public void addAccount(Account account) {
        if (account != null && !accounts.contains(account)) {
            accounts.add(account);
        }
    }

    public Account getAccount(String accountNumber) {
        return accounts.stream()
                .filter(account -> account.getAccountNumber().equals(accountNumber))
                .findFirst()
                .orElse(null);
    }

    public String getFullName() {
        return firstName + " " + surname;
    }
}

// BankController class
class BankController {
    private List<Customer> customers;
    private int accountCounter;

    public BankController() {
        this.customers = new ArrayList<>();
        this.accountCounter = 1000; // Starting account number
    }

    // Getters
    public List<Customer> getCustomers() {
        return new ArrayList<>(customers);
    }

    public int getAccountCounter() {
        return accountCounter;
    }

    // Business methods
    public Customer registerCustomer(String customerId, String firstName, String surname, String address) {
        Customer customer = new Customer(customerId, firstName, surname, address);
        customers.add(customer);
        return customer;
    }

    public Account openSavingsAccount(Customer customer, double initialDeposit, String branch) {
        String accountNumber = "SAV" + (accountCounter++);
        Account account = new SavingsAccount(accountNumber, initialDeposit, branch, customer);
        customer.addAccount(account);
        return account;
    }

    public Account openInvestmentAccount(Customer customer, double initialDeposit, String branch) {
        if (initialDeposit < InvestmentAccount.getMinOpeningBalance()) {
            throw new IllegalArgumentException("Initial deposit must be at least $" +
                    InvestmentAccount.getMinOpeningBalance());
        }
        String accountNumber = "INV" + (accountCounter++);
        Account account = new InvestmentAccount(accountNumber, initialDeposit, branch, customer);
        customer.addAccount(account);
        return account;
    }

    public Account openChequeAccount(Customer customer, double initialDeposit, String branch,
                                     String companyName, String companyAddress) {
        String accountNumber = "CHQ" + (accountCounter++);
        Account account = new ChequeAccount(accountNumber, initialDeposit, branch,
                customer, companyName, companyAddress);
        customer.addAccount(account);
        return account;
    }

    public boolean depositToAccount(Customer customer, String accountNumber, double amount) {
        Account account = customer.getAccount(accountNumber);
        if (account != null && amount > 0) {
            account.deposit(amount);
            return true;
        }
        return false;
    }

    public boolean withdrawFromAccount(Customer customer, String accountNumber, double amount) {
        Account account = customer.getAccount(accountNumber);
        return account != null && account.withdraw(amount);
    }

    public void payMonthlyInterest(Customer customer) {
        for (Account account : customer.getAccounts()) {
            account.payInterest();
        }
    }
}

// CustomerDAO class (simplified version)
class CustomerDAO {
    private DatabaseConnection dbConnection;

    public CustomerDAO(DatabaseConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    public void saveCustomer(Customer customer) {
        // Implementation to save customer to database
        System.out.println("Saving customer: " + customer.getFullName());
    }

    public Customer findCustomerById(String customerId) {
        // Implementation to find customer by ID
        System.out.println("Finding customer with ID: " + customerId);
        return null; // Placeholder
    }

    public List<Customer> getAllCustomers() {
        // Implementation to get all customers
        System.out.println("Getting all customers");
        return new ArrayList<>(); // Placeholder
    }
}

// DatabaseConnection class (simplified version)
class DatabaseConnection {
    private String URL;
    private Object connection; // Using Object as placeholder for Connection

    public DatabaseConnection(String URL) {
        this.URL = URL;
    }

    // Getters and Setters
    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public Object getConnection() {
        return connection;
    }

    public void setConnection(Object connection) {
        this.connection = connection;
    }

    public void initializeDatabase() {
        System.out.println("Initializing database connection to: " + URL);
    }

    public void closeConnection() {
        System.out.println("Closing database connection");
    }
}