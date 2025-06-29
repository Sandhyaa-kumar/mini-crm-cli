package crm;

public class Lead {
    private static int idCounter = 1;

    private int id;
    private String name;
    private String email;
    private String phone;
    private String followUpDate;
    private String priority;

    public Lead(String name, String email, String phone, String followUpDate, String priority) {
        this.id = idCounter++;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.followUpDate = followUpDate;
        this.priority = priority;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getFollowUpDate() {
        return followUpDate;
    }

    public String getPriority() {
        return priority;
    }

    public void setFollowUpDate(String followUpDate) {
        this.followUpDate = followUpDate;
    }

    @Override
    public String toString() {
        return "Lead ID: " + id +
                ", Name: " + name +
                ", Email: " + email +
                ", Phone: " + phone +
                ", Follow-up: " + followUpDate +
                ", Priority: " + priority;
    }
}
