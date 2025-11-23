import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class LoginView {
    private Scene scene;
    private TextField usernameField;
    private PasswordField passwordField;
    private Button loginButton;
    private Button createAccountButton;
    private Button showIdsButton;
    private Label messageLabel;
    private LoginController controller;

    public LoginView(LoginController controller) {
        this.controller = controller;
        initializeUI();
    }

    private void initializeUI() {
        Label titleLabel = new Label("Banking System Login");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        usernameField = new TextField();
        usernameField.setPromptText("Enter Customer ID (e.g., CUST1)");
        usernameField.setMaxWidth(300);

        passwordField = new PasswordField();
        passwordField.setPromptText("Enter password (any text for demo)");
        passwordField.setMaxWidth(300);

        loginButton = new Button("Login");
        loginButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
        loginButton.setMaxWidth(300);

        createAccountButton = new Button("Create New Account");
        createAccountButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        createAccountButton.setMaxWidth(300);

        showIdsButton = new Button("Show Available Customer IDs");
        showIdsButton.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white;");
        showIdsButton.setMaxWidth(300);

        messageLabel = new Label();
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(300);

        VBox layout = new VBox(15);
        layout.setPadding(new Insets(30));
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(
                titleLabel,
                new Label("Customer ID:"), usernameField,
                new Label("Password:"), passwordField,
                loginButton,
                createAccountButton,
                showIdsButton,
                messageLabel
        );

        scene = new Scene(layout, 500, 500);

        setupEventHandlers();
    }

    private void setupEventHandlers() {
        loginButton.setOnAction(e -> {
            controller.handleLogin(getUsername(), getPassword());
        });

        createAccountButton.setOnAction(e -> {
            controller.handleCreateAccount();
        });

        showIdsButton.setOnAction(e -> {
            // This would ideally show a popup with available IDs
            System.out.println("\n=== Available Customer IDs ===");
            // In a real implementation, this would show a dialog with available IDs
            messageLabel.setText("Check console for available Customer IDs");
            messageLabel.setTextFill(Color.BLUE);
        });
    }

    public String getUsername() {
        return usernameField.getText();
    }

    public String getPassword() {
        return passwordField.getText();
    }

    public void setMessage(String message, boolean isSuccess) {
        messageLabel.setText(message);
        messageLabel.setTextFill(isSuccess ? Color.GREEN : Color.RED);
    }

    public void clearFields() {
        usernameField.clear();
        passwordField.clear();
        messageLabel.setText("");
    }

    public Scene getScene() {
        return scene;
    }
}