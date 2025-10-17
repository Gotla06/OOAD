import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.Date;

class BankLoginConsole extends Application {
    private Bank bank = new Bank();

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
        TextField firstNameField = new TextField();
        firstNameField.setPromptText("First Name");
        TextField lastNameField = new TextField();
        lastNameField.setPromptText("Last Name");
        TextField addressField = new TextField();
        addressField.setPromptText("Address");
        TextField idField = new TextField();
        idField.setPromptText("ID Number");
        DatePicker dobPicker = new DatePicker();

        // --- Company fields ---
        TextField companyNameField = new TextField();
        companyNameField.setPromptText("Company Name");
        TextField regNumberField = new TextField();
        regNumberField.setPromptText("Registration Number");
        TextField contactPersonField = new TextField();
        contactPersonField.setPromptText("Contact Person");

        // --- Layout ---
        VBox fieldsBox = new VBox(10,
                firstNameField, lastNameField, addressField, idField, dobPicker,
                companyNameField, regNumberField, contactPersonField
        );

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
            firstNameField.setVisible(individual);
            lastNameField.setVisible(individual);
            addressField.setVisible(individual);
            idField.setVisible(individual);
            dobPicker.setVisible(individual);

            companyNameField.setVisible(!individual);
            regNumberField.setVisible(!individual);
            contactPersonField.setVisible(!individual);
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
                    Date dob = java.sql.Date.valueOf(dobPicker.getValue());

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

        Scene scene = new Scene(root, 400, 550);
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
