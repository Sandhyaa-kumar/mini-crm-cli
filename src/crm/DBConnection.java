package crm;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    public void connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");  // Load MySQL driver
            Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/mini_crm_db", "root", "sandhyaa");
            System.out.println("Connected to the database!");  // success message
            con.close();
        } catch (ClassNotFoundException e) {
            System.out.println("MySQL Driver not found");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Failed to connect to database");
            e.printStackTrace();
        }
    }
}
