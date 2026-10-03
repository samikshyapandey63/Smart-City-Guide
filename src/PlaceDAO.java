import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class PlaceDAO {

    // places whose name contains the text, optionally only one category ("All" = every category)
    public ArrayList<Place> searchPlaces(String text, String category) throws SQLException {
        ArrayList<Place> list = new ArrayList<>();
        boolean allCategories = category.equals("All");

        String sql = "SELECT p.id, p.name, c.name AS category, p.area, p.address, p.phone, "
                + "p.open_time, p.close_time, p.description, "
                + "IFNULL(AVG(r.rating), 0) AS avg_rating, COUNT(r.id) AS review_count "
                + "FROM places p "
                + "JOIN categories c ON p.category_id = c.id "
                + "LEFT JOIN reviews r ON r.place_id = p.id "
                + "WHERE p.name LIKE ? "
                + (allCategories ? "" : "AND c.name = ? ")
                + "GROUP BY p.id, p.name, c.name, p.area, p.address, p.phone, "
                + "p.open_time, p.close_time, p.description "
                + "ORDER BY p.name";

        Connection con = DatabaseConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, "%" + text + "%");
        if (!allCategories) {
            ps.setString(2, category);
        }
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            Place p = new Place(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("category"),
                    rs.getString("area"),
                    rs.getString("address"),
                    rs.getString("phone"),
                    rs.getTime("open_time").toString().substring(0, 5),    // 08:00:00 -> 08:00
                    rs.getTime("close_time").toString().substring(0, 5),
                    rs.getString("description"),
                    rs.getDouble("avg_rating"),
                    rs.getInt("review_count"));
            list.add(p);
        }
        con.close();
        return list;
    }

    // names for the category drop-down
    public ArrayList<String> getCategoryNames() throws SQLException {
        ArrayList<String> names = new ArrayList<>();

        Connection con = DatabaseConnection.getConnection();
        PreparedStatement ps = con.prepareStatement("SELECT name FROM categories ORDER BY name");
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            names.add(rs.getString("name"));
        }
        con.close();
        return names;
    }
}
