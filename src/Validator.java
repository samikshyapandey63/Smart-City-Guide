/**
 * Every check returns an error message, or null when the value is fine.
 * Uses only loops, if statements and String methods.
 */
public class Validator {

    public static String checkName(String name) {
        if (name.equals("")) {
            return "Enter your full name.";
        }
        String[] parts = name.trim().split("\\s+");

        if (parts.length < 2) {
            return "Please enter your full name (first name and last name).";
        }
        if (name.length() > 50) {
            return "Name can't be longer than 50 characters.";
        }
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            if (!Character.isLetter(c) && c != ' ') {
                return "Name can only contain letters and spaces.";
            }
        }
        return null;
    }

    public static String checkUsername(String username) {
        if (username.equals("")) {
            return "Enter a username.";
        }
        if (username.length() < 4 || username.length() > 20) {
            return "Username must be 4 to 20 characters.";
        }
        if (!isEnglishLetter(username.charAt(0))) {
            return "Username must start with a letter.";
        }
        for (int i = 0; i < username.length(); i++) {
            char c = username.charAt(i);
            if (!isEnglishLetter(c) && !(c >= '0' && c <= '9') && c != '_') {
                return "Username can only have letters, digits and underscore.";
            }
        }
        if (username.toLowerCase().contains("admin")) {
            return "That username is not allowed.";
        }
        return null;
    }

    public static String checkPassword(String password, String username) {
        if (password.equals("")) {
            return "Enter a password.";
        }
        if (password.length() < 6) {
            return "Password must be at least 6 characters.";
        }
        if (password.length() > 30) {
            return "Password can't be longer than 30 characters.";
        }
        boolean hasLetter = false;
        boolean hasDigit = false;
        for (int i = 0; i < password.length(); i++) {
            char c = password.charAt(i);
            if (c == ' ') {
                return "Password can't contain spaces.";
            }
            if (Character.isLetter(c)) {
                hasLetter = true;
            }
            if (Character.isDigit(c)) {
                hasDigit = true;
            }
        }
        if (!hasLetter || !hasDigit) {
            return "Password needs at least one letter and one number.";
        }
        if (password.equalsIgnoreCase(username)) {
            return "Password can't be the same as your username.";
        }
        return null;
    }

    private static boolean isEnglishLetter(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
    }
}

