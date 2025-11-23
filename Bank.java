import java.util.*;

public class Bank {
    private Map<String, Customer> customers;
    private Map<String, Account> accounts;
    private Map<String, List<Transaction>> transactionHistory;
    private int customerCounter;
    private int accountCounter;

    public Bank() {
        this.customers = new HashMap<>();
        this.accounts = new HashMap<>();
        this.transactionHistory = new HashMap<>();
        this.customerCounter = 1;
        this.accountCounter = 1;
        initializeSampleData();
    }

    // Customer management
    public void addCustomer(Customer customer) {
        customers.put(customer.getCustomerId(), customer);
    }

    public Customer getCustomer(String customerId) {
        return customers.get(customerId);
    }

    public List<Customer> getAllCustomers() {
        return new ArrayList<>(customers.values());
    }

    // Account management
    public boolean openAccount(Account account) {
        // Validate investment account minimum balance
        if (account instanceof InvestmentAccount && account.getBalance() < InvestmentAccount.getMinOpeningBalance()) {
            System.out.println("Investment account requires minimum BWP 500.00 opening balance");
            return false;
        }

        // Validate cheque account employment
        if (account instanceof ChequeAccount && account.getCustomer() instanceof IndividualCustomer) {
            IndividualCustomer individual = (IndividualCustomer) account.getCustomer();
            if (!individual.canOpenAccount(AccountType.CHEQUE)) {
                System.out.println("Individual customer must be employed to open a cheque account");
                return false;
            }
        }

        accounts.put(account.getAccountNumber(), account);
        return true;
    }

    public Account getAccount(String accountNumber) {
        return accounts.get(accountNumber);
    }

    public List<Account> getCustomerAccounts(String customerId) {
        List<Account> customerAccounts = new ArrayList<>();
        for (Account account : accounts.values()) {
            if (account.getCustomer().getCustomerId().equals(customerId)) {
                customerAccounts.add(account);
            }
        }
        return customerAccounts;
    }

    public List<Account> getAllAccounts() {
        return new ArrayList<>(accounts.values());
    }

    // Check if customer can open additional account of specific type
    public boolean canOpenAdditionalAccount(String customerId, AccountType accountType) {
        Customer customer = getCustomer(customerId);
        if (customer == null) return false;

        // Check if customer already has this account type
        for (Account account : getCustomerAccounts(customerId)) {
            if (account.getAccountType().equals(accountType.toString())) {
                return false;
            }
        }

        return customer.canOpenAccount(accountType);
    }

    // Transaction methods
    public boolean deposit(String accountNumber, double amount) {
        Account account = accounts.get(accountNumber);
        if (account != null && amount > 0) {
            account.deposit(amount);
            recordTransaction(accountNumber, "DEPOSIT", amount, "Deposit to account");
            return true;
        }
        return false;
    }

    public boolean withdraw(String accountNumber, double amount) {
        Account account = accounts.get(accountNumber);
        if (account != null && account instanceof Withdrawable) {
            Withdrawable withdrawableAccount = (Withdrawable) account;
            if (withdrawableAccount.withdraw(amount)) {
                recordTransaction(accountNumber, "WITHDRAWAL", amount, "Withdrawal from account");
                return true;
            }
        } else if (account != null) {
            System.out.println("Withdrawals not allowed from " + account.getAccountType() + " account");
        }
        return false;
    }

    public void applyMonthlyInterest() {
        System.out.println("=== APPLYING MONTHLY INTEREST ===");
        for (Account account : accounts.values()) {
            if (account instanceof InterestBearing) {
                ((InterestBearing) account).applyMonthlyInterest();
            }
        }
    }

    // Transaction recording
    private void recordTransaction(String accountNumber, String type, double amount, String description) {
        Transaction transaction = new Transaction(accountNumber, type, amount, description);
        transactionHistory.computeIfAbsent(accountNumber, k -> new ArrayList<>()).add(transaction);
    }

    public List<Transaction> getAccountTransactions(String accountNumber) {
        return transactionHistory.getOrDefault(accountNumber, new ArrayList<>());
    }

    // Generate unique IDs
    public String generateCustomerId() {
        return "CUST" + (customerCounter++);
    }

    public String generateAccountNumber() {
        return "ACC" + (accountCounter++);
    }

    // Initialize sample data with 10+ records as required
    private void initializeSampleData() {
        System.out.println("=== INITIALIZING SAMPLE DATA ===");

        // Create 10 sample customers and accounts
        for (int i = 1; i <= 10; i++) {
            String customerId = "CUST" + i;

            if (i % 2 == 0) {
                // Individual customers (employed and unemployed)
                boolean employed = i % 4 == 0; // Every 4th customer is employed
                IndividualCustomer customer = new IndividualCustomer(
                        customerId,
                        "John" + i,
                        "Doe" + i,
                        "Address " + i + ", Gaborone",
                        "ID" + i,
                        new Date(),
                        employed,
                        employed ? "Company " + i : null,
                        employed ? "Business Address " + i : null
                );
                addCustomer(customer);

                // Create multiple accounts for some customers
                if (i % 3 == 0) {
                    // Customer with savings account
                    openAccount(new SavingsAccount("ACC" + i + "S", 1000 + i * 100, "Gaborone Main", customer));
                }
                if (i % 3 == 1 || i % 3 == 0) {
                    // Customer with investment account
                    openAccount(new InvestmentAccount("ACC" + i + "I", 500 + i * 50, "Gaborone Main", customer));
                }
                if (employed) {
                    // Employed customer with cheque account
                    openAccount(new ChequeAccount("ACC" + i + "C", 2000 + i * 100, "Gaborone Main", customer,
                            "Company " + i, "Business Address " + i));
                }
            } else {
                // Company customers
                CompanyCustomer company = new CompanyCustomer(
                        customerId,
                        "Company " + i + " Ltd",
                        "REG" + i,
                        "Business Address " + i + ", Gaborone",
                        "Contact Person " + i
                );
                addCustomer(company);

                // Companies can have any account type
                openAccount(new ChequeAccount("ACC" + i + "B", 5000 + i * 200, "Gaborone Main", company,
                        "Company " + i, "Business Address " + i));
                if (i % 3 == 0) {
                    openAccount(new InvestmentAccount("ACC" + i + "BI", 1000 + i * 100, "Gaborone Main", company));
                }
            }
        }

        System.out.println("✅ Sample data initialized: " + customers.size() + " customers, " + accounts.size() + " accounts");
    }

    // Getters
    public Map<String, Customer> getCustomers() {
        return new HashMap<>(customers);
    }

    public Map<String, Account> getAccounts() {
        return new HashMap<>(accounts);
    }
}