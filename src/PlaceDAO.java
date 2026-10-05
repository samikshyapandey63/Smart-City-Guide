import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class PlaceDAO {

    // the columns every place query needs, plus the average rating (shared with FavoriteDAO)
    public static final String SELECT_COLUMNS =
            "SELECT p.id, p.name, c.name AS category, p.city, p.area, p.address, p.phone, "
                    + "p.open_time, p.close_time, p.description, "
                    + "IFNULL(AVG(r.rating), 0) AS avg_rating, COUNT(r.id) AS review_count "
                    + "FROM places p "
                    + "JOIN categories c ON p.category_id = c.id "
                    + "LEFT JOIN reviews r ON r.place_id = p.id ";

    public static final String GROUP_AND_ORDER =
            "GROUP BY p.id, p.name, c.name, p.city, p.area, p.address, p.phone, "
                    + "p.open_time, p.close_time, p.description "
                    + "ORDER BY p.name";

    // turns the current row of a result into a Place object
    public static Place fromRow(ResultSet rs) throws SQLException {
        return new Place(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("category"),
                rs.getString("city"),
                rs.getString("area"),
                rs.getString("address"),
                rs.getString("phone"),
                rs.getTime("open_time").toString().substring(0, 5),    // 08:00:00 -> 08:00
                rs.getTime("close_time").toString().substring(0, 5),
                rs.getString("description"),
                rs.getDouble("avg_rating"),
                rs.getInt("review_count"));
    }

    // places in one city whose name contains the text, optionally only one category ("All" = every category)
    public ArrayList<Place> searchPlaces(String text, String category, String city) throws SQLException {
        ArrayList<Place> list = new ArrayList<>();
        boolean allCategories = category.equals("All");

        String sql = SELECT_COLUMNS
                + "WHERE p.name LIKE ? AND p.city = ? "
                + (allCategories ? "" : "AND c.name = ? ")
                + GROUP_AND_ORDER;

        Connection con = DatabaseConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, "%" + text + "%");
        ps.setString(2, city);
        if (!allCategories) {
            ps.setString(3, category);
        }
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            list.add(fromRow(rs));
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

    // id of a category by its name, or -1 if there is no such category
    public int getCategoryId(String name) throws SQLException {
        Connection con = DatabaseConnection.getConnection();
        PreparedStatement ps = con.prepareStatement("SELECT id FROM categories WHERE name = ?");
        ps.setString(1, name);
        ResultSet rs = ps.executeQuery();
        int id = -1;
        if (rs.next()) {
            id = rs.getInt("id");
        }
        con.close();
        return id;
    }

    // true if a place with this name and address already exists in the city
    public boolean placeExists(String name, String address, String city) throws SQLException {
        Connection con = DatabaseConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(
                "SELECT id FROM places WHERE name = ? AND address = ? AND city = ?");
        ps.setString(1, name);
        ps.setString(2, address);
        ps.setString(3, city);
        ResultSet rs = ps.executeQuery();
        boolean found = rs.next();
        con.close();
        return found;
    }
}