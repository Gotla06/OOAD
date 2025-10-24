public class LoginController {
    private BankController bankController;
    private Bank bank;

    public LoginController(BankController bankController, Bank bank) {
        this.bankController = bankController;
        this.bank = bank;
    }

    // Business logic for login
    public void handleLogin(String username, String password) {
        // Input validation
        if (username == null || username.trim().isEmpty()) {
            // In real implementation, you'd update the view through callbacks
            System.out.println("Username is required");
            return;
        }

        if (password == null || password.trim().isEmpty()) {
            System.out.println("Password is required");
            return;
        }

        // Authentication logic
        if (authenticateUser(username, password)) {
            System.out.println("Login successful!");
            // Navigate to dashboard (to be implemented)
        } else {
            System.out.println("Invalid credentials");
        }
    }

    public void handleCreateAccount() {
        bankController.showAccountCreationView();
    }

    private boolean authenticateUser(String username, String password) {
        // Demo authentication - in real system, check against database
        return !username.isEmpty() && !password.isEmpty();
    }
}
