import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public class AccountCreationView {
    private Scene scene;
    private ToggleGroup customerGroup;
    private RadioButton individualBtn, companyBtn;
    private ComboBox<String> accountTypeCombo;
    private TextField firstNameField, lastNameField, addressField, idField;
    private TextField companyNameField, regNumberField, contactPersonField;
    private DatePicker dobPicker;
    private Button createButton, backButton, clearButton;
    private Label messageLabel;
    private VBox individualFields, companyFields;

    public AccountCreationView(AccountController controller) {
        initializeUI(controller);
    }

    private void initializeUI(AccountController controller) {
        // Initialize components
        initializeComponents();

        // Create layout
        VBox root = createMainLayout();

        scene = new Scene(root, 500, 650);

        // Connect to controller
        setupEventHandlers(controller);
        updateFieldVisibility();
    }

    private void initializeComponents() {
        customerGroup = new ToggleGroup();
        individualBtn = new RadioButton("Individual");
        companyBtn = new RadioButton("Company");
        individualBtn.setToggleGroup(customerGroup);
        companyBtn.setToggleGroup(customerGroup);
        individualBtn.setSelected(true);

        accountTypeCombo = new ComboBox<>();
        accountTypeCombo.getItems().addAll("Savings", "Investment", "Cheque");
        accountTypeCombo.getSelectionModel().selectFirst();

        // Initialize all text fields
        firstNameField = new TextField();
        lastNameField = new TextField();
        addressField = new TextField();
        idField = new TextField();
        dobPicker = new DatePicker();
        companyNameField = new TextField();
        regNumberField = new TextField();
        contactPersonField = new TextField();

        createButton = new Button("Create Account");
        backButton = new Button("Back to Login");
        clearButton = new Button("Clear");
        messageLabel = new Label();

        // Create field containers
        individualFields = createIndividualFields();
        companyFields = createCompanyFields();
    }

    private VBox createMainLayout() {
        Label titleLabel = new Label("Create Bank Account");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.getChildren().addAll(
                titleLabel,
                new Label("Customer Type:"), createCustomerTypeBox(),
                new Label("Account Type:"), accountTypeCombo,
                individualFields, companyFields,
                createButtonsBox(),
                messageLabel
        );

        return layout;
    }

    private HBox createCustomerTypeBox() {
        HBox box = new HBox(20, individualBtn, companyBtn);
        box.setAlignment(Pos.CENTER_LEFT);
        return box;
    }

    private VBox createIndividualFields() {
        VBox fields = new VBox(8,
                new Label("First Name:"), firstNameField,
                new Label("Last Name:"), lastNameField,
                new Label("Address:"), addressField,
                new Label("ID Number:"), idField,
                new Label("Date of Birth:"), dobPicker
        );
        fields.setStyle("-fx-border-color: #cccccc; -fx-border-width: 1px; -fx-padding: 15px;");
        return fields;
    }

    private VBox createCompanyFields() {
        VBox fields = new VBox(8,
                new Label("Company Name:"), companyNameField,
                new Label("Registration Number:"), regNumberField,
                new Label("Address:"), addressField,
                new Label("Contact Person:"), contactPersonField
        );
        fields.setStyle("-fx-border-color: #cccccc; -fx-border-width: 1px; -fx-padding: 15px;");
        return fields;
    }

    private HBox createButtonsBox() {
        createButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
        backButton.setStyle("-fx-background-color: #757575; -fx-text-fill: white;");
        clearButton.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white;");

        HBox buttonsBox = new HBox(15, createButton, clearButton, backButton);
        buttonsBox.setAlignment(Pos.CENTER);
        return buttonsBox;
    }

    private void setupEventHandlers(AccountController controller) {
        // Customer type toggle
        individualBtn.setOnAction(e -> updateFieldVisibility());
        companyBtn.setOnAction(e -> updateFieldVisibility());

        // Button actions
        createButton.setOnAction(e -> {
            controller.handleCreateAccount(
                    isIndividualCustomer(),
                    getAccountType(),
                    getIndividualData(),
                    getCompanyData()
            );
        });

        clearButton.setOnAction(e -> {
            clearAllFields();
        });

        backButton.setOnAction(e -> {
            controller.handleBackToLogin();
        });
    }

    private void updateFieldVisibility() {
        boolean individual = isIndividualCustomer();
        individualFields.setVisible(individual);
        individualFields.setManaged(individual);
        companyFields.setVisible(!individual);
        companyFields.setManaged(!individual);
    }

    // Pure getter methods - no business logic
    public boolean isIndividualCustomer() {
        return individualBtn.isSelected();
    }

    public String getAccountType() {
        return accountTypeCombo.getValue();
    }

    public IndividualFormData getIndividualData() {
        return new IndividualFormData(
                firstNameField.getText(),
                lastNameField.getText(),
                addressField.getText(),
                idField.getText(),
                dobPicker.getValue()
        );
    }

    public CompanyFormData getCompanyData() {
        return new CompanyFormData(
                companyNameField.getText(),
                regNumberField.getText(),
                contactPersonField.getText(),
                addressField.getText()
        );
    }

    public void setMessage(String message, boolean isSuccess) {
        messageLabel.setText(message);
        messageLabel.setTextFill(isSuccess ? Color.GREEN : Color.RED);
    }

    public void clearAllFields() {
        firstNameField.clear();
        lastNameField.clear();
        addressField.clear();
        idField.clear();
        dobPicker.setValue(null);
        companyNameField.clear();
        regNumberField.clear();
        contactPersonField.clear();
        messageLabel.setText("");
    }

    public Scene getScene() {
        return scene;
    }

    // Data transfer objects
    public static class IndividualFormData {
        public final String firstName, lastName, address, idNumber;
        public final java.time.LocalDate dateOfBirth;

        public IndividualFormData(String firstName, String lastName, String address,
                                  String idNumber, java.time.LocalDate dateOfBirth) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.address = address;
            this.idNumber = idNumber;
            this.dateOfBirth = dateOfBirth;
        }
    }

    public static class CompanyFormData {
        public final String companyName, registrationNumber, contactPerson, address;

        public CompanyFormData(String companyName, String registrationNumber,
                               String contactPerson, String address) {
            this.companyName = companyName;
            this.registrationNumber = registrationNumber;
            this.contactPerson = contactPerson;
            this.address = address;
        }
    }
}
