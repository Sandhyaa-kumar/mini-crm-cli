package crm;

import java.sql.*;

public class ClientService {

    public void viewAllClients() {
        String sql = "SELECT * FROM crm_clients";
        try (Connection con = DBConnection.connect();
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

    public void searchClient(String key) {
        String sql = "SELECT * FROM crm_clients WHERE email = ? OR phone = ?";
        try (Connection con = DBConnection.connect();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, key);
            pst.setString(2, key);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    System.out.println("Client Found:\n" + formatClient(rs));
                } else {
                    System.out.println("Client not found with given email/phone.");
                }
            }

        } catch (SQLException e) {
            System.out.println("SQL Error while searching client:");
            e.printStackTrace();
        }
    }

    public void deleteClient(String key) {
        String sql = "DELETE FROM crm_clients WHERE email = ? OR phone = ?";
        try (Connection con = DBConnection.connect();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, key);
            pst.setString(2, key);

            int rows = pst.executeUpdate();
            if (rows > 0) {
                System.out.println("Client deleted successfully.");
            } else {
                System.out.println("Client not found with given email/phone.");
            }

        } catch (SQLException e) {
            System.out.println("SQL Error while deleting client:");
            e.printStackTrace();
        }
    }

    private String formatClient(ResultSet rs) throws SQLException {
        return "ID: " + rs.getInt("id") +
               ", Name: " + rs.getString("name") +
               ", Email: " + rs.getString("email") +
               ", Phone: " + rs.getString("phone") +
               ", Priority: " + rs.getString("priority") +
               ", Converted On: " + rs.getString("converted_on") +
               ", Created: " + rs.getString("created_at");
    }
}
