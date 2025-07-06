package crm;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class LeadService {

    private static final String URL = "jdbc:mysql://localhost:3306/mini_crm_db";
    private static final String USER = "root";
    private static final String PASS = "sandhyaa";

    // Add a new lead
    public void addLead(String name, String email, String phone, String followUpDate) {
        if (name == null || name.trim().isEmpty()) {
            System.out.println("Name cannot be empty.");
            return;
        }
        if (!email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            System.out.println("Invalid email format.");
            return;
        }
        if (!phone.matches("^\\d{10}$")) {
            System.out.println("Phone number must be exactly 10 digits.");
            return;
        }

        LocalDate today = LocalDate.now();
        LocalDate followDate = LocalDate.parse(followUpDate);
        if (followDate.isBefore(today)) {
            System.out.println("Follow-up date must be today or in the future.");
            return;
        }

        String sql = "INSERT INTO crm_leads (name, email, phone, lead_source, lead_status, follow_up_date) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection con = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, name);
            pst.setString(2, email);
            pst.setString(3, phone);
            pst.setString(4, "Website");
            pst.setString(5, "New"); // Default status
            pst.setString(6, followUpDate);

            int rows = pst.executeUpdate();
            if (rows > 0) {
                System.out.println("Lead added successfully.");
            } else {
                System.out.println("Failed to add lead.");
            }

        } catch (SQLException e) {
            System.out.println("SQL Error while adding lead:");
            e.printStackTrace();
        }
    }

    // View all leads
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
                System.out.println("No leads found.");
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
public void viewTodayFollowUps() {
        String sql = "SELECT * FROM crm_leads WHERE follow_up_date = ?";
        try (Connection con = DriverManager.getConnection(URL, USER, PASS);
                 PreparedStatement pst = con.prepareStatement(sql)) {
         String todayDate = LocalDate.now().toString();

            pst.setString(1, todayDate);

            try (ResultSet rs = pst.executeQuery()) {
                boolean any = false;
                while (rs.next()) {
                    any = true;
                    System.out.println("Follow-up Today: " + formatLead(rs));
                }
                if (!any) {
                    System.out.println("No follow-ups for today.");
                }
            }

        } catch (SQLException e) {
            System.out.println("SQL Error while checking follow-ups:");
            e.printStackTrace();
        }
    }

    // Convert a lead to client
   public void convertLeadToClient(String key) {
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
                String today = LocalDate.now().toString();

                insert.setString(1, name);
                insert.setString(2, email);
                insert.setString(3, phone);
                insert.setString(4, today); // auto date
                insert.setString(5, priority);
                insert.executeUpdate();

                updateStatus.setString(1, key);
                updateStatus.setString(2, key);
                updateStatus.executeUpdate();

                delete.setString(1, key);
                delete.setString(2, key);
                delete.executeUpdate();

                System.out.println("Lead converted to client.");
            } else {
                System.out.println("Lead not found to convert.");
            }
        }

    } catch (SQLException e) {
        System.out.println("SQL Error during lead conversion:");
        e.printStackTrace();
    }
}


    // Auto-snooze missed follow-ups
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
                snoozedLeads.add("Lead " + (count + 1) + ": " + formatLead(rs));

                try (PreparedStatement updatePst = con.prepareStatement(updateSql)) {
                    updatePst.setInt(1, leadId);
                    updatePst.executeUpdate();
                    count++;
                }
            }

            if (count == 0) {
                System.out.println("No missed follow-ups.");
            } else {
                System.out.println("Auto-snoozed missed follow-ups:");
                for (String lead : snoozedLeads) {
                    System.out.println(lead);
                }
                System.out.println("Total " + count + " leads snoozed to tomorrow.");
            }

        } catch (SQLException e) {
            System.out.println("SQL Error while auto-snoozing:");
            e.printStackTrace();
        }
    }

    // Update lead details
  public void updateLead(Scanner scanner) {
    System.out.print("Enter Email or Phone of the lead to update: ");
    String key = scanner.nextLine();

    String fetchSQL = "SELECT * FROM crm_leads WHERE email = ? OR phone = ?";
    String updateSQL = "UPDATE crm_leads SET name = ?, phone = ?, follow_up_date = ?, lead_status = ? WHERE email = ? OR phone = ?";

    try (Connection con = DriverManager.getConnection(URL, USER, PASS);
         PreparedStatement fetch = con.prepareStatement(fetchSQL)) {

        fetch.setString(1, key);
        fetch.setString(2, key);

        try (ResultSet rs = fetch.executeQuery()) {
            if (!rs.next()) {
                System.out.println("Lead not found.");
                return;
            }

            String name = rs.getString("name");
            String phone = rs.getString("phone");
            String date = rs.getString("follow_up_date");
            String status = rs.getString("lead_status");

            while (true) {
                System.out.println("\nCurrent Lead Info:");
                System.out.println("1. Name: " + name);
                System.out.println("2. Phone: " + phone);
                System.out.println("3. Follow-up Date: " + date);
                System.out.println("4. Lead Status: " + status);
                System.out.println("5. Save and Exit");

                System.out.print("Which field do you want to update ? ");
                String choice = scanner.nextLine();

                switch (choice) {
                    case "1":
                        System.out.print("Enter new Name: ");
                        name = scanner.nextLine();
                        break;
                    case "2":
                        System.out.print("Enter new Phone: ");
                        phone = scanner.nextLine();
                        break;
                    case "3":
                        System.out.print("Enter new Follow-up Date (YYYY-MM-DD): ");
                        date = scanner.nextLine();
                        break;
                    case "4":
                        System.out.print("Enter new Lead Status (New/Contacted/Qualified): ");
                        status = scanner.nextLine();
                        break;
                    case "5":
                        try (PreparedStatement update = con.prepareStatement(updateSQL)) {
                            update.setString(1, name);
                            update.setString(2, phone);
                            update.setString(3, date);
                            update.setString(4, status);
                            update.setString(5, key);
                            update.setString(6, key);

                            int rows = update.executeUpdate();
                            if (rows > 0) {
                                System.out.println("Lead updated successfully.");
                            } else {
                                System.out.println("Lead not found.");
                            }
                        }
                        return;
                    default:
                        System.out.println("Invalid option. Try again.");
                }
            }

        }

    } catch (SQLException e) {
        System.out.println("SQL Error while updating lead:");
        e.printStackTrace();
    }
}



    // Format lead for display
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
