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
    private Label messageLabel;

    public LoginView(LoginController controller) {
        initializeUI(controller);
    }

    private void initializeUI(LoginController controller) {
        // Create UI components
        Label titleLabel = new Label("Banking System Login");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        usernameField = new TextField();
        usernameField.setPromptText("Enter username");
        usernameField.setMaxWidth(250);

        passwordField = new PasswordField();
        passwordField.setPromptText("Enter password");
        passwordField.setMaxWidth(250);

        loginButton = new Button("Login");
        loginButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
        loginButton.setMaxWidth(250);

        createAccountButton = new Button("Create New Account");
        createAccountButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        createAccountButton.setMaxWidth(250);

        messageLabel = new Label();

        // Layout
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(30));
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(
                titleLabel,
                new Label("Username:"), usernameField,
                new Label("Password:"), passwordField,
                loginButton, createAccountButton, messageLabel
        );

        scene = new Scene(layout, 400, 400);

        // Connect to controller (NO BUSINESS LOGIC HERE)
        setupEventHandlers(controller);
    }

    private void setupEventHandlers(LoginController controller) {
        loginButton.setOnAction(e -> {
            controller.handleLogin(getUsername(), getPassword());
        });

        createAccountButton.setOnAction(e -> {
            controller.handleCreateAccount();
        });
    }

    // Pure getter methods - no business logic
    public String getUsername() {
        return usernameField.getText();
    }

    public String getPassword() {
        return passwordField.getText();
    }

    public void setMessage(String message, boolean isError) {
        messageLabel.setText(message);
        messageLabel.setTextFill(isError ? Color.RED : Color.GREEN);
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