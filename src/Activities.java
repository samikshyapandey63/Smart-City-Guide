import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class Activities extends JFrame {

    private JComboBox<String> placeBox;
    private JTextField activityField;
    private JTextField oldActivityField;


    private JTable activitiesTable;
    private DefaultTableModel tableModel;


    public Activities() {

        setTitle("Smart City Guide - Activities");
        setSize(900, 650);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);


        // Main panel
        JPanel mainPanel =
                new JPanel(new BorderLayout(10, 10));

        mainPanel.setBorder(
                BorderFactory.createEmptyBorder(
                        20, 20, 20, 20
                )
        );

        mainPanel.setBackground(Color.WHITE);


        // Title
        JLabel title =
                new JLabel("MANAGE ACTIVITIES");

        title.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        24
                )
        );

        mainPanel.add(
                title,
                BorderLayout.NORTH
        );


        // Form
        JPanel formPanel =
                new JPanel(new GridBagLayout());

        formPanel.setBackground(Color.WHITE);

        GridBagConstraints gbc =
                new GridBagConstraints();

        gbc.insets =
                new Insets(8, 8, 8, 8);

        gbc.fill =
                GridBagConstraints.HORIZONTAL;


        // Place
        gbc.gridx = 0;
        gbc.gridy = 0;

        formPanel.add(
                new JLabel("Place:"),
                gbc
        );


        placeBox =
                new JComboBox<>();

        gbc.gridx = 1;

        formPanel.add(
                placeBox,
                gbc
        );


        // Activity
        gbc.gridx = 0;
        gbc.gridy = 1;

        formPanel.add(
                new JLabel("Old Activity:"),
                gbc
        );

        oldActivityField =
                new JTextField(25);

        gbc.gridx = 1;

        formPanel.add(
                oldActivityField,
                gbc
        );

        gbc.gridx = 0;
        gbc.gridy = 2;

        formPanel.add(
                new JLabel("New Activity:"),
                gbc
        );

        activityField =
                new JTextField(25);

        gbc.gridx = 1;

        formPanel.add(
                activityField,
                gbc
        );


        // Description


        // Buttons
        JButton addButton =
                new JButton("ADD");

        JButton updateButton =
                new JButton("UPDATE");

        JButton deleteButton =
                new JButton("DELETE");

        JButton clearButton =
                new JButton("CLEAR");



        addButton.addActionListener(e -> addActivity());
        clearButton.addActionListener(e -> {
            activityField.setText("");
            placeBox.setSelectedIndex(0);
        });
        deleteButton.addActionListener(e -> deleteActivity());
        updateButton.addActionListener(e -> updateActivity());


        JPanel buttonPanel =
                new JPanel();

        buttonPanel.setBackground(Color.WHITE);

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);


        gbc.gridx = 1;
        gbc.gridy = 3;

        gbc.anchor =
                GridBagConstraints.CENTER;

        formPanel.add(
                buttonPanel,
                gbc
        );


        // Table
        String[] columns = {
                "ID",
                "Place",
                "Activity"
        };


        tableModel =
                new DefaultTableModel(
                        columns,
                        0
                );


        activitiesTable =
                new JTable(tableModel);
        activitiesTable.getColumnModel()
                .getColumn(2)
                .setPreferredWidth(300);

        activitiesTable.setRowHeight(50);
        activitiesTable.getColumnModel()
                .getColumn(2)
                .setCellRenderer(
                        new javax.swing.table.DefaultTableCellRenderer() {

                            @Override
                            public java.awt.Component getTableCellRendererComponent(
                                    javax.swing.JTable table,
                                    Object value,
                                    boolean isSelected,
                                    boolean hasFocus,
                                    int row,
                                    int column) {

                                javax.swing.JLabel label =
                                        (javax.swing.JLabel) super
                                                .getTableCellRendererComponent(
                                                        table,
                                                        value,
                                                        isSelected,
                                                        hasFocus,
                                                        row,
                                                        column
                                                );

                                if (value != null) {

                                    label.setText(
                                            "<html>"
                                                    + value.toString()
                                                    .replace(
                                                            "\n",
                                                            "<br>"
                                                    )
                                                    + "</html>"
                                    );
                                }

                                return label;
                            }
                        }
                );
        activitiesTable.getColumnModel()
                .getColumn(0)
                .setMinWidth(0);

        activitiesTable.getColumnModel()
                .getColumn(0)
                .setMaxWidth(0);

        activitiesTable.getColumnModel()
                .getColumn(0)
                .setWidth(0);


        JScrollPane tableScrollPane =
                new JScrollPane(
                        activitiesTable
                );


        tableScrollPane.setBorder(
                BorderFactory.createTitledBorder(
                        "Existing Activities"
                )
        );


        // Center
        JPanel centerPanel =
                new JPanel(
                        new BorderLayout(10, 10)
                );

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

        loadPlaces();
        activitiesTable.addMouseListener(
                new java.awt.event.MouseAdapter() {

                    @Override
                    public void mouseClicked(
                            java.awt.event.MouseEvent e) {

                        int row =
                                activitiesTable.getSelectedRow();

                        if (row != -1) {

                            String activity =
                                    activitiesTable
                                            .getValueAt(row, 2)
                                            .toString();

                            activityField.setText(activity);
                        }
                    }
                }
        );

        // Window
        loadActivities();
        add(mainPanel);

        setVisible(true);
    }

    private void loadPlaces() {

        String sql = "SELECT place_id, name FROM places";

        try {

            java.sql.Connection connection =
                    DatabaseConnection.getConnection();

            java.sql.PreparedStatement statement =
                    connection.prepareStatement(sql);

            java.sql.ResultSet result =
                    statement.executeQuery();

            placeBox.removeAllItems();

            while (result.next()) {

                placeBox.addItem(
                        result.getInt("place_id")
                                + " - "
                                + result.getString("name")
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
    private void addActivity() {

        String activityName =
                activityField.getText().trim();

        if (activityName.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please enter an activity."
            );

            return;
        }

        String selectedPlace =
                placeBox.getSelectedItem().toString();

        int placeId =
                Integer.parseInt(
                        selectedPlace
                                .split(" - ")[0]
                );
        String checkSql =
                "SELECT * FROM activities " +
                        "WHERE place_id = ? " +
                        "AND activity_name = ?";

        try {

            java.sql.Connection connection =
                    DatabaseConnection.getConnection();

            java.sql.PreparedStatement checkStatement =
                    connection.prepareStatement(checkSql);

            checkStatement.setInt(1, placeId);
            checkStatement.setString(2, activityName);

            java.sql.ResultSet result =
                    checkStatement.executeQuery();

            if (result.next()) {

                JOptionPane.showMessageDialog(
                        this,
                        "This activity already exists for this place."
                );

                return;
            }

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error checking activity: "
                            + e.getMessage()
            );

            return;
        }

        String sql =
                "INSERT INTO activities " +
                        "(place_id, activity_name) " +
                        "VALUES (?, ?)";

        try {

            java.sql.Connection connection =
                    DatabaseConnection.getConnection();

            java.sql.PreparedStatement statement =
                    connection.prepareStatement(sql);

            statement.setInt(1, placeId);
            statement.setString(2, activityName);

            statement.executeUpdate();

            JOptionPane.showMessageDialog(
                    this,
                    "Activity added successfully!"
            );

            activityField.setText("");
            loadActivities();

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error adding activity: "
                            + e.getMessage()
            );

            e.printStackTrace();
        }
    }
    private void loadActivities() {

        String sql =
                "SELECT a.activity_id, p.name, a.activity_name " +
                        "FROM activities a " +
                        "JOIN places p ON a.place_id = p.place_id " +
                        "ORDER BY p.name";

        try {

            java.sql.Connection connection =
                    DatabaseConnection.getConnection();

            java.sql.PreparedStatement statement =
                    connection.prepareStatement(sql);

            java.sql.ResultSet result =
                    statement.executeQuery();

            tableModel.setRowCount(0);

            java.util.LinkedHashMap<String, String> activityMap =
                    new java.util.LinkedHashMap<>();

            java.util.HashMap<String, Integer> idMap =
                    new java.util.HashMap<>();

            while (result.next()) {

                int activityId =
                        result.getInt("activity_id");

                String place =
                        result.getString("name");

                String activity =
                        result.getString("activity_name");

                if (!activityMap.containsKey(place)) {

                    activityMap.put(
                            place,
                            "• " + activity
                    );

                    idMap.put(
                            place,
                            activityId
                    );

                } else {

                    activityMap.put(
                            place,
                            activityMap.get(place)
                                    + "\n• "
                                    + activity
                    );
                }
            }

            for (String place : activityMap.keySet()) {

                tableModel.addRow(
                        new Object[]{
                                idMap.get(place),
                                place,
                                activityMap.get(place)
                        }
                );
            }

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error loading activities: "
                            + e.getMessage()
            );

            e.printStackTrace();
        }
    }
    private void deleteActivity() {

        String activityName =
                activityField.getText().trim();

        if (activityName.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select an activity first."
            );

            return;
        }

        String selectedPlace =
                placeBox.getSelectedItem().toString();

        int placeId =
                Integer.parseInt(
                        selectedPlace.split(" - ")[0]
                );

        int confirmation =
                JOptionPane.showConfirmDialog(
                        this,
                        "Delete activity: "
                                + activityName + "?",
                        "Confirm Delete",
                        JOptionPane.YES_NO_OPTION
                );

        if (confirmation != JOptionPane.YES_OPTION) {
            return;
        }

        String sql =
                "DELETE FROM activities " +
                        "WHERE place_id = ? " +
                        "AND activity_name = ?";

        try {

            java.sql.Connection connection =
                    DatabaseConnection.getConnection();

            java.sql.PreparedStatement statement =
                    connection.prepareStatement(sql);

            statement.setInt(1, placeId);
            statement.setString(2, activityName);

            int rows =
                    statement.executeUpdate();

            if (rows > 0) {

                JOptionPane.showMessageDialog(
                        this,
                        "Activity deleted successfully!"
                );

                activityField.setText("");
                loadActivities();

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "Activity not found."
                );
            }

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error deleting activity: "
                            + e.getMessage()
            );

            e.printStackTrace();
        }
    }
    private void updateActivity() {

        String oldActivity =
                oldActivityField.getText().trim();

        String newActivity =
                activityField.getText().trim();

        if (oldActivity.isEmpty()
                || newActivity.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please enter both old and new activity."
            );

            return;
        }

        String selectedPlace =
                placeBox.getSelectedItem().toString();

        int placeId =
                Integer.parseInt(
                        selectedPlace.split(" - ")[0]
                );

        String sql =
                "UPDATE activities " +
                        "SET activity_name = ? " +
                        "WHERE place_id = ? " +
                        "AND activity_name = ?";

        try {

            java.sql.Connection connection =
                    DatabaseConnection.getConnection();

            java.sql.PreparedStatement statement =
                    connection.prepareStatement(sql);

            statement.setString(1, newActivity);
            statement.setInt(2, placeId);
            statement.setString(3, oldActivity);

            int rows =
                    statement.executeUpdate();

            if (rows > 0) {

                JOptionPane.showMessageDialog(
                        this,
                        "Activity updated successfully!"
                );

                oldActivityField.setText("");
                activityField.setText("");

                loadActivities();

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "Activity not found."
                );
            }

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error updating activity: "
                            + e.getMessage()
            );

            e.printStackTrace();
        }
    }
    public static void main(String[] args) {

        new Activities();

    }
}
