import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;

public class SuggestionDAO {

    // =========================================================
    // SUBMIT SUGGESTION
    // =========================================================

    // saved with status PENDING until an admin approves or rejects it
    public void submit(int userId, String name, int categoryId, String city, String area,
                       String address, String phone, String openTime, String closeTime,
                       String description) throws SQLException {

        Connection con = DatabaseConnection.getConnection();

        PreparedStatement ps = con.prepareStatement(
                "INSERT INTO suggestions (user_id, name, category_id, city, area, address, phone, "
                        + "open_time, close_time, description) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
        );

        ps.setInt(1, userId);
        ps.setString(2, name);
        ps.setInt(3, categoryId);
        ps.setString(4, city);
        ps.setString(5, area);
        ps.setString(6, address);
        ps.setString(7, phone);
        ps.setTime(8, Time.valueOf(openTime + ":00"));
        ps.setTime(9, Time.valueOf(closeTime + ":00"));
        ps.setString(10, description);

        ps.executeUpdate();

        ps.close();
        con.close();
    }


    // =========================================================
    // CHECK PENDING DUPLICATE PLACE
    // =========================================================

    // true if the same place is already waiting for approval
    public boolean pendingExists(String name, String address, String city) throws SQLException {

        Connection con = DatabaseConnection.getConnection();

        PreparedStatement ps = con.prepareStatement(
                "SELECT id FROM suggestions " +
                        "WHERE name = ? AND address = ? AND city = ? AND status = 'PENDING'"
        );

        ps.setString(1, name);
        ps.setString(2, address);
        ps.setString(3, city);

        ResultSet rs = ps.executeQuery();

        boolean found = rs.next();

        rs.close();
        ps.close();
        con.close();

        return found;
    }

    // true if this phone number has already been used
    // in any suggestion
    public boolean phoneExists(String phone) throws SQLException {

        Connection con = DatabaseConnection.getConnection();

        PreparedStatement ps = con.prepareStatement(
                "SELECT id FROM suggestions WHERE phone = ?"
        );

        ps.setString(1, phone);

        ResultSet rs = ps.executeQuery();

        boolean found = rs.next();

        rs.close();
        ps.close();
        con.close();

        return found;
    }
}