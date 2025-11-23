import java.util.Date;

public class IndividualCustomer extends Customer {
    private String idNumber;
    private Date dateOfBirth;
    private boolean employed;
    private String employerName;
    private String employerAddress;

    public IndividualCustomer(String customerId, String firstName, String surname,
                              String address, String idNumber, Date dateOfBirth,
                              boolean employed, String employerName, String employerAddress) {
        super(customerId, firstName, surname, address);
        this.idNumber = idNumber;
        this.dateOfBirth = dateOfBirth;
        this.employed = employed;
        this.employerName = employerName;
        this.employerAddress = employerAddress;
    }

    @Override
    public boolean canOpenAccount(AccountType accountType) {
        if (accountType == AccountType.CHEQUE) {
            return employed && employerName != null && !employerName.trim().isEmpty();
        }
        return true;
    }

    @Override
    public String getCustomerType() {
        return "Individual";
    }

    // Getters
    public String getIdNumber() { return idNumber; }
    public Date getDateOfBirth() { return dateOfBirth; }
    public boolean isEmployed() { return employed; }
    public String getEmployerName() { return employerName; }
    public String getEmployerAddress() { return employerAddress; }

    @Override
    public String toString() {
        return "IndividualCustomer{" +
                "customerId='" + getCustomerId() + '\'' +
                ", name='" + getFullName() + '\'' +
                ", idNumber='" + idNumber + '\'' +
                ", employed=" + employed +
                '}';
    }
}