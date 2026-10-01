import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ManagePlaces extends JFrame {

    private JTextField nameField;
    private JTextField locationField;
    private JComboBox<String> categoryBox;
    private JTextArea descriptionArea;

    private JTable placesTable;
    private DefaultTableModel tableModel;


    public ManagePlaces() {

        setTitle("Smart City Guide - Manage Places");
        setSize(900, 650);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);


        // ================= MAIN PANEL =================

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));

        mainPanel.setBorder(
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        );

        mainPanel.setBackground(Color.WHITE);


        // ================= TITLE =================

        JLabel title = new JLabel("MANAGE TOURIST PLACES");

        title.setFont(
                new Font("Arial", Font.BOLD, 24)
        );

        mainPanel.add(title, BorderLayout.NORTH);


        // ================= FORM =================

        JPanel formPanel = new JPanel(new GridBagLayout());

        formPanel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;


        // Place Name

        gbc.gridx = 0;
        gbc.gridy = 0;

        formPanel.add(
                new JLabel("Place Name:"),
                gbc
        );

        nameField = new JTextField(25);

        gbc.gridx = 1;

        formPanel.add(
                nameField,
                gbc
        );


        // Location

        gbc.gridx = 0;
        gbc.gridy = 1;

        formPanel.add(
                new JLabel("Location:"),
                gbc
        );

        locationField = new JTextField(25);

        gbc.gridx = 1;

        formPanel.add(
                locationField,
                gbc
        );


        // Category

        gbc.gridx = 0;
        gbc.gridy = 2;

        formPanel.add(
                new JLabel("Category:"),
                gbc
        );

        String[] categories = {
                "Historical",
                "Religious",
                "Cultural",
                "Nature",
                "Entertainment",
                "Shopping"
        };

        categoryBox = new JComboBox<>(categories);

        gbc.gridx = 1;

        formPanel.add(
                categoryBox,
                gbc
        );


        // Description

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.NORTH;

        formPanel.add(
                new JLabel("Description:"),
                gbc
        );

        descriptionArea = new JTextArea(4, 25);

        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);

        JScrollPane descriptionScroll =
                new JScrollPane(descriptionArea);

        gbc.gridx = 1;

        formPanel.add(
                descriptionScroll,
                gbc
        );


        // ================= BUTTONS =================

        JButton addButton =
                new JButton("ADD PLACE");

        JButton updateButton =
                new JButton("UPDATE PLACE");

        JButton deleteButton =
                new JButton("DELETE PLACE");

        JButton clearButton =
                new JButton("CLEAR");


        JPanel buttonPanel = new JPanel();

        buttonPanel.setBackground(Color.WHITE);

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);


        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.CENTER;

        formPanel.add(
                buttonPanel,
                gbc
        );


        // ================= TABLE =================

        String[] columns = {
                "ID",
                "Place Name",
                "Location",
                "Category",
                "Description"
        };

        tableModel =
                new DefaultTableModel(columns, 0);

        placesTable =
                new JTable(tableModel);


        JScrollPane tableScrollPane =
                new JScrollPane(placesTable);

        tableScrollPane.setBorder(
                BorderFactory.createTitledBorder(
                        "Existing Places"
                )
        );


        // ================= CENTER =================

        JPanel centerPanel =
                new JPanel(new BorderLayout(10, 10));

        centerPanel.setBackground(Color.WHITE);

        centerPanel.add(
                formPanel,
                BorderLayout.NORTH
        );

        centerPanel.add(
                tableScrollPane,
                BorderLayout.CENTER
        );

        mainPanel.add(
                centerPanel,
                BorderLayout.CENTER
        );


        // ================= BUTTON ACTIONS =================

        addButton.addActionListener(
                e -> addPlace()
        );

        updateButton.addActionListener(
                e -> updatePlace()
        );

        deleteButton.addActionListener(
                e -> deletePlace()
        );

        clearButton.addActionListener(
                e -> clearFields()
        );


        // ================= TABLE CLICK =================

        placesTable.addMouseListener(
                new java.awt.event.MouseAdapter() {

                    @Override
                    public void mouseClicked(
                            java.awt.event.MouseEvent e) {

                        int row =
                                placesTable.getSelectedRow();

                        if (row != -1) {

                            String name =
                                    placesTable
                                            .getValueAt(row, 1)
                                            .toString();

                            String location =
                                    placesTable
                                            .getValueAt(row, 2)
                                            .toString();

                            String category =
                                    placesTable
                                            .getValueAt(row, 3)
                                            .toString();

                            int placeId =
                                    Integer.parseInt(
                                            placesTable
                                                    .getValueAt(row, 0)
                                                    .toString()
                                    );


                            // Get complete description
                            String description =
                                    getFullDescription(placeId);


                            // Put information into the form
                            nameField.setText(name);

                            locationField.setText(location);

                            categoryBox.setSelectedItem(category);

                            descriptionArea.setText(description);


                            // Show complete details in popup
                            JOptionPane.showMessageDialog(
                                    ManagePlaces.this,

                                    "Place Name: " + name +
                                            "\n\nLocation: " + location +
                                            "\n\nCategory: " + category +
                                            "\n\nDescription:\n" + description,

                                    "Place Details",

                                    JOptionPane.INFORMATION_MESSAGE
                            );
                        }
                    }
                }
        );


        // ================= LOAD DATA =================

        loadPlaces();


        // ================= WINDOW =================

        add(mainPanel);

        setVisible(true);
    }


    // ==================================================
    // ADD PLACE
    // ==================================================

    private void addPlace() {

        String name =
                nameField.getText();

        String location =
                locationField.getText();

        String category =
                categoryBox
                        .getSelectedItem()
                        .toString();

        String description =
                descriptionArea.getText();


        if (name.isEmpty()
                || location.isEmpty()
                || description.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please fill in all fields."
            );

            return;
        }


        String sql =
                "INSERT INTO places " +
                        "(name, location, category, description) " +
                        "VALUES (?, ?, ?, ?)";


        try {

            java.sql.Connection connection =
                    DatabaseConnection.getConnection();

            java.sql.PreparedStatement statement =
                    connection.prepareStatement(sql);


            statement.setString(1, name);
            statement.setString(2, location);
            statement.setString(3, category);
            statement.setString(4, description);


            statement.executeUpdate();


            JOptionPane.showMessageDialog(
                    this,
                    "Place added successfully!"
            );


            loadPlaces();

            clearFields();


        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error adding place: "
                            + e.getMessage()
            );

            e.printStackTrace();
        }
    }


    // ==================================================
    // UPDATE PLACE
    // ==================================================

    private void updatePlace() {

        int selectedRow =
                placesTable.getSelectedRow();


        if (selectedRow == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select a place from the table first."
            );

            return;
        }


        int placeId =
                Integer.parseInt(
                        placesTable
                                .getValueAt(
                                        selectedRow,
                                        0
                                )
                                .toString()
                );


        String name =
                nameField.getText();

        String location =
                locationField.getText();

        String category =
                categoryBox
                        .getSelectedItem()
                        .toString();

        String description =
                descriptionArea.getText();


        if (name.isEmpty()
                || location.isEmpty()
                || description.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please fill in all fields."
            );

            return;
        }


        String sql =
                "UPDATE places SET " +
                        "name = ?, " +
                        "location = ?, " +
                        "category = ?, " +
                        "description = ? " +
                        "WHERE place_id = ?";


        try {

            java.sql.Connection connection =
                    DatabaseConnection.getConnection();

            java.sql.PreparedStatement statement =
                    connection.prepareStatement(sql);


            statement.setString(1, name);
            statement.setString(2, location);
            statement.setString(3, category);
            statement.setString(4, description);
            statement.setInt(5, placeId);


            statement.executeUpdate();


            JOptionPane.showMessageDialog(
                    this,
                    "Place updated successfully!"
            );


            loadPlaces();

            clearFields();


        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error updating place: "
                            + e.getMessage()
            );

            e.printStackTrace();
        }
    }
    private void deletePlace() {

        int selectedRow =
                placesTable.getSelectedRow();

        if (selectedRow == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select a place from the table first."
            );

            return;
        }


        int placeId =
                Integer.parseInt(
                        placesTable
                                .getValueAt(
                                        selectedRow,
                                        0
                                )
                                .toString()
                );


        String placeName =
                placesTable
                        .getValueAt(
                                selectedRow,
                                1
                        )
                        .toString();


        int confirmation =
                JOptionPane.showConfirmDialog(
                        this,
                        "Are you sure you want to delete "
                                + placeName + "?",
                        "Confirm Delete",
                        JOptionPane.YES_NO_OPTION
                );


        if (confirmation != JOptionPane.YES_OPTION) {

            return;
        }


        String sql =
                "DELETE FROM places " +
                        "WHERE place_id = ?";


        try {

            java.sql.Connection connection =
                    DatabaseConnection.getConnection();

            java.sql.PreparedStatement statement =
                    connection.prepareStatement(sql);


            statement.setInt(
                    1,
                    placeId
            );


            statement.executeUpdate();


            JOptionPane.showMessageDialog(
                    this,
                    "Place deleted successfully!"
            );


            loadPlaces();

            clearFields();


        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error deleting place: "
                            + e.getMessage()
            );

            e.printStackTrace();
        }
    }


    // ==================================================
    // LOAD PLACES
    // ==================================================

    private void loadPlaces() {

        String sql =
                "SELECT * FROM places";


        try {

            java.sql.Connection connection =
                    DatabaseConnection.getConnection();

            java.sql.PreparedStatement statement =
                    connection.prepareStatement(sql);

            java.sql.ResultSet result =
                    statement.executeQuery();


            tableModel.setRowCount(0);


            while (result.next()) {

                String description =
                        result.getString(
                                "description"
                        );


                if (description.length() > 40) {

                    description =
                            description.substring(
                                    0,
                                    40
                            ) + "...";
                }


                tableModel.addRow(
                        new Object[]{
                                result.getInt(
                                        "place_id"
                                ),

                                result.getString(
                                        "name"
                                ),

                                result.getString(
                                        "location"
                                ),

                                result.getString(
                                        "category"
                                ),

                                description
                        }
                );
            }


        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error loading places: "
                            + e.getMessage()
            );

            e.printStackTrace();
        }
    }


    // ==================================================
    // GET FULL DESCRIPTION
    // ==================================================

    private String getFullDescription(
            int placeId) {

        String sql =
                "SELECT description " +
                        "FROM places " +
                        "WHERE place_id = ?";


        try {

            java.sql.Connection connection =
                    DatabaseConnection.getConnection();

            java.sql.PreparedStatement statement =
                    connection.prepareStatement(sql);


            statement.setInt(
                    1,
                    placeId
            );


            java.sql.ResultSet result =
                    statement.executeQuery();


            if (result.next()) {

                return result.getString(
                        "description"
                );
            }


        } catch (Exception e) {

            e.printStackTrace();
        }


        return "Description not available.";
    }


    // ==================================================
    // CLEAR FIELDS
    // ==================================================

    private void clearFields() {

        nameField.setText("");

        locationField.setText("");

        descriptionArea.setText("");

        categoryBox.setSelectedIndex(0);

        placesTable.clearSelection();
    }


    // ==================================================
    // MAIN
    // ==================================================

    public static void main(String[] args) {

        new ManagePlaces();
    }
}