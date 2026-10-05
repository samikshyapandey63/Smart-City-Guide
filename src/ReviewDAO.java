import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ReviewDAO {

    // ================= ADD OR UPDATE REVIEW =================

    public void saveReview(int placeId, int userId, int rating, String comment)
            throws SQLException {

        String sql = "INSERT INTO reviews (place_id, user_id, rating, comment) "
                + "VALUES (?, ?, ?, ?) "
                + "ON DUPLICATE KEY UPDATE "
                + "rating = VALUES(rating), "
                + "comment = VALUES(comment)";

        Connection con = DatabaseConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(sql);

        ps.setInt(1, placeId);
        ps.setInt(2, userId);
        ps.setInt(3, rating);
        ps.setString(4, comment);

        ps.executeUpdate();

        con.close();
    }

    // ================= GET REVIEWS =================

    public ArrayList<String[]> getReviews(int placeId)
            throws SQLException {

        ArrayList<String[]> reviews = new ArrayList<>();

        String sql = "SELECT u.name, r.rating, r.comment, r.created_at "
                + "FROM reviews r "
                + "JOIN users u ON r.user_id = u.id "
                + "WHERE r.place_id = ? "
                + "ORDER BY r.created_at DESC";

        Connection con = DatabaseConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(sql);

        ps.setInt(1, placeId);

        ResultSet rs = ps.executeQuery();

        while (rs.next()) {

            String name = rs.getString("name");
            String rating = String.valueOf(rs.getInt("rating"));
            String comment = rs.getString("comment");
            String date = rs.getTimestamp("created_at").toString();

            reviews.add(new String[]{
                    name,
                    rating,
                    comment,
                    date
            });
        }

        con.close();

        return reviews;
    }
}
