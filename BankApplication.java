import javafx.application.Application;
import javafx.stage.Stage;

public class BankApplication extends Application {
    private BankController bankController;

    public BankApplication() {
        this.bankController = new BankController();
    }

    @Override
    public void start(Stage primaryStage) {
        bankController.setPrimaryStage(primaryStage);
        bankController.showLoginView();
    }

    public static void main(String[] args) {
        launch(args);
    }
}