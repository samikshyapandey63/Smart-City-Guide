import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class DashboardFrame extends JFrame {

    private User user;
    private PlaceDAO placeDAO = new PlaceDAO();
    // parts that search() and the button actions need to reach
    private JTextField searchField;
    private JComboBox<String> categoryBox;
    private DefaultTableModel model;
    private JTable placesTable;

    // the places currently shown in the table (same order as the table rows)
    private ArrayList<Place> shown = new ArrayList<>();

    public DashboardFrame(User user) {

        this.user = user;

        setTitle("Smart City Guide");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());


        // ================= HEADER =================

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(190, 215, 235));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);

        JLabel title = new JLabel("SMART CITY GUIDE");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Times New Roman", Font.BOLD, 22));

        JLabel welcome = new JLabel("Welcome, " + user.getName());
        welcome.setForeground(Color.WHITE);
        welcome.setFont(new Font("Segoe Script", Font.ITALIC, 15));

        titlePanel.add(title);
        titlePanel.add(welcome);

        JLabel timeLabel = new JLabel();
        timeLabel.setForeground(Color.WHITE);
        timeLabel.setFont(new Font("Times New Roman", Font.PLAIN, 15));

        JButton logoutButton = new JButton("Logout");
        logoutButton.setPreferredSize(new Dimension(85, 30));

        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 8));
        userPanel.setOpaque(false);

        userPanel.add(timeLabel);
        userPanel.add(logoutButton);

        headerPanel.add(titlePanel, BorderLayout.WEST);
        headerPanel.add(userPanel, BorderLayout.EAST);


        // ================= NAVIGATION =================

        JPanel navigationPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        navigationPanel.setBackground(new Color(235, 240, 245));

        JButton placesButton = new JButton("Places");
        JButton favoritesButton = new JButton("My Favorites");
        JButton emergencyButton = new JButton("Emergency");
        JButton suggestButton = new JButton("Suggest a Place");

        navigationPanel.add(placesButton);
        navigationPanel.add(favoritesButton);
        navigationPanel.add(emergencyButton);
        navigationPanel.add(suggestButton);

        // header and navigation share ONE panel, because a BorderLayout

        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.add(headerPanel, BorderLayout.NORTH);
        topContainer.add(navigationPanel, BorderLayout.CENTER);


        // ================= MAIN CONTENT =================

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));


        // ================= EXPLORE TITLE =================

        JLabel exploreTitle = new JLabel("Explore Places");
        exploreTitle.setFont(new Font("Arial", Font.BOLD, 25));


        // ================= SEARCH PANEL =================

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        searchPanel.setBackground(Color.WHITE);

        JLabel searchLabel = new JLabel("Search:");
        searchField = new JTextField(18);

        JLabel categoryLabel = new JLabel("Category:");


        categoryBox = new JComboBox<>();
        categoryBox.addItem("All");
        try {
            for (String name : placeDAO.getCategoryNames()) {
                categoryBox.addItem(name);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Could not load categories. Is MySQL running?\n" + ex.getMessage());
        }

        JButton searchButton = new JButton("Search");

        // search icon (if the file is missing the button simply shows the text)
        ImageIcon icon = new ImageIcon("icons/search_dark.png");
        if (icon.getIconWidth() > 0) {
            searchButton.setIcon(new ImageIcon(icon.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH)));
            searchButton.setIconTextGap(8);
        }

        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(categoryLabel);
        searchPanel.add(categoryBox);
        searchPanel.add(searchButton);


        // ================= TOP CONTENT =================

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        topPanel.add(exploreTitle, BorderLayout.NORTH);
        topPanel.add(searchPanel, BorderLayout.CENTER);


        // ================= PLACES TABLE =================

        String[] columns = {"Name", "Category", "Area", "Phone", "Hours", "Rating"};

        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        placesTable = new JTable(model);
        placesTable.setRowHeight(30);
        placesTable.setFont(new Font("Arial", Font.PLAIN, 14));
        placesTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        placesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane tableScrollPane = new JScrollPane(placesTable);

        loadTable("", "All");                       // fill the table from MySQL


        // ================= PLACE ACTIONS =================

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        actionPanel.setBackground(Color.WHITE);

        JButton detailsButton = new JButton("View Details");
        JButton favoriteButton = new JButton("Add to Favorites");

        actionPanel.add(detailsButton);
        actionPanel.add(favoriteButton);


        // ================= TABLE AREA =================

        JPanel tablePanel = new JPanel(new BorderLayout(10, 10));
        tablePanel.setBackground(Color.WHITE);
        tablePanel.add(tableScrollPane, BorderLayout.CENTER);
        tablePanel.add(actionPanel, BorderLayout.SOUTH);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(tablePanel, BorderLayout.CENTER);


        // ================= BUTTON ACTIONS =================

        placesButton.addActionListener(e ->
                JOptionPane.showMessageDialog(this, "You are already viewing Places."));

        favoritesButton.addActionListener(e ->
                JOptionPane.showMessageDialog(this, "My Favorites coming soon."));

        emergencyButton.addActionListener(e ->
                JOptionPane.showMessageDialog(this, "Emergency services coming soon."));

        suggestButton.addActionListener(e ->
                JOptionPane.showMessageDialog(this, "Suggest a Place coming soon."));

        favoriteButton.addActionListener(e ->
                JOptionPane.showMessageDialog(this, "Favorites functionality coming soon."));

        // search: button, Enter key in the box, or changing the category
        searchButton.addActionListener(e -> search());
        searchField.addActionListener(e -> search());
        categoryBox.addActionListener(e -> search());

        detailsButton.addActionListener(e -> {
            int selectedRow = placesTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a place first.");
                return;
            }
            showPlaceDetails(shown.get(selectedRow));       // the place that was selected
        });


        // ================= CLOCK =================

        DateTimeFormatter clockFormat = DateTimeFormatter.ofPattern("HH:mm:ss");

        timeLabel.setText(LocalTime.now().format(clockFormat));

        Timer timer = new Timer(1000, e ->
                timeLabel.setText(LocalTime.now().format(clockFormat)));
        timer.start();


        // ================= LOGOUT =================

        logoutButton.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);

            if (choice == JOptionPane.YES_OPTION) {
                timer.stop();
                dispose();
                MainScreen.main(null);
            }
        });


        // ================= ADD EVERYTHING =================

        add(topContainer, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);

        setVisible(true);
    }


    // ================= SEARCH + TABLE =================

    private void search() {
        boolean loaded = loadTable(searchField.getText().trim(), (String) categoryBox.getSelectedItem());
        if (loaded && shown.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No places found.");
        }
    }

    // asks MySQL for the matching places and fills the table; returns false if the database failed
    private boolean loadTable(String text, String category) {
        model.setRowCount(0);
        shown.clear();

        try {
            shown = placeDAO.searchPlaces(text, category);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Could not load places. Is MySQL running?\n" + ex.getMessage());
            return false;
        }

        for (Place p : shown) {
            model.addRow(new Object[]{
                    p.getName(), p.getCategory(), p.getArea(),
                    p.getPhone(), p.getHours(), p.getRatingText()});
        }
        return true;
    }

    // true if the current time is between opening and closing (also works for night hours like 22:00 - 02:00)
    private boolean isOpenNow(Place p) {
        LocalTime open = LocalTime.parse(p.getOpenTime());
        LocalTime close = LocalTime.parse(p.getCloseTime());
        LocalTime now = LocalTime.now();

        if (open.isBefore(close)) {
            return !now.isBefore(open) && !now.isAfter(close);
        }
        return !now.isBefore(open) || !now.isAfter(close);
    }


    // ================= PLACE DETAILS =================

    private void showPlaceDetails(Place place) {

        JDialog dialog = new JDialog(this, place.getName(), true);
        dialog.setSize(500, 480);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        JLabel nameLabel = new JLabel(place.getName());
        nameLabel.setFont(new Font("Arial", Font.BOLD, 24));

        JLabel openLabel = new JLabel();
        openLabel.setFont(new Font("Arial", Font.PLAIN, 15));
        if (isOpenNow(place)) {
            openLabel.setText("\u25CF Open now");
            openLabel.setForeground(new Color(30, 130, 60));
        } else {
            openLabel.setText("\u25CF Closed");
            openLabel.setForeground(new Color(180, 0, 0));
        }

        String description = place.getDescription();
        if (description == null) {
            description = "";
        }

        JLabel categoryLabel = new JLabel("Category: " + place.getCategory());
        JLabel locationLabel = new JLabel("Address: " + place.getAddress() + ", " + place.getArea());
        JLabel phoneLabel = new JLabel("Phone: " + place.getPhone());
        JLabel hoursLabel = new JLabel("Hours: " + place.getHours());
        JLabel ratingInfoLabel = new JLabel("Average rating: " + place.getRatingText());
        JLabel descriptionLabel = new JLabel(description);

        panel.add(nameLabel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(openLabel);
        panel.add(Box.createVerticalStrut(15));
        panel.add(categoryLabel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(locationLabel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(phoneLabel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(hoursLabel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(ratingInfoLabel);
        panel.add(Box.createVerticalStrut(15));
        panel.add(descriptionLabel);
        panel.add(Box.createVerticalStrut(25));

        JLabel ratingLabel = new JLabel("Your rating:");
        panel.add(ratingLabel);

        JPanel ratingPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        ButtonGroup ratingGroup = new ButtonGroup();
        JRadioButton[] stars = new JRadioButton[5];

        for (int i = 0; i < 5; i++) {
            stars[i] = new JRadioButton("" + (i + 1));
            ratingGroup.add(stars[i]);
            ratingPanel.add(stars[i]);
        }
        panel.add(ratingPanel);

        // BoxLayout lines everything up on the left only if every component has the same alignment
        for (Component c : panel.getComponents()) {
            if (c instanceof JComponent) {
                ((JComponent) c).setAlignmentX(Component.LEFT_ALIGNMENT);
            }
        }

        JButton submitButton = new JButton("Submit Review");
        JButton closeButton = new JButton("Close");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(submitButton);
        buttonPanel.add(closeButton);

        submitButton.addActionListener(e -> {
            int rating = 0;
            for (int i = 0; i < 5; i++) {
                if (stars[i].isSelected()) {
                    rating = i + 1;
                }
            }
            if (rating == 0) {
                JOptionPane.showMessageDialog(dialog, "Please choose a rating from 1 to 5.");
                return;
            }
            JOptionPane.showMessageDialog(dialog,
                    "You rated " + place.getName() + " " + rating + "/5.\nSaving reviews to the database is coming soon.");
        });

        closeButton.addActionListener(e -> dialog.dispose());

        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }
}