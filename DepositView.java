import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class DepositView {
    private Scene scene;
    private ComboBox<String> accountComboBox;
    private TextField amountField;
    private Button depositButton;
    private Button backButton;
    private Label messageLabel;
    private Label balanceLabel;

    public DepositView(TransactionController controller, String customerId) {
        initializeUI(controller, customerId);
    }

    private void initializeUI(TransactionController controller, String customerId) {
        Label titleLabel = new Label("Deposit Funds");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        accountComboBox = new ComboBox<>();
        amountField = new TextField();
        amountField.setPromptText("Enter amount in BWP");

        depositButton = new Button("Deposit");
        depositButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");

        backButton = new Button("Back to Dashboard");
        backButton.setStyle("-fx-background-color: #757575; -fx-text-fill: white;");

        messageLabel = new Label();
        balanceLabel = new Label();

        // Create form layout
        GridPane formGrid = new GridPane();
        formGrid.setHgap(10);
        formGrid.setVgap(10);
        formGrid.setPadding(new Insets(20));

        formGrid.add(new Label("Select Account:"), 0, 0);
        formGrid.add(accountComboBox, 1, 0);
        formGrid.add(new Label("Amount (BWP):"), 0, 1);
        formGrid.add(amountField, 1, 1);
        formGrid.add(balanceLabel, 1, 2);

        HBox buttonBox = new HBox(15, depositButton, backButton);
        buttonBox.setAlignment(Pos.CENTER);

        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.getChildren().addAll(
                titleLabel,
                formGrid,
                buttonBox,
                messageLabel
        );

        scene = new Scene(layout, 400, 300);

        setupEventHandlers(controller, customerId);
        loadCustomerAccounts(controller, customerId);
    }

    private void setupEventHandlers(TransactionController controller, String customerId) {
        depositButton.setOnAction(e -> {
            String selectedAccount = accountComboBox.getValue();
            String amountText = amountField.getText();

            if (selectedAccount != null && amountText != null && !amountText.isEmpty()) {
                try {
                    double amount = Double.parseDouble(amountText);
                    controller.handleDeposit(selectedAccount, amount);
                } catch (NumberFormatException ex) {
                    setMessage("Please enter a valid amount", false);
                }
            } else {
                setMessage("Please select an account and enter amount", false);
            }
        });

        backButton.setOnAction(e -> {
            controller.handleBackToDashboard();
        });

        // Update balance when account selection changes
        accountComboBox.setOnAction(e -> {
            updateBalanceDisplay(controller);
        });
    }

    private void loadCustomerAccounts(TransactionController controller, String customerId) {
        accountComboBox.getItems().clear();
        java.util.List<Account> accounts = controller.getCustomerAccounts(customerId);

        for (Account account : accounts) {
            accountComboBox.getItems().add(account.getAccountNumber());
        }

        if (!accounts.isEmpty()) {
            accountComboBox.getSelectionModel().selectFirst();
            updateBalanceDisplay(controller);
        }
    }

    private void updateBalanceDisplay(TransactionController controller) {
        String selectedAccount = accountComboBox.getValue();
        if (selectedAccount != null) {
            Account account = controller.getAccount(selectedAccount);
            if (account != null) {
                balanceLabel.setText("Current Balance: BWP " + String.format("%.2f", account.getBalance()));
                balanceLabel.setTextFill(Color.BLUE);
            }
        }
    }

    public void setMessage(String message, boolean isSuccess) {
        messageLabel.setText(message);
        messageLabel.setTextFill(isSuccess ? Color.GREEN : Color.RED);
    }

    public void clearFields() {
        amountField.clear();
        messageLabel.setText("");
    }

    public Scene getScene() {
        return scene;
    }
}