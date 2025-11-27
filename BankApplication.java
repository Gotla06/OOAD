import javafx.application.Application;
import javafx.stage.Stage;

public class BankApplication extends Application {
    private BankController bankController;

    public BankApplication() {
        // Initialize database connection first
        try {
            Class.forName("org.h2.Driver");
            System.out.println("✅ H2 Database Driver loaded successfully");
        } catch (ClassNotFoundException e) {
            System.err.println("❌ H2 Database Driver not found");
            e.printStackTrace();
        }

        this.bankController = new BankController();
    }

    @Override
    public void start(Stage primaryStage) {
        bankController.setPrimaryStage(primaryStage);

        // Display system status
        displaySystemStatus();

        bankController.showLoginView();
    }

    private void displaySystemStatus() {
        Bank bank = new Bank();
        System.out.println("\n" + "=".repeat(60));
        System.out.println("🏦 BANKING SYSTEM STARTED");
        System.out.println("=".repeat(60));
        System.out.println("Total Customers: " + bank.getAllCustomers().size());
        System.out.println("Total Accounts: " + bank.getAllAccounts().size());

        if (bank.getAllCustomers().isEmpty()) {
            System.out.println("\n💡 No customers found. Create your first account!");
            System.out.println("📝 Click 'Create New Account' to get started");
        } else {
            System.out.println("\n👥 EXISTING CUSTOMERS:");
            System.out.println("-".repeat(30));
            for (Customer customer : bank.getAllCustomers()) {
                System.out.println("ID: " + customer.getCustomerId() +
                        " - " + customer.getFullName() +
                        " (" + customer.getCustomerType() + ")");
            }
            System.out.println("\n💡 Use Customer ID to login");
        }

        System.out.println("👨‍💼 Admin Login: admin / admin");
        System.out.println("📝 Password can be any text for demo purposes");
        System.out.println("=".repeat(60) + "\n");
    }

    public static void main(String[] args) {
        // Clear database if needed (for testing)
        if (args.length > 0 && args[0].equals("--clear")) {
            DatabaseManager.clearAllData();
            System.out.println("🗑️ Database cleared. Starting fresh...");
        }

        // Launch GUI
        launch(args);
    }
}