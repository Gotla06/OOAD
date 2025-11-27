public class LoginController {
    private BankController bankController;
    private Bank bank;

    public LoginController(BankController bankController, Bank bank) {
        this.bankController = bankController;
        this.bank = bank;
    }

    public void handleLogin(String username, String password) {
        // Input validation
        if (username == null || username.trim().isEmpty()) {
            System.out.println("Username is required");
            return;
        }

        if (password == null || password.trim().isEmpty()) {
            System.out.println("Password is required");
            return;
        }

        // Authentication logic
        if (authenticateUser(username, password)) {
            System.out.println("Login successful for: " + username);
            bankController.showDashboardView(username);
        } else {
            System.out.println("Invalid credentials for: " + username);

            // Show available customer IDs for help
            if (bank.getAllCustomers().isEmpty()) {
                System.out.println("💡 No customers found. Click 'Create New Account' to create your first account!");
            } else {
                System.out.println("💡 Available Customer IDs:");
                for (Customer customer : bank.getAllCustomers()) {
                    System.out.println("  - " + customer.getCustomerId() + " (" + customer.getFullName() + ")");
                }
            }
        }
    }

    public void handleCreateAccount() {
        bankController.showAccountCreationView();
    }

    private boolean authenticateUser(String username, String password) {
        // Demo authentication - check if customer exists or use admin
        if ("admin".equals(username) && "admin".equals(password)) {
            return true;
        }

        // Check if customer exists in the bank
        Customer customer = bank.getCustomer(username);
        if (customer != null) {
            System.out.println("Authenticated: " + customer.getFullName() + " (" + customer.getCustomerType() + ")");
            return true;
        }

        return false;
    }
}