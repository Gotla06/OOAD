public class CompanyCustomer extends Customer {
    private String registrationNumber;
    private String contactPerson;

    public CompanyCustomer(String customerId, String companyName, String registrationNumber,
                           String address, String contactPerson) {
        super(customerId, companyName, "", address);
        this.registrationNumber = registrationNumber;
        this.contactPerson = contactPerson;
    }

    @Override
    public boolean canOpenAccount(AccountType accountType) {
        // Companies can open any type of account
        return true;
    }

    @Override
    public String getCustomerType() {
        return "Company";
    }

    @Override
    public String getFullName() {
        return getFirstName(); // Company name is stored in firstName
    }

    // Getters
    public String getRegistrationNumber() { return registrationNumber; }
    public String getContactPerson() { return contactPerson; }

    @Override
    public String toString() {
        return "CompanyCustomer{" +
                "customerId='" + getCustomerId() + '\'' +
                ", companyName='" + getFullName() + '\'' +
                ", registrationNumber='" + registrationNumber + '\'' +
                '}';
    }
}