public class Validator {

    // =========================================================
    // NAME VALIDATION
    // =========================================================

    public static String checkName(String name) {

        if (name == null || name.trim().equals("")) {
            return "Enter your full name.";
        }

        if (name.length() < 2) {
            return "Name must be at least 2 characters.";
        }

        if (name.length() > 50) {
            return "Name can't be longer than 50 characters.";
        }

        boolean previousWasSpace = true;

        for (int i = 0; i < name.length(); i++) {

            char c = name.charAt(i);

            if (c == ' ') {

                if (previousWasSpace) {
                    return "Enter one space between names only.";
                }

                previousWasSpace = true;

            } else {

                if (!isEnglishLetter(c)) {
                    return "Name can only contain English letters and spaces.";
                }

                if (previousWasSpace && !Character.isUpperCase(c)) {
                    return "Each name must start with a capital letter.";
                }

                if (!previousWasSpace && !Character.isLowerCase(c)) {
                    return "Use lowercase letters after the first letter.";
                }

                previousWasSpace = false;
            }
        }

        if (previousWasSpace) {
            return "Name can't end with a space.";
        }

        return null;
    }


    // =========================================================
    // USERNAME VALIDATION
    // =========================================================

    public static String checkUsername(String username) {

        if (username == null || username.equals("")) {
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

            if (!isEnglishLetter(c) && !isDigit(c) && c != '_') {
                return "Username can only have letters, digits and underscore.";
            }
        }

        if (username.toLowerCase().contains("admin")) {
            return "That username is not allowed.";
        }

        return null;
    }


    // =========================================================
    // PASSWORD VALIDATION
    // =========================================================

    public static String checkPassword(String password, String username) {

        if (password == null || password.equals("")) {
            return "Enter a password.";
        }

        if (password.length() < 8) {
            return "Password must be at least 8 characters.";
        }

        if (password.length() > 30) {
            return "Password can't be longer than 30 characters.";
        }

        boolean hasLetter = false;
        boolean hasDigit = false;

        for (int i = 0; i < password.length(); i++) {

            char c = password.charAt(i);

            if (Character.isWhitespace(c)) {
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

        if (username != null && password.equalsIgnoreCase(username)) {
            return "Password can't be the same as your username.";
        }

        return null;
    }


    // =========================================================
    // EMAIL VALIDATION
    // =========================================================

    public static String checkEmail(String email) {

        if (email == null || email.trim().equals("")) {
            return "Enter your email address.";
        }

        email = email.trim();

        if (email.length() > 100) {
            return "Email can't be longer than 100 characters.";
        }

        if (email.indexOf(' ') != -1) {
            return "Email can't contain spaces.";
        }

        // Check for @
        int at = email.indexOf('@');

        if (at == -1) {
            return "Email must contain @.";
        }

        if (at != email.lastIndexOf('@')) {
            return "Email can only contain one @.";
        }

        // Separate username and domain
        String local = email.substring(0, at);
        String domain = email.substring(at + 1);


        // -----------------------------------------------------
        // PART BEFORE @
        // -----------------------------------------------------

        if (local.equals("")) {
            return "Enter the part before @.";
        }

        if (local.length() > 64) {
            return "The part before @ is too long.";
        }

        if (local.startsWith(".") || local.endsWith(".")) {
            return "Email can't have a dot at the start or end of the username.";
        }

        if (local.contains("..")) {
            return "Email can't have two dots in a row.";
        }

        /*
         * Gmail usernames can contain:
         * English letters
         * numbers
         * dots
         *
         * We keep Gmail-only validation because that is
         * what your original project required.
         */
        for (int i = 0; i < local.length(); i++) {

            char c = local.charAt(i);

            if (!isEnglishLetter(c) && !isDigit(c) && c != '.') {
                return "Gmail username can only contain letters, digits and dots.";
            }
        }


        // -----------------------------------------------------
        // DOMAIN
        // -----------------------------------------------------

        if (domain.equals("")) {
            return "Enter the domain after @.";
        }

        // Your project accepts Gmail only
        if (!domain.equalsIgnoreCase("gmail.com")) {
            return "Only Gmail addresses ending in @gmail.com are allowed.";
        }

        // Additional safety check for gmail.com
        if (domain.startsWith(".") || domain.endsWith(".")) {
            return "Email domain can't start or end with a dot.";
        }

        if (domain.contains("..")) {
            return "Email domain can't contain two dots in a row.";
        }

        return null;
    }

// =========================================================
// PHONE VALIDATION
// Exactly 10 digits
// =========================================================

    public static String checkPhone(String phone) {

        if (phone == null || phone.trim().equals("")) {
            return "Enter a phone number.";
        }

        phone = phone.trim();

        // Every character must be a digit
        for (int i = 0; i < phone.length(); i++) {

            if (!isDigit(phone.charAt(i))) {
                return "Phone number can contain digits only.";
            }
        }

        // Phone number MUST be exactly 10 digits
        if (phone.length() != 10) {
            return "Phone number must contain exactly 10 digits.";
        }

        // Nepal mobile numbers normally start with 97 or 98
        boolean mobileNumber =
                phone.startsWith("97") ||
                        phone.startsWith("98");

        // Nepal Kathmandu landline numbers start with 01
        boolean landlineNumber =
                phone.startsWith("01");

        if (!mobileNumber && !landlineNumber) {
            return "Enter a valid Nepal phone number.";
        }

        return null;
    }

    // =========================================================
    // PLACE NAME VALIDATION
    // Used by "Suggest a Place"
    // =========================================================

    public static String checkPlaceName(String name) {

        if (name == null || name.trim().equals("")) {
            return "Enter the name of the place.";
        }

        name = name.trim();

        if (name.length() < 2) {
            return "Place name must be at least 2 characters.";
        }

        if (name.length() > 100) {
            return "Place name can't be longer than 100 characters.";
        }

        boolean hasLetter = false;

        for (int i = 0; i < name.length(); i++) {

            char c = name.charAt(i);

            if (Character.isLetter(c)) {

                hasLetter = true;

            } else if (!Character.isDigit(c)
                    && " &'.,()-".indexOf(c) == -1) {

                return "Place name can't contain the character " + c;
            }
        }

        if (!hasLetter) {
            return "Place name must contain letters.";
        }

        return null;
    }


    // =========================================================
    // ADDRESS VALIDATION
    // Used by "Suggest a Place"
    // =========================================================

    public static String checkAddress(String address) {

        if (address == null || address.trim().equals("")) {
            return "Enter the address of the place.";
        }

        address = address.trim();

        if (address.length() < 3) {
            return "Address is too short.";
        }

        if (address.length() > 200) {
            return "Address can't be longer than 200 characters.";
        }

        return null;
    }


    // =========================================================
    // AREA VALIDATION
    // Area is optional
    // =========================================================

    public static String checkArea(String area) {

        if (area == null) {
            return null;
        }

        if (area.length() > 100) {
            return "Area can't be longer than 100 characters.";
        }

        return null;
    }


    // =========================================================
    // TIME VALIDATION
    // Format: HH:mm
    // Example: 09:00
    // =========================================================

    public static String checkTime(String time, String label) {

        String message =
                "Enter the " + label + " time as HH:mm, like 09:00.";

        if (time == null || time.equals("")) {
            return message;
        }

        if (time.length() != 5 || time.charAt(2) != ':') {
            return message;
        }

        if (!isDigit(time.charAt(0))
                || !isDigit(time.charAt(1))
                || !isDigit(time.charAt(3))
                || !isDigit(time.charAt(4))) {

            return message;
        }

        int hour = Integer.parseInt(time.substring(0, 2));
        int minute = Integer.parseInt(time.substring(3, 5));

        if (hour > 23 || minute > 59) {
            return "The " + label
                    + " time is not a real time "
                    + "(hours 00-23, minutes 00-59).";
        }

        return null;
    }


    // =========================================================
    // OPENING/CLOSING HOURS VALIDATION
    // =========================================================

    public static String checkHours(String open, String close) {

        if (open == null || close == null) {
            return null;
        }

        if (open.equals("") || close.equals("")) {
            return null;
        }

        if (open.equals(close)) {
            return "Opening and closing time can't be the same "
                    + "(for 24 hours use 00:00 to 23:59).";
        }

        return null;
    }


    // =========================================================
    // DESCRIPTION VALIDATION
    // =========================================================

    public static String checkDescription(String description) {

        if (description == null) {
            return null;
        }

        if (description.length() > 500) {
            return "Description can't be longer than 500 characters.";
        }

        return null;
    }


    // =========================================================
    // HELPER METHODS
    // =========================================================

    private static boolean isDigit(char c) {

        return c >= '0' && c <= '9';
    }


    private static boolean isEnglishLetter(char c) {

        return (c >= 'a' && c <= 'z')
                || (c >= 'A' && c <= 'Z');
    }
}

