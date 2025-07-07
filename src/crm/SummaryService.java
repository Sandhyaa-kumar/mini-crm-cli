package crm;

import java.sql.*;
import java.time.LocalDate;

public class SummaryService {
    private static final String URL = "jdbc:mysql://localhost:3306/mini_crm_db";
    private static final String USER = "root";
    private static final String PASS = "sandhyaa";

    public int getTodayLeadCount(LocalDate date) {
        String sql = "SELECT COUNT(*) FROM crm_leads WHERE DATE(created_at) = ?";
        return getCount(sql, date);
    }

    public int getTodayFollowUpsCount(LocalDate date) {
        String sql = "SELECT COUNT(*) FROM crm_leads WHERE follow_up_date = ?";
        return getCount(sql, date);
    }

    public int getMissedFollowUpsCount(LocalDate date) {
        String sql = "SELECT COUNT(*) FROM crm_leads WHERE follow_up_date < ?";
        return getCount(sql, date);
    }

    public int getTodayConvertedClientsCount(LocalDate date) {
        String sql = "SELECT COUNT(*) FROM crm_clients WHERE DATE(converted_on) = ?";
        return getCount(sql, date);
    }

    public int getTodayNewClientsCount(LocalDate date) {
        String sql = "SELECT COUNT(*) FROM crm_clients WHERE DATE(created_at) = ?";
        return getCount(sql, date);
    }

    private int getCount(String sql, LocalDate date) {
        try (Connection con = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, date.toString());

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }

        } catch (SQLException e) {
            System.out.println("SQL Error while fetching count:");
            e.printStackTrace();
        }
        return 0;
    }

    public void displayDailySummary(LocalDate date) {
        System.out.println("\nDaily CRM Summary for: " + date);
        System.out.println("---------------------------------------");
        System.out.println("Total Leads Created Today: " + getTodayLeadCount(date));
        System.out.println("Today's Follow-ups: " + getTodayFollowUpsCount(date));
        System.out.println("Missed Follow-ups: " + getMissedFollowUpsCount(date));
        System.out.println("Leads Converted to Clients Today: " + getTodayConvertedClientsCount(date));
        System.out.println("New Clients Added Today: " + getTodayNewClientsCount(date));
    }

    public void leadStatusBreakdown() {
        String sql = "SELECT lead_status, COUNT(*) as count FROM crm_leads GROUP BY lead_status";

        try (Connection con = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            System.out.println("\nLead Status Summary:");
            while (rs.next()) {
                System.out.println("- " + rs.getString("lead_status") + ": " + rs.getInt("count"));
            }

        } catch (SQLException e) {
            System.out.println("SQL Error while fetching lead status breakdown:");
            e.printStackTrace();
        }
    }

    public void clientsConvertedLast7Days() {
        String sql = "SELECT COUNT(*) FROM crm_clients WHERE converted_on >= DATE_SUB(CURDATE(), INTERVAL 7 DAY)";

        try (Connection con = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            if (rs.next()) {
                System.out.println("\nClients converted in last 7 days: " + rs.getInt(1));
            }

        } catch (SQLException e) {
            System.out.println("SQL Error while fetching weekly client conversions:");
            e.printStackTrace();
        }
    }
}
