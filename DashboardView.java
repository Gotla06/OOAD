import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public class DashboardView {
    private Scene scene;
    private Label welcomeLabel;
    private TextArea accountsArea;
    private TextArea transactionsArea;
    private Button logoutButton;
    private Button refreshButton;
    private Button addAccountButton;
    private Button depositButton;
    private Button withdrawButton;
    private Button interestButton;

    public DashboardView(DashboardController controller, String customerName, String customerId) {
        initializeUI(controller, customerName, customerId);
    }

    private void initializeUI(DashboardController controller, String customerName, String customerId) {
        welcomeLabel = new Label("Welcome, " + customerName + "! (ID: " + customerId + ")");
        welcomeLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        accountsArea = new TextArea();
        accountsArea.setEditable(false);
        accountsArea.setPrefHeight(200);
        accountsArea.setPromptText("Your accounts will appear here...");

        transactionsArea = new TextArea();
        transactionsArea.setEditable(false);
        transactionsArea.setPrefHeight(150);
        transactionsArea.setPromptText("Recent transactions will appear here...");

        logoutButton = new Button("Logout");
        logoutButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");

        refreshButton = new Button("Refresh");
        refreshButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");

        addAccountButton = new Button("Add Account");
        addAccountButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");

        depositButton = new Button("Deposit");
        depositButton.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white;");

        withdrawButton = new Button("Withdraw");
        withdrawButton.setStyle("-fx-background-color: #E91E63; -fx-text-fill: white;");

        interestButton = new Button("Apply Interest");
        interestButton.setStyle("-fx-background-color: #9C27B0; -fx-text-fill: white;");

        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));

        HBox buttonRow1 = new HBox(10, refreshButton, addAccountButton, interestButton);
        HBox buttonRow2 = new HBox(10, depositButton, withdrawButton, logoutButton);

        layout.getChildren().addAll(
                welcomeLabel,
                new Label("Your Accounts:"),
                accountsArea,
                new Label("Recent Transactions:"),
                transactionsArea,
                buttonRow1,
                buttonRow2
        );

        scene = new Scene(layout, 700, 600);

        // Event handlers
        logoutButton.setOnAction(e -> controller.handleLogout());
        refreshButton.setOnAction(e -> controller.handleRefresh());
        addAccountButton.setOnAction(e -> controller.handleAddAccount());
        depositButton.setOnAction(e -> controller.handleDeposit());
        withdrawButton.setOnAction(e -> controller.handleWithdraw());
        interestButton.setOnAction(e -> controller.handleApplyInterest());
    }

    public void displayAccounts(String accountsInfo) {
        accountsArea.setText(accountsInfo);
    }

    public void displayTransactions(String transactionsInfo) {
        transactionsArea.setText(transactionsInfo);
    }

    public Scene getScene() {
        return scene;
    }
}