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
    private TextField companyNameField, regNumberField, contactPersonField, companyAddressField;
    private TextField employerNameField, employerAddressField;
    private CheckBox employedCheckbox;
    private DatePicker dobPicker;
    private Button createButton, backButton, clearButton;
    private Label messageLabel;
    private VBox individualFields, companyFields, employmentFields;

    public AccountCreationView(AccountController controller) {
        initializeUI(controller);
    }

    private void initializeUI(AccountController controller) {
        initializeComponents();
        VBox root = createMainLayout();
        scene = new Scene(root, 600, 750); // Increased height for additional fields
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

        // Initialize all fields
        firstNameField = new TextField();
        lastNameField = new TextField();
        addressField = new TextField(); // INDIVIDUAL ADDRESS FIELD
        idField = new TextField();
        dobPicker = new DatePicker();

        companyNameField = new TextField();
        regNumberField = new TextField();
        contactPersonField = new TextField();
        companyAddressField = new TextField(); // COMPANY ADDRESS FIELD

        employerNameField = new TextField();
        employerAddressField = new TextField();
        employedCheckbox = new CheckBox("Currently Employed");

        createButton = new Button("Create Account");
        backButton = new Button("Back to Login");
        clearButton = new Button("Clear");
        messageLabel = new Label();

        individualFields = createIndividualFields();
        companyFields = createCompanyFields();
        employmentFields = createEmploymentFields();
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
                individualFields,
                employmentFields,
                companyFields,
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
                new Label("First Name:*"), firstNameField,
                new Label("Last Name:*"), lastNameField,
                new Label("Address:*"), addressField, // ADDED ADDRESS FIELD FOR INDIVIDUAL
                new Label("ID Number:*"), idField,
                new Label("Date of Birth:*"), dobPicker
        );
        fields.setStyle("-fx-border-color: #cccccc; -fx-border-width: 1px; -fx-padding: 15px;");
        return fields;
    }

    private VBox createEmploymentFields() {
        VBox fields = new VBox(8,
                employedCheckbox,
                new Label("Employer Name:"), employerNameField,
                new Label("Employer Address:"), employerAddressField
        );
        fields.setStyle("-fx-border-color: #cccccc; -fx-border-width: 1px; -fx-padding: 15px;");
        return fields;
    }

    private VBox createCompanyFields() {
        VBox fields = new VBox(8,
                new Label("Company Name:*"), companyNameField,
                new Label("Registration Number:*"), regNumberField,
                new Label("Company Address:*"), companyAddressField, // COMPANY ADDRESS FIELD
                new Label("Contact Person:*"), contactPersonField
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
        individualBtn.setOnAction(e -> updateFieldVisibility());
        companyBtn.setOnAction(e -> updateFieldVisibility());
        employedCheckbox.setOnAction(e -> updateEmploymentFields());
        accountTypeCombo.setOnAction(e -> updateEmploymentRequirement());

        createButton.setOnAction(e -> {
            controller.handleCreateAccount(
                    isIndividualCustomer(),
                    getAccountType(),
                    getIndividualData(),
                    getCompanyData()
            );
        });

        clearButton.setOnAction(e -> clearAllFields());
        backButton.setOnAction(e -> controller.handleBackToLogin());
    }

    private void updateFieldVisibility() {
        boolean individual = isIndividualCustomer();
        individualFields.setVisible(individual);
        individualFields.setManaged(individual);
        employmentFields.setVisible(individual);
        employmentFields.setManaged(individual);
        companyFields.setVisible(!individual);
        companyFields.setManaged(!individual);
        updateEmploymentFields();
        updateEmploymentRequirement();
    }

    private void updateEmploymentFields() {
        boolean employed = employedCheckbox.isSelected();
        employerNameField.setDisable(!employed);
        employerAddressField.setDisable(!employed);

        // Clear fields if employment is unchecked
        if (!employed) {
            employerNameField.clear();
            employerAddressField.clear();
        }
    }

    private void updateEmploymentRequirement() {
        // Highlight employment requirement for cheque accounts
        if ("Cheque".equals(getAccountType()) && isIndividualCustomer()) {
            employedCheckbox.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
            messageLabel.setText("Note: Employment information is REQUIRED for Cheque accounts");
            messageLabel.setTextFill(Color.ORANGE);
        } else {
            employedCheckbox.setStyle("");
            messageLabel.setText("");
        }
    }

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
                addressField.getText(), // INDIVIDUAL ADDRESS
                idField.getText(),
                dobPicker.getValue(),
                employedCheckbox.isSelected(),
                employerNameField.getText(),
                employerAddressField.getText()
        );
    }

    public CompanyFormData getCompanyData() {
        return new CompanyFormData(
                companyNameField.getText(),
                regNumberField.getText(),
                contactPersonField.getText(),
                companyAddressField.getText() // COMPANY ADDRESS
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
        companyAddressField.clear();
        employerNameField.clear();
        employerAddressField.clear();
        employedCheckbox.setSelected(false);
        messageLabel.setText("");
    }

    public Scene getScene() {
        return scene;
    }

    public static class IndividualFormData {
        public final String firstName, lastName, address, idNumber;
        public final java.time.LocalDate dateOfBirth;
        public final boolean employed;
        public final String employerName, employerAddress;

        public IndividualFormData(String firstName, String lastName, String address,
                                  String idNumber, java.time.LocalDate dateOfBirth,
                                  boolean employed, String employerName, String employerAddress) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.address = address;
            this.idNumber = idNumber;
            this.dateOfBirth = dateOfBirth;
            this.employed = employed;
            this.employerName = employerName;
            this.employerAddress = employerAddress;
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