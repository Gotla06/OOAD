import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private static final String URL = "jdbc:h2:mem:bankdb;DB_CLOSE_DELAY=-1";
    private static final String USER = "sa";
    private static final String PASSWORD = "";

    static {
        initializeDatabase();
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    private static void initializeDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            // Create tables
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS customers (
                    customer_id VARCHAR(50) PRIMARY KEY,
                    first_name VARCHAR(100),
                    last_name VARCHAR(100),
                    address VARCHAR(255),
                    customer_type VARCHAR(20),
                    id_number VARCHAR(50),
                    date_of_birth DATE,
                    employed BOOLEAN,
                    employer_name VARCHAR(100),
                    employer_address VARCHAR(255),
                    company_name VARCHAR(100),
                    registration_number VARCHAR(50),
                    contact_person VARCHAR(100),
                    registration_date TIMESTAMP
                )
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS accounts (
                    account_number VARCHAR(50) PRIMARY KEY,
                    customer_id VARCHAR(50),
                    account_type VARCHAR(20),
                    balance DECIMAL(15,2),
                    branch VARCHAR(100),
                    opening_date TIMESTAMP,
                    employer_name VARCHAR(100),
                    employer_address VARCHAR(255),
                    FOREIGN KEY (customer_id) REFERENCES customers(customer_id)
                )
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS transactions (
                    transaction_id VARCHAR(50) PRIMARY KEY,
                    account_number VARCHAR(50),
                    transaction_type VARCHAR(20),
                    amount DECIMAL(15,2),
                    description VARCHAR(255),
                    transaction_date TIMESTAMP,
                    FOREIGN KEY (account_number) REFERENCES accounts(account_number)
                )
            """);

            System.out.println("✅ Database tables created successfully");

        } catch (SQLException e) {
            System.err.println("❌ Database initialization failed: " + e.getMessage());
        }
    }

    // Save customer to database
    public static boolean saveCustomer(Customer customer) {
        String sql = """
            INSERT INTO customers (customer_id, first_name, last_name, address, customer_type, 
                                 id_number, date_of_birth, employed, employer_name, employer_address,
                                 company_name, registration_number, contact_person, registration_date) 
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, customer.getCustomerId());
            pstmt.setString(2, customer.getFirstName());
            pstmt.setString(3, customer.getSurname());
            pstmt.setString(4, customer.getAddress());
            pstmt.setString(5, customer.getCustomerType());

            if (customer instanceof IndividualCustomer) {
                IndividualCustomer individual = (IndividualCustomer) customer;
                pstmt.setString(6, individual.getIdNumber());
                pstmt.setDate(7, new java.sql.Date(individual.getDateOfBirth().getTime()));
                pstmt.setBoolean(8, individual.isEmployed());
                pstmt.setString(9, individual.getEmployerName());
                pstmt.setString(10, individual.getEmployerAddress());
                pstmt.setString(11, null);
                pstmt.setString(12, null);
                pstmt.setString(13, null);
            } else {
                CompanyCustomer company = (CompanyCustomer) customer;
                pstmt.setString(6, null);
                pstmt.setDate(7, null);
                pstmt.setBoolean(8, false);
                pstmt.setString(9, null);
                pstmt.setString(10, null);
                pstmt.setString(11, company.getFullName());
                pstmt.setString(12, company.getRegistrationNumber());
                pstmt.setString(13, company.getContactPerson());
            }

            pstmt.setTimestamp(14, new Timestamp(customer.getRegistrationDate().getTime()));

            int rowsAffected = pstmt.executeUpdate();
            System.out.println("✅ Customer saved to database: " + customer.getCustomerId());
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("❌ Error saving customer to database: " + e.getMessage());
            return false;
        }
    }

    // Save account to database
    public static boolean saveAccount(Account account) {
        String sql = """
            INSERT INTO accounts (account_number, customer_id, account_type, balance, branch, opening_date, employer_name, employer_address) 
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, account.getAccountNumber());
            pstmt.setString(2, account.getCustomer().getCustomerId());
            pstmt.setString(3, account.getAccountType());
            pstmt.setDouble(4, account.getBalance());
            pstmt.setString(5, account.getBranch());
            pstmt.setTimestamp(6, new Timestamp(account.getOpeningDate().getTime()));

            if (account instanceof ChequeAccount) {
                ChequeAccount cheque = (ChequeAccount) account;
                pstmt.setString(7, cheque.getEmployerName());
                pstmt.setString(8, cheque.getEmployerAddress());
            } else {
                pstmt.setString(7, null);
                pstmt.setString(8, null);
            }

            int rowsAffected = pstmt.executeUpdate();
            System.out.println("✅ Account saved to database: " + account.getAccountNumber() + " (" + account.getAccountType() + ")");
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("❌ Error saving account to database: " + e.getMessage());
            return false;
        }
    }

    // Save transaction to database
    public static boolean saveTransaction(Transaction transaction) {
        String sql = """
            INSERT INTO transactions (transaction_id, account_number, transaction_type, amount, description, transaction_date) 
            VALUES (?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, transaction.getTransactionId());
            pstmt.setString(2, transaction.getAccountNumber());
            pstmt.setString(3, transaction.getType());
            pstmt.setDouble(4, transaction.getAmount());
            pstmt.setString(5, transaction.getDescription());
            pstmt.setTimestamp(6, new Timestamp(transaction.getTimestamp().getTime()));

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("❌ Error saving transaction to database: " + e.getMessage());
            return false;
        }
    }

    // Load all customers from database
    public static List<Customer> loadAllCustomers() {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT * FROM customers";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String customerType = rs.getString("customer_type");
                Customer customer;

                if ("Individual".equals(customerType)) {
                    customer = new IndividualCustomer(
                            rs.getString("customer_id"),
                            rs.getString("first_name"),
                            rs.getString("last_name"),
                            rs.getString("address"),
                            rs.getString("id_number"),
                            rs.getDate("date_of_birth"),
                            rs.getBoolean("employed"),
                            rs.getString("employer_name"),
                            rs.getString("employer_address")
                    );
                } else {
                    customer = new CompanyCustomer(
                            rs.getString("customer_id"),
                            rs.getString("company_name"),
                            rs.getString("registration_number"),
                            rs.getString("address"),
                            rs.getString("contact_person")
                    );
                }
                customers.add(customer);
            }
            System.out.println("✅ Loaded " + customers.size() + " customers from database");

        } catch (SQLException e) {
            System.err.println("❌ Error loading customers from database: " + e.getMessage());
        }
        return customers;
    }

    // Load all accounts from database
    public static List<Account> loadAllAccounts(List<Customer> customers) {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT * FROM accounts";

        // Create customer map for quick lookup
        java.util.Map<String, Customer> customerMap = new java.util.HashMap<>();
        for (Customer customer : customers) {
            customerMap.put(customer.getCustomerId(), customer);
        }

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String accountNumber = rs.getString("account_number");
                String customerId = rs.getString("customer_id");
                String accountType = rs.getString("account_type");
                double balance = rs.getDouble("balance");
                String branch = rs.getString("branch");

                Customer customer = customerMap.get(customerId);
                if (customer != null) {
                    Account account;

                    switch (accountType) {
                        case "Savings":
                            account = new SavingsAccount(accountNumber, balance, branch, customer);
                            break;
                        case "Investment":
                            account = new InvestmentAccount(accountNumber, balance, branch, customer);
                            break;
                        case "Cheque":
                            String employerName = rs.getString("employer_name");
                            String employerAddress = rs.getString("employer_address");
                            account = new ChequeAccount(accountNumber, balance, branch, customer, employerName, employerAddress);
                            break;
                        default:
                            continue; // Skip unknown account types
                    }

                    accounts.add(account);
                }
            }
            System.out.println("✅ Loaded " + accounts.size() + " accounts from database");

        } catch (SQLException e) {
            System.err.println("❌ Error loading accounts from database: " + e.getMessage());
        }
        return accounts;
    }

    // Load all transactions from database
    public static List<Transaction> loadAllTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Transaction transaction = new Transaction(
                        rs.getString("account_number"),
                        rs.getString("transaction_type"),
                        rs.getDouble("amount"),
                        rs.getString("description")
                );
                transactions.add(transaction);
            }
            System.out.println("✅ Loaded " + transactions.size() + " transactions from database");

        } catch (SQLException e) {
            System.err.println("❌ Error loading transactions from database: " + e.getMessage());
        }
        return transactions;
    }

    // Check if customer exists in database
    public static boolean customerExists(String customerId) {
        String sql = "SELECT COUNT(*) FROM customers WHERE customer_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, customerId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            System.err.println("❌ Error checking customer existence: " + e.getMessage());
        }
        return false;
    }

    // Get total customer count from database
    public static int getCustomerCount() {
        String sql = "SELECT COUNT(*) FROM customers";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.err.println("❌ Error getting customer count: " + e.getMessage());
        }
        return 0;
    }

    // Get total account count from database
    public static int getAccountCount() {
        String sql = "SELECT COUNT(*) FROM accounts";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.err.println("❌ Error getting account count: " + e.getMessage());
        }
        return 0;
    }

    // Clear all data from database (for testing)
    public static void clearAllData() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute("DELETE FROM transactions");
            stmt.execute("DELETE FROM accounts");
            stmt.execute("DELETE FROM customers");

            System.out.println("✅ All data cleared from database");

        } catch (SQLException e) {
            System.err.println("❌ Error clearing database: " + e.getMessage());
        }
    }
}