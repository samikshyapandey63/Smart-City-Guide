import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class FavoriteDAO {

    public boolean isFavorite(int userId, int placeId) throws SQLException {
        Connection con = DatabaseConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(
                "SELECT place_id FROM favorites WHERE user_id = ? AND place_id = ?");
        ps.setInt(1, userId);
        ps.setInt(2, placeId);
        ResultSet rs = ps.executeQuery();
        boolean found = rs.next();
        con.close();
        return found;
    }

    public void addFavorite(int userId, int placeId) throws SQLException {
        Connection con = DatabaseConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(
                "INSERT INTO favorites (user_id, place_id) VALUES (?, ?)");
        ps.setInt(1, userId);
        ps.setInt(2, placeId);
        ps.executeUpdate();
        con.close();
    }

    public void removeFavorite(int userId, int placeId) throws SQLException {
        Connection con = DatabaseConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(
                "DELETE FROM favorites WHERE user_id = ? AND place_id = ?");
        ps.setInt(1, userId);
        ps.setInt(2, placeId);
        ps.executeUpdate();
        con.close();
    }

    // all places this user saved (from every city), with their average rating
    public ArrayList<Place> getFavorites(int userId) throws SQLException {
        ArrayList<Place> list = new ArrayList<>();

        Connection con = DatabaseConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(
                PlaceDAO.SELECT_COLUMNS
                        + "JOIN favorites f ON f.place_id = p.id "
                        + "WHERE f.user_id = ? "
                        + PlaceDAO.GROUP_AND_ORDER);
        ps.setInt(1, userId);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            list.add(PlaceDAO.fromRow(rs));
        }
        con.close();
        return list;
    }
}
