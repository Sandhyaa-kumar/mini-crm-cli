package crm;

import java.sql.*;

public class ClientService {

    private static final String URL = "jdbc:mysql://localhost:3306/mini_crm_db";
    private static final String USER = "root";
    private static final String PASS = "sandhyaa";

    // View all clients
    public void viewAllClients() {
        String sql = "SELECT * FROM crm_clients";
        try (Connection con = DriverManager.getConnection(URL, USER, PASS);
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            boolean empty = true;
            while (rs.next()) {
                empty = false;
                System.out.println(formatClient(rs));
            }

            if (empty) {
                System.out.println("No clients found.");
            }

        } catch (SQLException e) {
            System.out.println("SQL Error while fetching clients:");
            e.printStackTrace();
        }
    }

    // Search client by email or phone
    public void searchClient(String key) {
        String sql = "SELECT * FROM crm_clients WHERE email = ? OR phone = ?";
        try (Connection con = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, key);
            pst.setString(2, key);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    System.out.println("Client Found:\n" + formatClient(rs));
                } else {
                    System.out.println("Client not found.");
                }
            }

        } catch (SQLException e) {
            System.out.println("SQL Error while searching client:");
            e.printStackTrace();
        }
    }

    // Delete client by email or phone
    public void deleteClient(String key) {
        String sql = "DELETE FROM crm_clients WHERE email = ? OR phone = ?";
        try (Connection con = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, key);
            pst.setString(2, key);

            int rows = pst.executeUpdate();
            if (rows > 0) {
                System.out.println(" Client deleted successfully.");
            } else {
                System.out.println(" No client found with that detail.");
            }

        } catch (SQLException e) {
            System.out.println("SQL Error while deleting client:");
            e.printStackTrace();
        }
    }

    // Format output for printing client
    private String formatClient(ResultSet rs) throws SQLException {
        return "ID: " + rs.getInt("id") +
                ", Name: " + rs.getString("name") +
                ", Email: " + rs.getString("email") +
                ", Phone: " + rs.getString("phone") +
                ", Converted On: " + rs.getDate("converted_on") +
                ", Priority: " + rs.getString("priority");
    }
}
