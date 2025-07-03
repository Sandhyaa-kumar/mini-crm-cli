package crm;

import java.sql.*;

public class LeadService {

    private static final String URL = "jdbc:mysql://localhost:3306/mini_crm_db";
    private static final String USER = "root";
    private static final String PASS = "sandhyaa";

    // Add a new lead to DB
    public void addLead(String name, String email, String phone, String followUpDate, String leadStatus) {
        String sql = "INSERT INTO crm_leads (name, email, phone, lead_source, lead_status, follow_up_date) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement pst = con.prepareStatement(sql)) {

            String leadSource = "Website";
            if (leadStatus == null || leadStatus.isEmpty()) {
                leadStatus = "New";
            }

            pst.setString(1, name);
            pst.setString(2, email);
            pst.setString(3, phone);
            pst.setString(4, leadSource);
            pst.setString(5, leadStatus);
            pst.setString(6, followUpDate);

            int rows = pst.executeUpdate();
            if (rows > 0) {
                System.out.println("Lead added successfully!");
            } else {
                System.out.println("Failed to add lead.");
            }

        } catch (SQLException e) {
            System.out.println("SQL Error while adding lead:");
            e.printStackTrace();
        }

        System.out.println(" Inserted: " + name + ", " + email + ", " + phone + ", Website, " + leadStatus + ", " + followUpDate);
    }

    // View all leads from DB
    public void viewLeads() {
        String sql = "SELECT * FROM crm_leads";
        try (Connection con = DriverManager.getConnection(URL, USER, PASS);
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            boolean empty = true;
            while (rs.next()) {
                empty = false;
                System.out.println(formatLead(rs));
            }
            if (empty) {
                System.out.println(" No leads found.");
            }

        } catch (SQLException e) {
            System.out.println("SQL Error while fetching leads:");
            e.printStackTrace();
        }
    }

    // Search lead by email or phone
    public void searchLead(String key) {
        String sql = "SELECT * FROM crm_leads WHERE email = ? OR phone = ?";
        try (Connection con = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, key);
            pst.setString(2, key);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    System.out.println("🔍 Lead Found:\n" + formatLead(rs));
                } else {
                    System.out.println("Lead not found with given email/phone.");
                }
            }

        } catch (SQLException e) {
            System.out.println("SQL Error while searching lead:");
            e.printStackTrace();
        }
    }

    // View today's follow-ups
    public void viewTodayFollowUps(String todayDate) {
        String sql = "SELECT * FROM crm_leads WHERE follow_up_date = ?";
        try (Connection con = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, todayDate);

            try (ResultSet rs = pst.executeQuery()) {
                boolean any = false;
                while (rs.next()) {
                    any = true;
                    System.out.println("📆 Follow-up Today: " + formatLead(rs));
                }
                if (!any) {
                    System.out.println("🕊️ No follow-ups for today.");
                }
            }

        } catch (SQLException e) {
            System.out.println("SQL Error while checking follow-ups:");
            e.printStackTrace();
        }
    }

    // Convert a lead to client
    public void convertLeadToClient(String key, String date) {
        String fetchSQL = "SELECT * FROM crm_leads WHERE email = ? OR phone = ?";
        String insertSQL = "INSERT INTO crm_clients (name, email, phone, converted_on, priority) VALUES (?, ?, ?, ?, ?)";
        String deleteSQL = "DELETE FROM crm_leads WHERE email = ? OR phone = ?";

        try (Connection con = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement fetch = con.prepareStatement(fetchSQL);
             PreparedStatement insert = con.prepareStatement(insertSQL);
             PreparedStatement delete = con.prepareStatement(deleteSQL)) {

            fetch.setString(1, key);
            fetch.setString(2, key);

            try (ResultSet rs = fetch.executeQuery()) {
                if (rs.next()) {
                    String name = rs.getString("name");
                    String email = rs.getString("email");
                    String phone = rs.getString("phone");
                    String priority = rs.getString("lead_status");

                    insert.setString(1, name);
                    insert.setString(2, email);
                    insert.setString(3, phone);
                    insert.setString(4, date);
                    insert.setString(5, priority);
                    insert.executeUpdate();

                    delete.setString(1, key);
                    delete.setString(2, key);
                    delete.executeUpdate();

                    System.out.println(" Lead converted to client!");
                } else {
                    System.out.println(" Lead not found to convert.");
                }
            }

        } catch (SQLException e) {
            System.out.println("SQL Error during lead conversion:");
            e.printStackTrace();
        }
    }

    // Format a lead from ResultSet
    private String formatLead(ResultSet rs) throws SQLException {
        return "ID: " + rs.getInt("id") +
                ", Name: " + rs.getString("name") +
                ", Email: " + rs.getString("email") +
                ", Phone: " + rs.getString("phone") +
                ", Lead Source: " + rs.getString("lead_source") +
                ", Status: " + rs.getString("lead_status") +
                ", Follow-up: " + rs.getString("follow_up_date") +
                ", Created: " + rs.getString("created_at");
    }
}
