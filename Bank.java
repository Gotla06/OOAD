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

        // Initialize counters from database
        this.customerCounter = DatabaseManager.getCustomerCount() + 1;
        this.accountCounter = DatabaseManager.getAccountCount() + 1;

        loadDataFromDatabase();
    }

    // Load data from database
    private void loadDataFromDatabase() {
        System.out.println("=== LOADING DATA FROM DATABASE ===");

        // Load customers
        List<Customer> dbCustomers = DatabaseManager.loadAllCustomers();
        for (Customer customer : dbCustomers) {
            customers.put(customer.getCustomerId(), customer);
        }

        // Load accounts
        List<Account> dbAccounts = DatabaseManager.loadAllAccounts(dbCustomers);
        for (Account account : dbAccounts) {
            accounts.put(account.getAccountNumber(), account);
        }

        // Load transactions
        List<Transaction> dbTransactions = DatabaseManager.loadAllTransactions();
        for (Transaction transaction : dbTransactions) {
            String accountNumber = transaction.getAccountNumber();
            transactionHistory.computeIfAbsent(accountNumber, k -> new ArrayList<>()).add(transaction);
        }

        System.out.println("✅ Data loaded: " + customers.size() + " customers, " + accounts.size() + " accounts");

        // Update counters based on loaded data
        updateCounters();
    }

    // Update counters based on loaded data
    private void updateCounters() {
        // Find highest customer ID
        int maxCustomerId = 0;
        for (String customerId : customers.keySet()) {
            if (customerId.startsWith("CUST")) {
                try {
                    int idNum = Integer.parseInt(customerId.substring(4));
                    if (idNum > maxCustomerId) {
                        maxCustomerId = idNum;
                    }
                } catch (NumberFormatException e) {
                    // Ignore if format is different
                }
            }
        }
        customerCounter = maxCustomerId + 1;

        // Find highest account number
        int maxAccountNum = 0;
        for (String accountNum : accounts.keySet()) {
            if (accountNum.startsWith("ACC")) {
                try {
                    int accNum = Integer.parseInt(accountNum.substring(3));
                    if (accNum > maxAccountNum) {
                        maxAccountNum = accNum;
                    }
                } catch (NumberFormatException e) {
                    // Ignore if format is different
                }
            }
        }
        accountCounter = maxAccountNum + 1;
    }

    // Customer management
    public void addCustomer(Customer customer) {
        customers.put(customer.getCustomerId(), customer);
        // Save to database
        DatabaseManager.saveCustomer(customer);
        System.out.println("✅ Customer added: " + customer.getFullName() + " (" + customer.getCustomerId() + ")");
    }

    public Customer getCustomer(String customerId) {
        return customers.get(customerId);
    }

    public List<Customer> getAllCustomers() {
        return new ArrayList<>(customers.values());
    }

    // Account management
    public boolean openAccount(Account account) {
        System.out.println("🔧 Attempting to open account: " + account.getAccountType() +
                " for customer: " + account.getCustomer().getFullName());

        // Validate investment account minimum balance
        if (account instanceof InvestmentAccount && account.getBalance() < InvestmentAccount.getMinOpeningBalance()) {
            System.out.println("❌ Investment account requires minimum BWP 500.00 opening balance");
            return false;
        }

        // Validate cheque account employment
        if (account instanceof ChequeAccount && account.getCustomer() instanceof IndividualCustomer) {
            IndividualCustomer individual = (IndividualCustomer) account.getCustomer();
            if (!individual.canOpenAccount(AccountType.CHEQUE)) {
                System.out.println("❌ Individual customer must be employed to open a cheque account");
                return false;
            }
        }

        accounts.put(account.getAccountNumber(), account);
        // Save to database
        DatabaseManager.saveAccount(account);
        System.out.println("✅ Successfully opened " + account.getAccountType() + " account: " + account.getAccountNumber());
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
        if (account != null) {
            // Check if account implements Withdrawable interface
            if (account instanceof Withdrawable) {
                Withdrawable withdrawableAccount = (Withdrawable) account;
                if (withdrawableAccount.withdraw(amount)) {
                    recordTransaction(accountNumber, "WITHDRAWAL", amount, "Withdrawal from account");
                    return true;
                }
            } else {
                // Account doesn't allow withdrawals (like Savings)
                System.out.println("❌ Withdrawals not allowed from " + account.getAccountType() + " accounts");
                return false;
            }
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
        // Save to database
        DatabaseManager.saveTransaction(transaction);
    }

    public List<Transaction> getAccountTransactions(String accountNumber) {
        return transactionHistory.getOrDefault(accountNumber, new ArrayList<>());
    }

    // Generate unique IDs
    public String generateCustomerId() {
        String customerId;
        do {
            customerId = "CUST" + (customerCounter++);
        } while (DatabaseManager.customerExists(customerId));
        return customerId;
    }

    public String generateAccountNumber() {
        return "ACC" + (accountCounter++);
    }

    // Debug method to print all accounts
    public void debugPrintAllAccounts() {
        System.out.println("\n🔍 ALL ACCOUNTS IN SYSTEM");
        System.out.println("=" .repeat(60));
        if (accounts.isEmpty()) {
            System.out.println("No accounts found. Create some accounts first!");
        } else {
            for (Account account : accounts.values()) {
                System.out.println("Account: " + account.getAccountNumber() +
                        " | Type: " + account.getAccountType() +
                        " | Customer: " + account.getCustomer().getFullName() +
                        " | Balance: BWP " + account.getBalance());
            }
        }
        System.out.println("=" .repeat(60) + "\n");
    }

    // Getters
    public Map<String, Customer> getCustomers() {
        return new HashMap<>(customers);
    }

    public Map<String, Account> getAccounts() {
        return new HashMap<>(accounts);
    }
}