import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseConnection {
    static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/SmartCity";
        String user = "root";
        String password = "WJ28@krhps";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement()) {

            // Example: Execute a query
            ResultSet rs = stmt.executeQuery("SELECT * FROM places");
            while (rs.next()) {
                System.out.println(rs.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}