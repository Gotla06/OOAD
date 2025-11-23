import java.util.Date;

public class Transaction {
    private String transactionId;
    private String accountNumber;
    private String type;
    private double amount;
    private String description;
    private Date timestamp;

    public Transaction(String accountNumber, String type, double amount, String description) {
        this.transactionId = "TXN" + System.currentTimeMillis();
        this.accountNumber = accountNumber;
        this.type = type;
        this.amount = amount;
        this.description = description;
        this.timestamp = new Date();
    }

    // Getters
    public String getTransactionId() { return transactionId; }
    public String getAccountNumber() { return accountNumber; }
    public String getType() { return type; }
    public double getAmount() { return amount; }
    public String getDescription() { return description; }
    public Date getTimestamp() { return timestamp; }

    @Override
    public String toString() {
        return String.format("Transaction[%s]: %s - BWP %.2f - %s",
                type, timestamp, amount, description);
    }
}