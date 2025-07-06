package crm;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LeadService {

    private static final String URL = "jdbc:mysql://localhost:3306/mini_crm_db";
    private static final String USER = "root";
    private static final String PASS = "sandhyaa";

    // Add a new lead to DB
    public void addLead(String name, String email, String phone, String followUpDate, String leadStatus) {
        if (name == null || name.trim().isEmpty()) {
        System.out.println("Name cannot be empty.");
        return;
    }
    if (!email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
        System.out.println(" Invalid email format.");
        return;
    }
     if (!phone.matches("^\\d{10}$")) {
        System.out.println("Phone number must be exactly 10 digits.");
        return;
    }
     LocalDate today = LocalDate.now();
    LocalDate followDate = LocalDate.parse(followUpDate);

    if (followDate.isBefore(today)) {
        System.out.println(" Follow-up date must be today or in the future.");
        return;
    }
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
                    System.out.println("Lead Found:\n" + formatLead(rs));
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
    String updateStatusSQL = "UPDATE crm_leads SET lead_status = 'Converted' WHERE email = ? OR phone = ?";

    try (Connection con = DriverManager.getConnection(URL, USER, PASS);
         PreparedStatement fetch = con.prepareStatement(fetchSQL);
         PreparedStatement insert = con.prepareStatement(insertSQL);
         PreparedStatement delete = con.prepareStatement(deleteSQL);
         PreparedStatement updateStatus = con.prepareStatement(updateStatusSQL)) {

        fetch.setString(1, key);
        fetch.setString(2, key);

        try (ResultSet rs = fetch.executeQuery()) {
            if (rs.next()) {
                String name = rs.getString("name");
                String email = rs.getString("email");
                String phone = rs.getString("phone");
                String priority = rs.getString("lead_status");

                // Insert into client table
                insert.setString(1, name);
                insert.setString(2, email);
                insert.setString(3, phone);
                insert.setString(4, date);
                insert.setString(5, priority);
                insert.executeUpdate();

                // Update lead status to Converted
                updateStatus.setString(1, key);
                updateStatus.setString(2, key);
                updateStatus.executeUpdate();

                // Delete from leads
                delete.setString(1, key);
                delete.setString(2, key);
                delete.executeUpdate();

                System.out.println("Lead converted to client and status updated to 'Converted'.");
            } else {
                System.out.println("Lead not found to convert.");
            }
        }

    } catch (SQLException e) {
        System.out.println("SQL Error during lead conversion:");
        e.printStackTrace();
    }
}

    // Auto-snooze missed follow-ups (set to next day)
public void autoSnoozeMissedFollowUps() {
    String selectSql = "SELECT * FROM crm_leads WHERE follow_up_date < CURDATE()";
    String updateSql = "UPDATE crm_leads SET follow_up_date = DATE_ADD(CURDATE(), INTERVAL 1 DAY) WHERE id = ?";

    try (Connection con = DriverManager.getConnection(URL, USER, PASS);
         PreparedStatement selectPst = con.prepareStatement(selectSql);
         ResultSet rs = selectPst.executeQuery()) {

        List<String> snoozedLeads = new ArrayList<>();
        int count = 0;

        while (rs.next()) {
            int leadId = rs.getInt("id");

            // Store lead details to print later
            snoozedLeads.add("Lead " + (count + 1) + ": " + formatLead(rs));

            // Update follow-up date to tomorrow
            try (PreparedStatement updatePst = con.prepareStatement(updateSql)) {
                updatePst.setInt(1, leadId);
                updatePst.executeUpdate();
                count++;
            }
        }

        if (count == 0) {
            System.out.println("No missed follow-ups today.");
        } else {
            System.out.println(" Auto-snoozed leads with missed follow-ups:");
            for (String lead : snoozedLeads) {
                System.out.println(lead);
            }
            System.out.println(" Total " + count + " leads snoozed to tomorrow.");
        }

    } catch (SQLException e) {
        System.out.println(" SQL Error while auto-snoozing:");
        e.printStackTrace();
    }
}
//updatelead
public void updateLead(String key, String newName, String newPhone, String newDate, String newStatus) {
    String sql = "UPDATE crm_leads SET name = ?, phone = ?, follow_up_date = ?, lead_status = ? WHERE email = ? OR phone = ?";

    try (Connection con = DriverManager.getConnection(URL, USER, PASS);
         PreparedStatement pst = con.prepareStatement(sql)) {

        pst.setString(1, newName);
        pst.setString(2, newPhone);
        pst.setString(3, newDate);
        pst.setString(4, newStatus);
        pst.setString(5, key);
        pst.setString(6, key);

        int rows = pst.executeUpdate();
        if (rows > 0) {
            System.out.println("Lead updated successfully.");
        } else {
            System.out.println("Lead not found.");
        }

    } catch (SQLException e) {
        System.out.println("SQL Error while updating lead:");
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
