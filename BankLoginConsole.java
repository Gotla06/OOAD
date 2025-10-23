import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.Date;

// Change from package-private to PUBLIC
public class BankLoginConsole extends Application {
    private Bank bank = new Bank();

    // Add this default constructor
    public BankLoginConsole() {
        // Default constructor required by JavaFX
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Bank Account Creation");

        // --- Customer type ---
        Label customerTypeLabel = new Label("Customer Type:");
        ToggleGroup customerGroup = new ToggleGroup();
        RadioButton individualBtn = new RadioButton("Individual");
        RadioButton companyBtn = new RadioButton("Company");
        individualBtn.setToggleGroup(customerGroup);
        companyBtn.setToggleGroup(customerGroup);
        individualBtn.setSelected(true);

        HBox customerTypeBox = new HBox(10, individualBtn, companyBtn);

        // --- Account type ---
        Label accountTypeLabel = new Label("Account Type:");
        ComboBox<String> accountTypeCombo = new ComboBox<>();
        accountTypeCombo.getItems().addAll("Savings", "Investment", "Cheque");
        accountTypeCombo.getSelectionModel().selectFirst();

        // --- Individual fields ---
        Label firstNameLabel = new Label("First Name:");
        TextField firstNameField = new TextField();
        firstNameField.setPromptText("First Name");

        Label lastNameLabel = new Label("Last Name:");
        TextField lastNameField = new TextField();
        lastNameField.setPromptText("Last Name");

        Label addressLabel = new Label("Address:");
        TextField addressField = new TextField();
        addressField.setPromptText("Address");

        Label idLabel = new Label("ID Number:");
        TextField idField = new TextField();
        idField.setPromptText("ID Number");

        Label dobLabel = new Label("Date of Birth:");
        DatePicker dobPicker = new DatePicker();

        // --- Company fields ---
        Label companyNameLabel = new Label("Company Name:");
        TextField companyNameField = new TextField();
        companyNameField.setPromptText("Company Name");

        Label regNumberLabel = new Label("Registration Number:");
        TextField regNumberField = new TextField();
        regNumberField.setPromptText("Registration Number");

        Label contactPersonLabel = new Label("Contact Person:");
        TextField contactPersonField = new TextField();
        contactPersonField.setPromptText("Contact Person");

        // Create individual fields container
        VBox individualFields = new VBox(5, firstNameLabel, firstNameField, lastNameLabel, lastNameField,
                addressLabel, addressField, idLabel, idField, dobLabel, dobPicker);

        // Create company fields container
        VBox companyFields = new VBox(5, companyNameLabel, companyNameField, regNumberLabel, regNumberField,
                contactPersonLabel, contactPersonField);

        // Main fields container
        VBox fieldsBox = new VBox(10);
        fieldsBox.getChildren().addAll(individualFields, companyFields);

        Label resultLabel = new Label();

        Button createBtn = new Button("Create Account");
        Button clearBtn = new Button("Clear");
        HBox buttonsBox = new HBox(10, createBtn, clearBtn);

        VBox root = new VBox(15,
                customerTypeLabel, customerTypeBox,
                accountTypeLabel, accountTypeCombo,
                fieldsBox,
                buttonsBox,
                resultLabel
        );
        root.setPadding(new Insets(20));

        // --- Show/hide fields based on customer type ---
        Runnable updateVisibility = () -> {
            boolean individual = individualBtn.isSelected();
            individualFields.setVisible(individual);
            individualFields.setManaged(individual);

            companyFields.setVisible(!individual);
            companyFields.setManaged(!individual);
        };

        individualBtn.setOnAction(e -> updateVisibility.run());
        companyBtn.setOnAction(e -> updateVisibility.run());
        updateVisibility.run();

        // --- Button actions ---
        createBtn.setOnAction(e -> {
            String accType = accountTypeCombo.getSelectionModel().getSelectedItem();
            String accNum = "ACC" + (int)(Math.random() * 10000);
            String branch = "Gaborone Main";
            double balance = 0;

            try {
                if (individualBtn.isSelected()) {
                    String firstName = firstNameField.getText().trim();
                    String lastName = lastNameField.getText().trim();
                    String address = addressField.getText().trim();
                    String idNum = idField.getText().trim();

                    if (firstName.isEmpty() || lastName.isEmpty() || address.isEmpty() || idNum.isEmpty()) {
                        resultLabel.setStyle("-fx-text-fill: red;");
                        resultLabel.setText("⚠️ Please fill all individual fields");
                        return;
                    }

                    Date dob = dobPicker.getValue() != null ? java.sql.Date.valueOf(dobPicker.getValue()) : new Date();

                    IndividualCustomer customer = new IndividualCustomer(
                            "CUST" + (int)(Math.random()*1000),
                            firstName, lastName, address, idNum, dob
                    );
                    bank.addCustomer(customer);

                    Account acc = createAccount(accType, accNum, balance, branch, customer);
                    bank.openAccount(acc);

                    resultLabel.setStyle("-fx-text-fill: green;");
                    resultLabel.setText("✅ Individual account created: " + accNum);

                } else {
                    String companyName = companyNameField.getText().trim();
                    String regNum = regNumberField.getText().trim();
                    String address = companyNameField.getText().trim();
                    String contact = contactPersonField.getText().trim();

                    if (companyName.isEmpty() || regNum.isEmpty() || address.isEmpty() || contact.isEmpty()) {
                        resultLabel.setStyle("-fx-text-fill: red;");
                        resultLabel.setText("⚠️ Please fill all company fields");
                        return;
                    }

                    CompanyCustomer company = new CompanyCustomer(
                            "CUST" + (int)(Math.random()*1000),
                            companyName, regNum, address, contact
                    );
                    bank.addCustomer(company);

                    Account acc = createAccount(accType, accNum, balance, branch, company);
                    bank.openAccount(acc);

                    resultLabel.setStyle("-fx-text-fill: green;");
                    resultLabel.setText("✅ Company account created: " + accNum);
                }
            } catch (Exception ex) {
                resultLabel.setStyle("-fx-text-fill: red;");
                resultLabel.setText("⚠️ Error: " + ex.getMessage());
                ex.printStackTrace(); // This will help debug any other issues
            }
        });

        clearBtn.setOnAction(e -> {
            firstNameField.clear();
            lastNameField.clear();
            addressField.clear();
            idField.clear();
            dobPicker.setValue(null);
            companyNameField.clear();
            regNumberField.clear();
            contactPersonField.clear();
            resultLabel.setText("");
        });

        Scene scene = new Scene(root, 450, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private Account createAccount(String type, String accNum, double balance,
                                  String branch, Customer customer) {
        return switch (type.toLowerCase()) {
            case "savings" -> new SavingsAccount(accNum, balance, branch, customer);
            case "investment" -> new InvestmentAccount(accNum, balance, branch, customer);
            case "cheque" -> new ChequeAccount(accNum, balance, branch, customer, "", "");
            default -> throw new IllegalArgumentException("Invalid account type: " + type);
        };
    }
}