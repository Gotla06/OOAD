import javafx.application.Application;
import javafx.stage.Stage;

public class BankApplication extends Application {
    private BankController bankController;

    // FIX: Changed from BankController() to BankApplication()
    public BankApplication() {
        this.bankController = new BankController();
    }

    @Override
    public void start(Stage primaryStage) {
        bankController.setPrimaryStage(primaryStage);

        // Display available login IDs when application starts
        bankController.displayAllCustomerIDs();

        bankController.showLoginView();
    }

    public static void main(String[] args) {
        // Test the system and show available accounts
        Bank bank = new Bank();
        System.out.println("=== BANKING SYSTEM STARTED ===");
        System.out.println("Total Customers: " + bank.getAllCustomers().size());
        System.out.println("Total Accounts: " + bank.getAllAccounts().size());

        // Display all available customer IDs for login
        System.out.println("\n" + "=".repeat(60));
        System.out.println("💡 QUICK START - USE THESE IDs TO LOGIN:");
        System.out.println("=".repeat(60));
        for (Customer customer : bank.getAllCustomers()) {
            System.out.println("🔑 Username: " + customer.getCustomerId() +
                    " - " + customer.getFullName() +
                    " (" + customer.getCustomerType() + ")");
        }
        System.out.println("👨‍💼 Admin: admin / admin");
        System.out.println("=".repeat(60));
        System.out.println("📝 Password can be any text for demo purposes");
        System.out.println("=".repeat(60) + "\n");

        // Launch GUI
        launch(args);
    }
}