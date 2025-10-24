import javafx.stage.Stage;

public class BankController {
    private Stage primaryStage;
    private Bank bank;
    private LoginController loginController;
    private AccountController accountController;

    public BankController() {
        this.bank = new Bank();
        initializeControllers();
    }

    private void initializeControllers() {
        this.loginController = new LoginController(this, bank);
        this.accountController = new AccountController(this, bank);
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    // Navigation methods
    public void showLoginView() {
        LoginView loginView = new LoginView(loginController);
        primaryStage.setScene(loginView.getScene());
        primaryStage.setTitle("Banking System - Login");
        primaryStage.show();
    }

    public void showAccountCreationView() {
        AccountCreationView accountView = new AccountCreationView(accountController);
        primaryStage.setScene(accountView.getScene());
        primaryStage.setTitle("Create Bank Account");
    }
}