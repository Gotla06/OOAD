public class CompanyCustomer extends Customer {
    private String companyName;
    private String registrationNumber;
    private String companyAddress;
    private String contactPerson;

    public CompanyCustomer(String customerId, String companyName, String registrationNumber,
                           String companyAddress, String contactPerson) {
        super(customerId, "", "", companyAddress);
        this.companyName = companyName;
        this.registrationNumber = registrationNumber;
        this.companyAddress = companyAddress;
        this.contactPerson = contactPerson;
    }

    @Override
    public boolean canOpenAccount(AccountType accountType) {
        // Companies can open any type of account
        return true;
    }

    @Override
    public String getFullName() {
        return companyName;
    }

    // Getters and setters
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getRegistrationNumber() { return registrationNumber; }
    public void setRegistrationNumber(String registrationNumber) { this.registrationNumber = registrationNumber; }

    public String getCompanyAddress() { return companyAddress; }
    public void setCompanyAddress(String companyAddress) { this.companyAddress = companyAddress; }

    public String getContactPerson() { return contactPerson; }
    public void setContactPerson(String contactPerson) { this.contactPerson = contactPerson; }

    @Override
    public String toString() {
        return "CompanyCustomer{" +
                "customerId='" + getCustomerId() + '\'' +
                ", companyName='" + companyName + '\'' +
                ", registrationNumber='" + registrationNumber + '\'' +
                '}';
    }
}