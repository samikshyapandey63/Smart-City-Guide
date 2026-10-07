import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class RecommendationDAO {

    // searches and views older than this many days are ignored
    public static final int HISTORY_DAYS = 60;

    // a category is dropped completely when the user's own ratings in it add up to this or less
    // (two 1-star ratings = -6, or three 2-star ratings)
    public static final int DROP_BAD_POINTS = -6;

    // ...or when its total score falls to this (many dislikes and almost no interest)
    public static final int DROP_SCORE = -4;

    // no more than this many cards of the same category in the personal picks
    public static final int MAX_PER_CATEGORY = 3;


    // ================= SAVE WHAT THE USER DOES =================

    // categoryName can be null (a search that matched nothing)
    public void logSearch(int userId, String city, String text, String categoryName)
            throws SQLException {

        if (text.length() > 100) {
            text = text.substring(0, 100);
        }

        Connection con = DatabaseConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(
                "INSERT INTO user_activity (user_id, action, city, search_text, category_id) "
                        + "VALUES (?, 'SEARCH', ?, ?, (SELECT id FROM categories WHERE name = ?))");
        ps.setInt(1, userId);
        ps.setString(2, city);
        ps.setString(3, text.isEmpty() ? null : text);
        ps.setString(4, categoryName);
        ps.executeUpdate();
        con.close();
    }

    // the user opened this place's details
    public void logView(int userId, int placeId) throws SQLException {
        Connection con = DatabaseConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(
                "INSERT INTO user_activity (user_id, action, city, place_id, category_id) "
                        + "SELECT ?, 'VIEW', p.city, p.id, p.category_id FROM places p WHERE p.id = ?");
        ps.setInt(1, userId);
        ps.setInt(2, placeId);
        ps.executeUpdate();
        con.close();
    }

    // "Hide": this place is never recommended to this user again
    public void hidePlace(int userId, int placeId) throws SQLException {
        Connection con = DatabaseConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(
                "INSERT IGNORE INTO hidden_places (user_id, place_id) VALUES (?, ?)");
        ps.setInt(1, userId);
        ps.setInt(2, placeId);
        ps.executeUpdate();
        con.close();
    }

    // forgets searches, views and hidden places (favorites and reviews stay)
    public void clearHistory(int userId) throws SQLException {
        Connection con = DatabaseConnection.getConnection();

        PreparedStatement ps = con.prepareStatement("DELETE FROM user_activity WHERE user_id = ?");
        ps.setInt(1, userId);
        ps.executeUpdate();

        ps = con.prepareStatement("DELETE FROM hidden_places WHERE user_id = ?");
        ps.setInt(1, userId);
        ps.executeUpdate();

        con.close();
    }

    // the category that appears most in a list of places (used to learn from a text search)
    public static String mostCommonCategory(ArrayList<Place> places) {
        HashMap<String, Integer> counts = new HashMap<>();
        String best = null;
        int bestCount = 0;

        for (Place p : places) {
            int count = counts.getOrDefault(p.getCategory(), 0) + 1;
            counts.put(p.getCategory(), count);
            if (count > bestCount) {
                bestCount = count;
                best = p.getCategory();
            }
        }
        return best;
    }


    // ================= WORK OUT THE RECOMMENDATIONS =================

    public ArrayList<Recommendation> getRecommendations(int userId, String city, int limit)
            throws SQLException {

        HashMap<String, int[]> stats = loadCategoryStats(userId);
        ArrayList<Place> candidates = loadCandidates(userId, city);

        ArrayList<Recommendation> personal = new ArrayList<>();
        ArrayList<Recommendation> popular = new ArrayList<>();

        for (Place p : candidates) {

            // a place most visitors dislike is never recommended
            if (p.getReviewCount() >= 3 && p.getAvgRating() < 2.5) {
                continue;
            }

            int[] s = stats.get(p.getCategory());
            int score = 0;
            if (s != null) {
                score = categoryScore(s);
            }

            // the user kept disliking this category: leave it out
            if (score <= DROP_SCORE) {
                continue;
            }
            if (s != null && s[3] <= DROP_BAD_POINTS) {
                continue;
            }

            if (score > 0) {
                personal.add(new Recommendation(p, makeReason(s, p.getCategory()), score, true));
            } else {
                String reason = p.getReviewCount() > 0
                        ? "Top rated in " + city
                        : "Discover in " + city;
                popular.add(new Recommendation(p, reason, 0, false));
            }
        }

        // best personal picks first
        Collections.sort(personal, (a, b) -> {
            if (a.getScore() != b.getScore()) {
                return b.getScore() - a.getScore();
            }
            return Double.compare(b.getPlace().getAvgRating(), a.getPlace().getAvgRating());
        });

        // best rated, most reviewed first
        Collections.sort(popular, (a, b) -> {
            int byRating = Double.compare(b.getPlace().getAvgRating(), a.getPlace().getAvgRating());
            if (byRating != 0) {
                return byRating;
            }
            return b.getPlace().getReviewCount() - a.getPlace().getReviewCount();
        });

        ArrayList<Recommendation> result = new ArrayList<>();
        HashMap<String, Integer> perCategory = new HashMap<>();

        // 1) personal picks, but not too many of one category
        for (Recommendation r : personal) {
            if (result.size() >= limit) {
                break;
            }
            String category = r.getPlace().getCategory();
            int used = perCategory.getOrDefault(category, 0);
            if (used < MAX_PER_CATEGORY) {
                result.add(r);
                perCategory.put(category, used + 1);
            }
        }

        // 2) fill the rest with popular places
        for (Recommendation r : popular) {
            if (result.size() >= limit) {
                break;
            }
            result.add(r);
        }
        return result;
    }


    // category score = searches and views + 3 per favorite + points from the user's own ratings
    public static int categoryScore(int[] s) {
        return s[0] + 3 * s[1] + s[2] + s[3];
    }

    // the strongest reason this category was picked
    private String makeReason(int[] s, String category) {
        int activityPoints = s[0];
        int favoritePoints = 3 * s[1];
        int ratingPoints = s[2];

        if (favoritePoints >= ratingPoints && favoritePoints >= activityPoints) {
            return "Because you saved " + category + " places";
        }
        if (ratingPoints >= activityPoints) {
            return "Because you rated " + category + " places highly";
        }
        return "Because you searched for " + category + " places";
    }


    // category name -> {searches and views, favorites, good-rating points, bad-rating points (negative)}
    public HashMap<String, int[]> loadCategoryStats(int userId) throws SQLException {
        HashMap<String, int[]> stats = new HashMap<>();
        Connection con = DatabaseConnection.getConnection();

        // searches and views of the last HISTORY_DAYS days
        PreparedStatement ps = con.prepareStatement(
                "SELECT c.name, COUNT(*) AS total "
                        + "FROM user_activity a JOIN categories c ON a.category_id = c.id "
                        + "WHERE a.user_id = ? AND a.created_at >= (NOW() - INTERVAL " + HISTORY_DAYS + " DAY) "
                        + "GROUP BY c.name");
        ps.setInt(1, userId);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            statsFor(stats, rs.getString("name"))[0] = rs.getInt("total");
        }

        // favorites
        ps = con.prepareStatement(
                "SELECT c.name, COUNT(*) AS total "
                        + "FROM favorites f JOIN places p ON f.place_id = p.id "
                        + "JOIN categories c ON p.category_id = c.id "
                        + "WHERE f.user_id = ? GROUP BY c.name");
        ps.setInt(1, userId);
        rs = ps.executeQuery();
        while (rs.next()) {
            statsFor(stats, rs.getString("name"))[1] = rs.getInt("total");
        }

        // the user's own ratings: 5 = +3, 4 = +2, 3 = 0, 2 = -2, 1 = -3
        ps = con.prepareStatement(
                "SELECT c.name, r.rating "
                        + "FROM reviews r JOIN places p ON r.place_id = p.id "
                        + "JOIN categories c ON p.category_id = c.id "
                        + "WHERE r.user_id = ?");
        ps.setInt(1, userId);
        rs = ps.executeQuery();
        while (rs.next()) {
            int[] s = statsFor(stats, rs.getString("name"));
            int rating = rs.getInt("rating");

            if (rating == 5) {
                s[2] += 3;
            } else if (rating == 4) {
                s[2] += 2;
            } else if (rating == 2) {
                s[3] -= 2;
            } else if (rating == 1) {
                s[3] -= 3;
            }
        }

        con.close();
        return stats;
    }

    private int[] statsFor(HashMap<String, int[]> stats, String category) {
        if (!stats.containsKey(category)) {
            stats.put(category, new int[4]);
        }
        return stats.get(category);
    }


    // places in the city this user has not hidden, rated or saved yet
    private ArrayList<Place> loadCandidates(int userId, String city) throws SQLException {
        ArrayList<Place> list = new ArrayList<>();

        Connection con = DatabaseConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(
                PlaceDAO.SELECT_COLUMNS
                        + "WHERE p.city = ? "
                        + "AND p.id NOT IN (SELECT place_id FROM hidden_places WHERE user_id = ?) "
                        + "AND p.id NOT IN (SELECT place_id FROM reviews WHERE user_id = ?) "
                        + "AND p.id NOT IN (SELECT place_id FROM favorites WHERE user_id = ?) "
                        + PlaceDAO.GROUP_AND_ORDER);
        ps.setString(1, city);
        ps.setInt(2, userId);
        ps.setInt(3, userId);
        ps.setInt(4, userId);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            list.add(PlaceDAO.fromRow(rs));
        }
        con.close();
        return list;
    }
}
