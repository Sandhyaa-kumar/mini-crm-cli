package crm;

public class Client {
    private String name;
    private String email;
    private String phone;
    private String convertedOn;
    private String priority;

    public Client(String name, String email, String phone, String convertedOn, String priority) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.convertedOn = convertedOn;
        this.priority = priority;
    }

    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getConvertedOn() { return convertedOn; }
    public String getPriority() { return priority; }

    @Override
    public String toString() {
        return "Client: " + name + ", Email: " + email + ", Phone: " + phone +
               ", Converted On: " + convertedOn + ", Priority: " + priority;
    }
}
