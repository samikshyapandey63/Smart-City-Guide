import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class EmergencyDAO {

    // each item is {service, number}, for example {"Police", "100"}
    public ArrayList<String[]> getContacts(String city) throws SQLException {
        ArrayList<String[]> list = new ArrayList<>();

        Connection con = DatabaseConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(
                "SELECT service, phone_number FROM emergency_contacts WHERE city = ? ORDER BY id");
        ps.setString(1, city);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            list.add(new String[]{rs.getString("service"), rs.getString("phone_number")});
        }
        con.close();
        return list;
    }
}
