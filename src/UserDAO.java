import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class UserDAO {

    public User login(String username, String password)
            throws InvalidLoginException, SQLException {

        Connection con = DatabaseConnection.getConnection();

        PreparedStatement ps = con.prepareStatement(
                "SELECT * FROM users WHERE username = ?"
        );

        ps.setString(1, username);

        ResultSet rs = ps.executeQuery();

        // Username does not exist
        if (!rs.next()) {
            con.close();
            throw new InvalidLoginException("USERNAME_NOT_FOUND");
        }

        int failedAttempts = rs.getInt("failed_attempts");
        Timestamp lockedUntil = rs.getTimestamp("locked_until");

        // Check if account is currently locked
        if (lockedUntil != null) {

            long remaining =
                    lockedUntil.getTime() - System.currentTimeMillis();

            if (remaining > 0) {

                con.close();

                long seconds = (remaining + 999) / 1000;

                throw new InvalidLoginException(
                        "LOCKED:" + seconds
                );
            }

            // Lock time has expired, reset attempts
            failedAttempts = 0;

            PreparedStatement resetPs = con.prepareStatement(
                    "UPDATE users SET failed_attempts = 0, " +
                            "locked_until = NULL " +
                            "WHERE username = ?"
            );

            resetPs.setString(1, username);
            resetPs.executeUpdate();
            resetPs.close();
        }

        // Check password
        String correctPassword = rs.getString("password");

        if (!correctPassword.equals(password)) {

            failedAttempts++;

            // 3 wrong attempts → lock for 30 seconds
            if (failedAttempts >= 3) {

                PreparedStatement lockPs = con.prepareStatement(
                        "UPDATE users SET failed_attempts = 3, " +
                                "locked_until = DATE_ADD(NOW(), INTERVAL 30 SECOND) " +
                                "WHERE username = ?"
                );

                lockPs.setString(1, username);

                lockPs.executeUpdate();
                lockPs.close();

                con.close();

                throw new InvalidLoginException("LOCKED:30");

            } else {

                PreparedStatement updatePs = con.prepareStatement(
                        "UPDATE users SET failed_attempts = ? " +
                                "WHERE username = ?"
                );

                updatePs.setInt(1, failedAttempts);
                updatePs.setString(2, username);

                updatePs.executeUpdate();
                updatePs.close();

                con.close();

                throw new InvalidLoginException(
                        "WRONG_PASSWORD:" + (3 - failedAttempts)
                );
            }
        }

        // Correct login → reset failed attempts and lock
        PreparedStatement resetPs = con.prepareStatement(
                "UPDATE users SET failed_attempts = 0, " +
                        "locked_until = NULL " +
                        "WHERE username = ?"
        );

        resetPs.setString(1, username);

        resetPs.executeUpdate();
        resetPs.close();

        // Create User object
        User user = new User(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("username"),
                rs.getString("password"),
                rs.getString("role")
        );
//        user.setCity(rs.getString("city"));

        con.close();

        return user;
    }


    public boolean usernameExists(String username) throws SQLException {

        Connection con = DatabaseConnection.getConnection();

        PreparedStatement ps = con.prepareStatement(
                "SELECT id FROM users WHERE username = ?"
        );

        ps.setString(1, username);

        ResultSet rs = ps.executeQuery();

        boolean found = rs.next();

        rs.close();
        ps.close();
        con.close();

        return found;
    }

    public boolean emailExists(String email) throws SQLException {
        Connection con = DatabaseConnection.getConnection();
        PreparedStatement ps = con.prepareStatement("SELECT id FROM users WHERE email = ?");
        ps.setString(1, email);
        ResultSet rs = ps.executeQuery();
        boolean found = rs.next();
        con.close();
        return found;
    }

    public void registerTourist(
            String name,
            String username,
            String email,
            String password
    ) throws SQLException {

        Connection con = DatabaseConnection.getConnection();

        PreparedStatement ps = con.prepareStatement(
                "INSERT INTO users (name, username, email, password, role) VALUES (?, ?, ?, ?, 'TOURIST')"
        );

        ps.setString(1, name);
        ps.setString(2, username);
        ps.setString(3, email);
        ps.setString(4, password);

        ps.executeUpdate();

        ps.close();
        con.close();
    }


    // Check how much lock time is remaining
    public long getRemainingLockSeconds(String username)
            throws SQLException {

        Connection con = DatabaseConnection.getConnection();

        PreparedStatement ps = con.prepareStatement(
                "SELECT locked_until FROM users WHERE username = ?"
        );

        ps.setString(1, username);

        ResultSet rs = ps.executeQuery();

        // Username does not exist
        if (!rs.next()) {

            rs.close();
            ps.close();
            con.close();

            return 0;
        }

        Timestamp lockedUntil = rs.getTimestamp("locked_until");

        rs.close();
        ps.close();
        con.close();

        // User is not locked
        if (lockedUntil == null) {
            return 0;
        }

        long remaining =
                lockedUntil.getTime() - System.currentTimeMillis();

        // Lock has expired
        if (remaining <= 0) {
            return 0;
        }

        // Convert milliseconds to seconds
        return (remaining + 999) / 1000;
    }
}