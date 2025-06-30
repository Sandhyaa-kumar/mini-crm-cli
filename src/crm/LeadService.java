package crm;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LeadService {

    private static final String URL = "jdbc:mysql://localhost:3306/your_db_name";
    private static final String USER = "root";
    private static final String PASS = "sandhyaa";

    // Add a new lead to DB
    public void addLead(String name, String email, String phone, String followUpDate, String priority) {
        String sql = "INSERT INTO lead (name, email, phone, followup_date, priority) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, name);
            pst.setString(2, email);
            pst.setString(3, phone);
            pst.setString(4, followUpDate);
            pst.setString(5, priority);

            int rows = pst.executeUpdate();
            if (rows > 0) {
                System.out.println("Lead added successfully!");
            } else {
                System.out.println("Failed to add lead.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // View all leads from DB
    public void viewLeads() {
        String sql = "SELECT * FROM lead";
        try (Connection con = DriverManager.getConnection(URL, USER, PASS);
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            boolean empty = true;
            while (rs.next()) {
                empty = false;
                System.out.println(formatLead(rs));
            }
            if (empty) {
                System.out.println("No leads found.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Search lead by email or phone
    public void searchLead(String key) {
        String sql = "SELECT * FROM lead WHERE email = ? OR phone = ?";
        try (Connection con = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, key);
            pst.setString(2, key);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    System.out.println("Lead Found:\n" + formatLead(rs));
                } else {
                    System.out.println("Lead not found with given email/phone.");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // View leads with follow-up date = todayDate
    public void viewTodayFollowUps(String todayDate) {
        String sql = "SELECT * FROM lead WHERE followup_date = ?";
        try (Connection con = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, todayDate);

            try (ResultSet rs = pst.executeQuery()) {
                boolean any = false;
                while (rs.next()) {
                    any = true;
                    System.out.println("Follow-up Today: " + formatLead(rs));
                }
                if (!any) {
                    System.out.println("No follow-ups for today!");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Helper method to format a lead's data from ResultSet
    private String formatLead(ResultSet rs) throws SQLException {
        return "ID: " + rs.getInt("id") + ", Name: " + rs.getString("name") +
               ", Email: " + rs.getString("email") + ", Phone: " + rs.getString("phone") +
               ", Follow-up Date: " + rs.getString("followup_date") + ", Priority: " + rs.getString("priority");
    }
}
