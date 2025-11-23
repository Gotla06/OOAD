import java.util.Date;

public abstract class Customer {
    private String customerId;
    private String firstName;
    private String surname;
    private String address;
    private String phoneNumber;
    private String email;
    private Date registrationDate;

    public Customer(String customerId, String firstName, String surname, String address) {
        this.customerId = customerId;
        this.firstName = firstName;
        this.surname = surname;
        this.address = address;
        this.registrationDate = new Date();
    }

    public abstract boolean canOpenAccount(AccountType accountType);
    public abstract String getCustomerType();

    public String getCustomerId() { return customerId; }
    public String getFirstName() { return firstName; }
    public String getSurname() { return surname; }
    public String getAddress() { return address; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Date getRegistrationDate() { return registrationDate; }

    public String getFullName() {
        return firstName + " " + surname;
    }

    public String getName() {
        return getFullName();
    }

    @Override
    public String toString() {
        return getCustomerType() + "{" +
                "customerId='" + customerId + '\'' +
                ", name='" + getFullName() + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}