
import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class SuggestPanel extends JPanel {

    private User user;
    private PlaceDAO placeDAO = new PlaceDAO();
    private SuggestionDAO suggestionDAO = new SuggestionDAO();

    private JTextField nameField, areaField, addressField, phoneField, openField, closeField;
    private JComboBox<String> categoryBox, cityBox;
    private JTextArea descriptionArea;
    private JLabel errorLabel;

    public SuggestPanel(User user, String startCity) {

        this.user = user;

        setLayout(null);
        setBackground(Color.WHITE);


        // ================= TITLE =================

        JLabel title = new JLabel("Suggest a Place");
        title.setFont(new Font("Arial", Font.BOLD, 25));
        title.setBounds(25, 15, 400, 34);
        add(title);

        JLabel subtitle = new JLabel("Know a place that is missing? Send it to the admin for approval.");
        subtitle.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitle.setForeground(new Color(110, 110, 110));
        subtitle.setBounds(25, 52, 600, 22);
        add(subtitle);


        // ================= ROW 1: NAME + CATEGORY =================

        add(makeLabel("Name of the place *", 25, 88));

        nameField = new JTextField();
        nameField.setBounds(25, 110, 300, 30);
        add(nameField);

        add(makeLabel("Category *", 355, 88));

        categoryBox = new JComboBox<>();
        categoryBox.setBounds(355, 110, 300, 30);
        add(categoryBox);

        try {
            for (String name : placeDAO.getCategoryNames()) {
                categoryBox.addItem(name);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Could not load categories. Is MySQL running?\n" + ex.getMessage());
        }


        // ================= ROW 2: CITY + AREA =================

        add(makeLabel("City *", 25, 150));

        cityBox = new JComboBox<>(Cities.LIST);
        cityBox.setSelectedItem(startCity);
        cityBox.setBounds(25, 172, 300, 30);
        add(cityBox);

        add(makeLabel("Area (for example Lakeside)", 355, 150));

        areaField = new JTextField();
        areaField.setBounds(355, 172, 300, 30);
        add(areaField);


        // ================= ROW 3: ADDRESS + PHONE =================

        add(makeLabel("Address *", 25, 212));

        addressField = new JTextField();
        addressField.setBounds(25, 234, 300, 30);
        add(addressField);

        // CHANGED: 7 to 10 digits -> exactly 10 digits
        add(makeLabel("Phone (10 digits) *", 355, 212));

        phoneField = new JTextField();
        phoneField.setBounds(355, 234, 300, 30);
        add(phoneField);


        // ================= ROW 4: OPENING HOURS =================

        add(makeLabel("Opens at (HH:mm) *", 25, 274));

        openField = new JTextField("09:00");
        openField.setBounds(25, 296, 140, 30);
        add(openField);

        add(makeLabel("Closes at (HH:mm) *", 185, 274));

        closeField = new JTextField("17:00");
        closeField.setBounds(185, 296, 140, 30);
        add(closeField);


        // ================= DESCRIPTION =================

        add(makeLabel("Description (optional)", 25, 336));

        descriptionArea = new JTextArea();
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);

        JScrollPane descriptionScroll = new JScrollPane(descriptionArea);
        descriptionScroll.setBounds(25, 358, 630, 60);
        add(descriptionScroll);


        // ================= ERROR + BUTTON =================

        errorLabel = new JLabel("");
        errorLabel.setForeground(new Color(180, 0, 0));
        errorLabel.setFont(new Font("Arial", Font.BOLD, 13));
        errorLabel.setBounds(25, 424, 630, 22);
        add(errorLabel);

        JButton submitButton = new JButton("Submit Suggestion");
        submitButton.setBounds(25, 452, 220, 38);
        submitButton.setFont(new Font("Times New Roman", Font.PLAIN, 18));
        add(submitButton);

        submitButton.addActionListener(e -> submit());
    }


    // =========================================================
    // SET CITY
    // =========================================================

    public void setCity(String city) {
        cityBox.setSelectedItem(city);
    }


    // =========================================================
    // CREATE LABEL
    // =========================================================

    private JLabel makeLabel(String text, int x, int y) {

        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.PLAIN, 13));
        label.setBounds(x, y, 300, 20);

        return label;
    }


    // =========================================================
    // SHOW ERROR
    // =========================================================

    private void showError(String message, JComponent field) {

        errorLabel.setText(message);

        if (field != null) {
            field.requestFocus();
        }
    }


    // =========================================================
    // SUBMIT
    // =========================================================

    private void submit() {

        errorLabel.setText("");

        String name = nameField.getText().trim();
        String category = (String) categoryBox.getSelectedItem();
        String city = (String) cityBox.getSelectedItem();
        String area = areaField.getText().trim();
        String address = addressField.getText().trim();
        String phone = phoneField.getText().trim();
        String open = openField.getText().trim();
        String close = closeField.getText().trim();
        String description = descriptionArea.getText().trim();


        // =====================================================
        // VALIDATION
        // =====================================================

        String error = Validator.checkPlaceName(name);

        if (error != null) {
            showError(error, nameField);
            return;
        }


        if (category == null) {
            showError("Choose a category.", categoryBox);
            return;
        }


        error = Validator.checkArea(area);

        if (error != null) {
            showError(error, areaField);
            return;
        }


        error = Validator.checkAddress(address);

        if (error != null) {
            showError(error, addressField);
            return;
        }


        // =====================================================
        // PHONE VALIDATION
        // EXACTLY 10 DIGITS
        // =====================================================

        error = Validator.checkPhone(phone);

        if (error != null) {
            showError(error, phoneField);
            return;
        }


        error = Validator.checkTime(open, "opening");

        if (error != null) {
            showError(error, openField);
            return;
        }


        error = Validator.checkTime(close, "closing");

        if (error != null) {
            showError(error, closeField);
            return;
        }


        error = Validator.checkHours(open, close);

        if (error != null) {
            showError(error, closeField);
            return;
        }


        error = Validator.checkDescription(description);

        if (error != null) {
            showError(error, descriptionArea);
            return;
        }


        // =====================================================
        // DATABASE
        // =====================================================

        try {

            if (placeDAO.placeExists(name, address, city)) {

                showError(
                        "That place is already on the list for " + city + ".",
                        nameField
                );

                return;
            }


            if (suggestionDAO.pendingExists(name, address, city)) {

                showError(
                        "That place was already suggested and is waiting for approval.",
                        nameField
                );

                return;
            }


            int categoryId = placeDAO.getCategoryId(category);


            suggestionDAO.submit(
                    user.getId(),
                    name,
                    categoryId,
                    city,
                    area,
                    address,
                    phone,
                    open,
                    close,
                    description
            );


            JOptionPane.showMessageDialog(
                    this,
                    "Thank you! An admin will check \"" +
                            name +
                            "\" before it appears on the list."
            );


            clearForm();

        } catch (SQLException ex) {

            showError(
                    "Could not save your suggestion. Is MySQL running?",
                    nameField
            );
        }
    }


    // =========================================================
    // CLEAR FORM
    // =========================================================

    private void clearForm() {

        nameField.setText("");
        areaField.setText("");
        addressField.setText("");
        phoneField.setText("");

        openField.setText("09:00");
        closeField.setText("17:00");

        descriptionArea.setText("");
        errorLabel.setText("");
    }
}

