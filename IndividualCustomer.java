import java.util.Date;

public class IndividualCustomer extends Customer {
    private String idNumber;
    private Date dateOfBirth;

    public IndividualCustomer(String customerId, String firstName, String surname,
                              String address, String idNumber, Date dateOfBirth) {
        super(customerId, firstName, surname, address);
        this.idNumber = idNumber;
        this.dateOfBirth = dateOfBirth;
    }

    @Override
    public boolean canOpenAccount(AccountType accountType) {
        // For cheque account, individual must be working (simplified check)
        if (accountType == AccountType.CHEQUE) {
            // In real implementation, we would check employment status
            return true; // Assuming they can open for demo purposes
        }
        return true;
    }

    // Getters and setters
    public String getIdNumber() { return idNumber; }
    public void setIdNumber(String idNumber) { this.idNumber = idNumber; }

    public Date getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(Date dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    @Override
    public String toString() {
        return "IndividualCustomer{" +
                "customerId='" + getCustomerId() + '\'' +
                ", name='" + getFullName() + '\'' +
                ", idNumber='" + idNumber + '\'' +
                '}';
    }
}
