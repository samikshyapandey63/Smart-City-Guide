import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnection {

    private static final String URL =
            "jdbc:mysql://localhost:3306/SmartCity";

    private static final String USER = "root";

    private static final String PASSWORD = "WJ28@krhps";

    public static Connection getConnection() {
        try {
            Connection connection = DriverManager.getConnection(
                    URL,
                    USER,
                    PASSWORD
            );

            System.out.println("Database connected successfully!");

            return connection;

        } catch (Exception e) {
            System.out.println("Database connection failed!");
            e.printStackTrace();

            return null;
        }
    }

    public static void main(String[] args) {
        getConnection();
    }
}