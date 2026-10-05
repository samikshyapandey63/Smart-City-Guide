import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.geom.GeneralPath;
import java.sql.SQLException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class DashboardFrame extends JFrame {

    private User user;
    private PlaceDAO placeDAO = new PlaceDAO();
    private ReviewDAO reviewDAO = new ReviewDAO();
    private FavoriteDAO favoriteDAO = new FavoriteDAO();

    // the other pages: they replace the Places page when a menu button is pressed
    private FavoritesPanel favoritesPanel;
    private EmergencyPanel emergencyPanel;
    private SuggestPanel suggestPanel;
    private JPanel contentPanel;

    private JTextField searchField;
    private JComboBox<String> categoryBox;
    private JComboBox<String> cityBox;
    private DefaultTableModel model;
    private JTable placesTable;

    private ArrayList<Place> shown = new ArrayList<>();

    public DashboardFrame(User user) {

        this.user = user;

        String startCity = Cities.LIST[0];

        if (user.getRole().equals("TOURIST")) {
            startCity = askCity();
        }

        setTitle("Smart City Guide");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());


        // ================= HEADER =================

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(190, 215, 235));
        headerPanel.setBorder(
                BorderFactory.createEmptyBorder(12, 20, 12, 20)
        );

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(
                new BoxLayout(titlePanel, BoxLayout.Y_AXIS)
        );
        titlePanel.setOpaque(false);

        JLabel title = new JLabel("SMART CITY GUIDE");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Times New Roman", Font.BOLD, 22));

        JLabel welcome = new JLabel(
                "Welcome, " + user.getName()
        );
        welcome.setForeground(Color.WHITE);
        welcome.setFont(
                new Font("Segoe Script", Font.ITALIC, 15)
        );

        titlePanel.add(title);
        titlePanel.add(welcome);

        JLabel timeLabel = new JLabel();
        timeLabel.setForeground(Color.WHITE);
        timeLabel.setFont(
                new Font("Times New Roman", Font.PLAIN, 15)
        );

        JButton logoutButton = new JButton("Logout");
        logoutButton.setPreferredSize(
                new Dimension(85, 30)
        );

        JPanel userPanel = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 15, 8)
        );

        userPanel.setOpaque(false);
        userPanel.add(timeLabel);
        userPanel.add(logoutButton);

        headerPanel.add(
                titlePanel,
                BorderLayout.WEST
        );

        headerPanel.add(
                userPanel,
                BorderLayout.EAST
        );


        // ================= NAVIGATION =================

        JPanel navigationPanel = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 10, 8)
        );

        navigationPanel.setBackground(
                new Color(235, 240, 245)
        );

        JButton placesButton = new JButton("Places");
        JButton favoritesButton = new JButton("My Favorites");
        JButton emergencyButton = new JButton("Emergency");
        JButton suggestButton = new JButton("Suggest a Place");

        navigationPanel.add(placesButton);
        navigationPanel.add(favoritesButton);
        navigationPanel.add(emergencyButton);
        navigationPanel.add(suggestButton);


        JPanel topContainer = new JPanel(
                new BorderLayout()
        );

        topContainer.add(
                headerPanel,
                BorderLayout.NORTH
        );

        topContainer.add(
                navigationPanel,
                BorderLayout.CENTER
        );


        // ================= MAIN CONTENT =================

        JPanel mainPanel = new JPanel(
                new BorderLayout(10, 10)
        );

        mainPanel.setBackground(Color.WHITE);

        mainPanel.setBorder(
                BorderFactory.createEmptyBorder(
                        20, 25, 20, 25
                )
        );


        // ================= EXPLORE TITLE =================

        JLabel exploreTitle =
                new JLabel("Explore Places");

        exploreTitle.setFont(
                new Font("Arial", Font.BOLD, 25)
        );


        // ================= SEARCH PANEL =================

        JPanel searchPanel = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 10, 10)
        );

        searchPanel.setBackground(Color.WHITE);

        JLabel cityLabel = new JLabel("City:");

        cityBox = new JComboBox<>(
                Cities.LIST
        );

        cityBox.setSelectedItem(startCity);

        JLabel searchLabel = new JLabel("Search:");

        searchField = new JTextField(18);

        JLabel categoryLabel =
                new JLabel("Category:");

        categoryBox = new JComboBox<>();

        categoryBox.addItem("All");

        try {

            for (String name :
                    placeDAO.getCategoryNames()) {

                categoryBox.addItem(name);
            }

        } catch (SQLException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Could not load categories. Is MySQL running?\n"
                            + ex.getMessage()
            );
        }


        JButton searchButton =
                new JButton("Search");

        ImageIcon icon =
                new ImageIcon("icons/search_dark.png");

        if (icon.getIconWidth() > 0) {

            searchButton.setIcon(
                    new ImageIcon(
                            icon.getImage()
                                    .getScaledInstance(
                                            16,
                                            16,
                                            Image.SCALE_SMOOTH
                                    )
                    )
            );

            searchButton.setIconTextGap(8);
        }

        searchPanel.add(cityLabel);
        searchPanel.add(cityBox);
        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(categoryLabel);
        searchPanel.add(categoryBox);
        searchPanel.add(searchButton);


        // ================= TOP CONTENT =================

        JPanel topPanel =
                new JPanel(new BorderLayout());

        topPanel.setBackground(Color.WHITE);

        topPanel.add(
                exploreTitle,
                BorderLayout.NORTH
        );

        topPanel.add(
                searchPanel,
                BorderLayout.CENTER
        );


        // ================= PLACES TABLE =================

        String[] columns = {
                "Name",
                "Category",
                "Area",
                "Phone",
                "Hours",
                "Rating"
        };

        model = new DefaultTableModel(
                columns,
                0
        ) {

            @Override
            public boolean isCellEditable(
                    int row,
                    int column
            ) {
                return false;
            }
        };

        placesTable = new JTable(model);

        placesTable.setRowHeight(30);

        placesTable.setFont(
                new Font("Arial", Font.PLAIN, 14)
        );

        placesTable.getTableHeader().setFont(
                new Font("Arial", Font.BOLD, 14)
        );

        placesTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        JScrollPane tableScrollPane =
                new JScrollPane(placesTable);

        loadTable(
                "",
                "All",
                (String) cityBox.getSelectedItem()
        );


        // ================= PLACE ACTIONS =================

        JPanel actionPanel = new JPanel(
                new FlowLayout(
                        FlowLayout.LEFT,
                        10,
                        5
                )
        );

        actionPanel.setBackground(Color.WHITE);

        JButton detailsButton =
                new JButton("View Details");

        JButton favoriteButton =
                new JButton("Add to Favorites");

        actionPanel.add(detailsButton);
        actionPanel.add(favoriteButton);


        // ================= TABLE AREA =================

        JPanel tablePanel =
                new JPanel(
                        new BorderLayout(10, 10)
                );

        tablePanel.setBackground(Color.WHITE);

        tablePanel.add(
                tableScrollPane,
                BorderLayout.CENTER
        );

        tablePanel.add(
                actionPanel,
                BorderLayout.SOUTH
        );

        mainPanel.add(
                topPanel,
                BorderLayout.NORTH
        );

        mainPanel.add(
                tablePanel,
                BorderLayout.CENTER
        );


        // ================= OTHER PAGES =================

        favoritesPanel = new FavoritesPanel(this, user);

        emergencyPanel = new EmergencyPanel();
        emergencyPanel.setCity(startCity);

        suggestPanel = new SuggestPanel(user, startCity);

        // the area under the menu: shows ONE page at a time
        contentPanel = new JPanel(
                new BorderLayout()
        );

        contentPanel.add(
                mainPanel,
                BorderLayout.CENTER
        );


        // ================= BUTTON ACTIONS =================

        placesButton.addActionListener(
                e -> showPage(mainPanel)
        );

        favoritesButton.addActionListener(e -> {

            favoritesPanel.reload();

            showPage(favoritesPanel);
        });

        emergencyButton.addActionListener(e -> {

            emergencyPanel.setCity(
                    (String) cityBox.getSelectedItem()
            );

            showPage(emergencyPanel);
        });

        suggestButton.addActionListener(e -> {

            suggestPanel.setCity(
                    (String) cityBox.getSelectedItem()
            );

            showPage(suggestPanel);
        });

        favoriteButton.addActionListener(e -> {

            int selectedRow =
                    placesTable.getSelectedRow();

            if (selectedRow == -1) {

                JOptionPane.showMessageDialog(
                        this,
                        "Please select a place first."
                );

                return;
            }

            addToFavorites(
                    this,
                    shown.get(selectedRow)
            );
        });


        // ================= SEARCH ACTIONS =================

        searchButton.addActionListener(
                e -> search()
        );

        searchField.addActionListener(
                e -> search()
        );

        categoryBox.addActionListener(
                e -> search()
        );

        cityBox.addActionListener(e -> {

            String city =
                    (String) cityBox.getSelectedItem();

            emergencyPanel.setCity(city);

            suggestPanel.setCity(city);

            search();
        });


        // ================= DETAILS BUTTON =================

        detailsButton.addActionListener(e -> {

            int selectedRow =
                    placesTable.getSelectedRow();

            if (selectedRow == -1) {

                JOptionPane.showMessageDialog(
                        this,
                        "Please select a place first."
                );

                return;
            }

            showPlaceDetails(
                    shown.get(selectedRow)
            );
        });


        // ================= CLOCK =================

        DateTimeFormatter clockFormat =
                DateTimeFormatter.ofPattern(
                        "HH:mm:ss"
                );

        timeLabel.setText(
                LocalTime.now().format(clockFormat)
        );

        Timer timer =
                new Timer(
                        1000,
                        e -> timeLabel.setText(
                                LocalTime.now()
                                        .format(clockFormat)
                        )
                );

        timer.start();


        // ================= LOGOUT =================

        logoutButton.addActionListener(e -> {

            int choice =
                    JOptionPane.showConfirmDialog(
                            this,
                            "Are you sure you want to logout?",
                            "Logout",
                            JOptionPane.YES_NO_OPTION
                    );

            if (choice ==
                    JOptionPane.YES_OPTION) {

                timer.stop();

                dispose();

                MainScreen.main(null);
            }
        });


        // ================= ADD EVERYTHING =================

        add(
                topContainer,
                BorderLayout.NORTH
        );

        add(
                contentPanel,
                BorderLayout.CENTER
        );

        setVisible(true);
    }


    // ================= SWITCH PAGE =================

    private void showPage(JPanel page) {

        contentPanel.removeAll();

        contentPanel.add(
                page,
                BorderLayout.CENTER
        );

        contentPanel.revalidate();

        contentPanel.repaint();
    }


    // ================= ADD TO FAVORITES =================

    // parent = the window the message should appear over
    private void addToFavorites(Component parent, Place place) {

        try {

            if (favoriteDAO.isFavorite(
                    user.getId(),
                    place.getId()
            )) {

                JOptionPane.showMessageDialog(
                        parent,
                        place.getName()
                                + " is already in your favorites."
                );

                return;
            }

            favoriteDAO.addFavorite(
                    user.getId(),
                    place.getId()
            );

            JOptionPane.showMessageDialog(
                    parent,
                    place.getName()
                            + " was added to your favorites."
            );

        } catch (SQLException ex) {

            JOptionPane.showMessageDialog(
                    parent,
                    "Could not update your favorites.\n"
                            + ex.getMessage()
            );
        }
    }


    // ================= ASK THE CITY =================

    private String askCity() {

        JDialog dialog =
                new JDialog(
                        this,
                        "Choose your city",
                        true
                );

        dialog.setSize(420, 260);

        dialog.setLocationRelativeTo(this);

        dialog.setLayout(
                new BorderLayout()
        );

        dialog.setResizable(false);


        JPanel mainPanel = new JPanel();

        mainPanel.setLayout(
                new BoxLayout(
                        mainPanel,
                        BoxLayout.Y_AXIS
                )
        );

        mainPanel.setBackground(
                new Color(245, 249, 252)
        );

        mainPanel.setBorder(
                BorderFactory.createEmptyBorder(
                        25, 30, 20, 30
                )
        );


        JLabel welcomeLabel =
                new JLabel(
                        "Welcome, "
                                + user.getName()
                                + "!"
                );

        welcomeLabel.setFont(
                new Font(
                        "Times New Roman",
                        Font.BOLD,
                        22
                )
        );

        welcomeLabel.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );


        JLabel questionLabel =
                new JLabel(
                        "Where would you like to explore today?"
                );

        questionLabel.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        15
                )
        );

        questionLabel.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );


        JComboBox<String> cityBox =
                new JComboBox<>(
                        Cities.LIST
                );

        cityBox.setMaximumSize(
                new Dimension(300, 35)
        );

        cityBox.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        14
                )
        );

        cityBox.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );


        JButton exploreButton =
                new JButton("Let's explore!");

        exploreButton.setFont(
                new Font(
                        "Times New Roman",
                        Font.BOLD,
                        16
                )
        );

        exploreButton.setForeground(
                Color.WHITE
        );

        exploreButton.setBackground(
                new Color(100, 150, 190)
        );

        exploreButton.setFocusPainted(false);


        JButton cancelButton =
                new JButton("Cancel");

        cancelButton.setFont(
                new Font(
                        "Times New Roman",
                        Font.PLAIN,
                        15
                )
        );

        cancelButton.setFocusPainted(false);


        JPanel buttonPanel =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.CENTER,
                                10,
                                0
                        )
                );

        buttonPanel.setOpaque(false);

        buttonPanel.add(exploreButton);
        buttonPanel.add(cancelButton);


        mainPanel.add(welcomeLabel);

        mainPanel.add(
                Box.createVerticalStrut(8)
        );

        mainPanel.add(questionLabel);

        mainPanel.add(
                Box.createVerticalStrut(18)
        );

        mainPanel.add(cityBox);

        mainPanel.add(
                Box.createVerticalStrut(18)
        );

        mainPanel.add(buttonPanel);


        dialog.add(
                mainPanel,
                BorderLayout.CENTER
        );


        final String[] selectedCity = {
                Cities.LIST[0]
        };


        exploreButton.addActionListener(e -> {

            selectedCity[0] =
                    (String) cityBox.getSelectedItem();

            dialog.dispose();
        });


        cancelButton.addActionListener(e -> {

            selectedCity[0] =
                    Cities.LIST[0];

            dialog.dispose();
        });


        dialog.setDefaultCloseOperation(
                JDialog.DISPOSE_ON_CLOSE
        );

        dialog.setVisible(true);

        return selectedCity[0];
    }


    // ================= SEARCH + TABLE =================

    private void search() {

        boolean loaded =
                loadTable(
                        searchField.getText().trim(),
                        (String) categoryBox.getSelectedItem(),
                        (String) cityBox.getSelectedItem()
                );

        if (loaded && shown.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "No places found."
            );
        }
    }


    // ================= LOAD TABLE =================

    private boolean loadTable(
            String text,
            String category,
            String city
    ) {

        model.setRowCount(0);

        shown.clear();

        try {

            shown =
                    placeDAO.searchPlaces(
                            text,
                            category,
                            city
                    );

        } catch (SQLException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Could not load places. Is MySQL running?\n"
                            + ex.getMessage()
            );

            return false;
        }


        for (Place p : shown) {

            model.addRow(
                    new Object[]{
                            p.getName(),
                            p.getCategory(),
                            p.getArea(),
                            p.getPhone(),
                            p.getHours(),
                            p.getRatingText()
                    }
            );
        }

        return true;
    }


    // ================= CHECK IF OPEN =================

    private boolean isOpenNow(Place p) {

        LocalTime open =
                LocalTime.parse(
                        p.getOpenTime()
                );

        LocalTime close =
                LocalTime.parse(
                        p.getCloseTime()
                );

        LocalTime now =
                LocalTime.now();


        if (open.isBefore(close)) {

            return !now.isBefore(open)
                    && !now.isAfter(close);
        }

        return !now.isBefore(open)
                || !now.isAfter(close);
    }


    // ================= PLACE DETAILS =================

    void showPlaceDetails(Place place) {

        JDialog dialog =
                new JDialog(
                        this,
                        place.getName(),
                        true
                );

        dialog.setSize(500, 700);

        dialog.setLocationRelativeTo(this);

        dialog.setLayout(
                new BorderLayout()
        );


        JPanel panel = new JPanel();

        panel.setLayout(
                new BoxLayout(
                        panel,
                        BoxLayout.Y_AXIS
                )
        );

        panel.setBorder(
                BorderFactory.createEmptyBorder(
                        20, 25, 20, 25
                )
        );


        // ================= PLACE INFORMATION =================

        JLabel nameLabel =
                new JLabel(
                        place.getName()
                );

        nameLabel.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        24
                )
        );


        JLabel openLabel =
                new JLabel();

        openLabel.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        15
                )
        );


        if (isOpenNow(place)) {

            openLabel.setText(
                    "Open now"
            );

            openLabel.setForeground(
                    new Color(30, 130, 60)
            );

        } else {

            openLabel.setText(
                    "Closed"
            );

            openLabel.setForeground(
                    new Color(180, 0, 0)
            );
        }


        String description =
                place.getDescription();

        if (description == null) {
            description = "";
        }


        JLabel categoryLabel =
                new JLabel(
                        "Category: "
                                + place.getCategory()
                );

        JLabel locationLabel =
                new JLabel(
                        "Address: "
                                + place.getAddress()
                                + ", "
                                + place.getArea()
                                + ", "
                                + place.getCity()
                );

        JLabel phoneLabel =
                new JLabel(
                        "Phone: "
                                + place.getPhone()
                );

        JLabel hoursLabel =
                new JLabel(
                        "Hours: "
                                + place.getHours()
                );


        // ================= AVERAGE RATING =================

        JPanel averageRatingPanel =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.LEFT,
                                2,
                                0
                        )
                );

        averageRatingPanel.setOpaque(false);

        if (place.getReviewCount() == 0) {

            JLabel noRatingLabel =
                    new JLabel(
                            "No ratings yet"
                    );

            noRatingLabel.setFont(
                    new Font(
                            "Arial",
                            Font.PLAIN,
                            14
                    )
            );

            averageRatingPanel.add(
                    noRatingLabel
            );

        } else {

            double average =
                    Math.round(
                            place.getAvgRating() * 10
                    ) / 10.0;

            JLabel averageLabel =
                    new JLabel(
                            average
                                    + " / 5 ("
                                    + place.getReviewCount()
                                    + " reviews)"
                    );

            averageLabel.setFont(
                    new Font(
                            "Arial",
                            Font.PLAIN,
                            14
                    )
            );

            averageRatingPanel.add(
                    averageLabel
            );


            int roundedRating =
                    (int) Math.round(
                            place.getAvgRating()
                    );

            for (int i = 1; i <= 5; i++) {

                JLabel starLabel =
                        new JLabel();

                starLabel.setIcon(
                        new StarIcon(
                                18,
                                i <= roundedRating
                        )
                );

                averageRatingPanel.add(
                        starLabel
                );
            }
        }


        JLabel descriptionLabel =
                new JLabel(
                        "<html><div style='width:400px;'>"
                                + escapeHtml(description)
                                + "</div></html>"
                );


        panel.add(nameLabel);

        panel.add(
                Box.createVerticalStrut(10)
        );

        panel.add(openLabel);

        panel.add(
                Box.createVerticalStrut(15)
        );

        panel.add(categoryLabel);

        panel.add(
                Box.createVerticalStrut(5)
        );

        panel.add(locationLabel);

        panel.add(
                Box.createVerticalStrut(5)
        );

        panel.add(phoneLabel);

        panel.add(
                Box.createVerticalStrut(5)
        );

        panel.add(hoursLabel);

        panel.add(
                Box.createVerticalStrut(8)
        );

        panel.add(averageRatingPanel);

        panel.add(
                Box.createVerticalStrut(15)
        );

        panel.add(descriptionLabel);

        panel.add(
                Box.createVerticalStrut(25)
        );


        // =========================================================
        // ================= WRITE REVIEW ==========================
        // =========================================================

        JLabel ratingLabel =
                new JLabel("Your rating:");

        ratingLabel.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        14
                )
        );

        panel.add(ratingLabel);


        // =========================================================
        // ================= CLICKABLE STARS =======================
        // =========================================================

        JPanel ratingPanel =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.LEFT,
                                2,
                                0
                        )
                );

        ratingPanel.setOpaque(false);

        JButton[] stars =
                new JButton[5];

        final int[] selectedRating = {
                0
        };


        for (int i = 0; i < 5; i++) {

            final int starNumber =
                    i + 1;

            stars[i] =
                    new JButton();

            stars[i].setPreferredSize(
                    new Dimension(
                            42,
                            42
                    )
            );

            stars[i].setBorderPainted(false);

            stars[i].setContentAreaFilled(false);

            stars[i].setFocusPainted(false);

            stars[i].setOpaque(false);

            stars[i].setCursor(
                    new Cursor(
                            Cursor.HAND_CURSOR
                    )
            );


            // Start with empty outlined star

            stars[i].setIcon(
                    new StarIcon(
                            28,
                            false
                    )
            );


            // ================= STAR CLICK =================

            stars[i].addActionListener(e -> {

                selectedRating[0] =
                        starNumber;


                // Change all stars according
                // to selected rating

                for (int j = 0; j < 5; j++) {

                    if (j < selectedRating[0]) {

                        stars[j].setIcon(
                                new StarIcon(
                                        28,
                                        true
                                )
                        );

                    } else {

                        stars[j].setIcon(
                                new StarIcon(
                                        28,
                                        false
                                )
                        );
                    }
                }
            });


            ratingPanel.add(
                    stars[i]
            );
        }


        panel.add(ratingPanel);


        // ================= REVIEW TEXT =================

        panel.add(
                Box.createVerticalStrut(10)
        );


        JLabel reviewLabel =
                new JLabel(
                        "Write your review:"
                );

        panel.add(reviewLabel);


        JTextArea reviewArea =
                new JTextArea(4, 35);

        reviewArea.setLineWrap(true);

        reviewArea.setWrapStyleWord(true);

        reviewArea.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        14
                )
        );


        JScrollPane reviewScroll =
                new JScrollPane(
                        reviewArea
                );

        panel.add(reviewScroll);


        // =========================================================
        // ================= EXISTING REVIEWS ======================
        // =========================================================

        panel.add(
                Box.createVerticalStrut(20)
        );


        JLabel existingReviewsLabel =
                new JLabel(
                        "Reviews from visitors"
                );

        existingReviewsLabel.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        17
                )
        );

        panel.add(existingReviewsLabel);

        panel.add(
                Box.createVerticalStrut(8)
        );


        JPanel reviewsPanel =
                new JPanel();

        reviewsPanel.setLayout(
                new BoxLayout(
                        reviewsPanel,
                        BoxLayout.Y_AXIS
                )
        );

        reviewsPanel.setBackground(
                Color.WHITE
        );

        reviewsPanel.setBorder(
                BorderFactory.createEmptyBorder(
                        5, 5, 5, 5
                )
        );


        try {

            ArrayList<String[]> reviews =
                    reviewDAO.getReviews(
                            place.getId()
                    );


            if (reviews.isEmpty()) {

                JLabel noReviewsLabel =
                        new JLabel(
                                "No reviews yet. Be the first to review this place!"
                        );

                noReviewsLabel.setFont(
                        new Font(
                                "Arial",
                                Font.ITALIC,
                                13
                        )
                );

                noReviewsLabel.setAlignmentX(
                        Component.LEFT_ALIGNMENT
                );

                reviewsPanel.add(
                        noReviewsLabel
                );


            } else {

                for (String[] review :
                        reviews) {

                    String name =
                            review[0];

                    String rating =
                            review[1];

                    String comment =
                            review[2];

                    String date =
                            review[3];


                    // ================= REVIEW CARD =================

                    JPanel reviewCard =
                            new JPanel();

                    reviewCard.setLayout(
                            new BoxLayout(
                                    reviewCard,
                                    BoxLayout.Y_AXIS
                            )
                    );

                    reviewCard.setBackground(
                            new Color(
                                    248,
                                    250,
                                    252
                            )
                    );

                    reviewCard.setBorder(
                            BorderFactory.createCompoundBorder(
                                    BorderFactory.createLineBorder(
                                            new Color(
                                                    220,
                                                    225,
                                                    230
                                            )
                                    ),
                                    BorderFactory.createEmptyBorder(
                                            10,
                                            12,
                                            10,
                                            12
                                    )
                            )
                    );

                    reviewCard.setAlignmentX(
                            Component.LEFT_ALIGNMENT
                    );


                    // ================= NAME + STARS =================

                    JPanel topRow =
                            new JPanel(
                                    new BorderLayout()
                            );

                    topRow.setOpaque(false);

                    topRow.setAlignmentX(
                            Component.LEFT_ALIGNMENT
                    );


                    JLabel nameLabelReview =
                            new JLabel(
                                    escapeHtml(name)
                            );

                    nameLabelReview.setFont(
                            new Font(
                                    "Arial",
                                    Font.BOLD,
                                    14
                            )
                    );


                    // Draw review stars instead of
                    // using Unicode characters

                    int ratingNumber =
                            Integer.parseInt(
                                    rating
                            );

                    JPanel reviewStarsPanel =
                            new JPanel(
                                    new FlowLayout(
                                            FlowLayout.RIGHT,
                                            1,
                                            0
                                    )
                            );

                    reviewStarsPanel.setOpaque(false);

                    for (int i = 1; i <= 5; i++) {

                        JLabel starLabel =
                                new JLabel();

                        starLabel.setIcon(
                                new StarIcon(
                                        18,
                                        i <= ratingNumber
                                )
                        );

                        reviewStarsPanel.add(
                                starLabel
                        );
                    }


                    topRow.add(
                            nameLabelReview,
                            BorderLayout.WEST
                    );

                    topRow.add(
                            reviewStarsPanel,
                            BorderLayout.EAST
                    );


                    reviewCard.add(topRow);

                    reviewCard.add(
                            Box.createVerticalStrut(6)
                    );


                    // ================= COMMENT =================

                    JLabel commentLabel =
                            new JLabel(
                                    "<html><div style='width:390px;'>"
                                            + "\""
                                            + escapeHtml(comment)
                                            + "\""
                                            + "</div></html>"
                            );

                    commentLabel.setFont(
                            new Font(
                                    "Arial",
                                    Font.PLAIN,
                                    13
                            )
                    );

                    commentLabel.setAlignmentX(
                            Component.LEFT_ALIGNMENT
                    );

                    reviewCard.add(
                            commentLabel
                    );


                    reviewCard.add(
                            Box.createVerticalStrut(7)
                    );


                    // ================= DATE =================

                    JLabel dateLabel =
                            new JLabel(
                                    "Reviewed on "
                                            + escapeHtml(date)
                            );

                    dateLabel.setFont(
                            new Font(
                                    "Arial",
                                    Font.ITALIC,
                                    11
                            )
                    );

                    dateLabel.setForeground(
                            new Color(
                                    120,
                                    120,
                                    120
                            )
                    );

                    dateLabel.setAlignmentX(
                            Component.LEFT_ALIGNMENT
                    );

                    reviewCard.add(
                            dateLabel
                    );


                    reviewsPanel.add(
                            reviewCard
                    );

                    reviewsPanel.add(
                            Box.createVerticalStrut(8)
                    );
                }
            }


        } catch (SQLException ex) {

            JLabel errorLabel =
                    new JLabel(
                            "<html>Could not load reviews.<br>"
                                    + escapeHtml(
                                    ex.getMessage()
                            )
                                    + "</html>"
                    );

            errorLabel.setFont(
                    new Font(
                            "Arial",
                            Font.PLAIN,
                            12
                    )
            );

            reviewsPanel.add(
                    errorLabel
            );
        }


        // ================= REVIEW SCROLL =================

        JScrollPane reviewsScroll =
                new JScrollPane(
                        reviewsPanel
                );

        reviewsScroll.setPreferredSize(
                new Dimension(
                        440,
                        180
                )
        );

        reviewsScroll.setBorder(
                BorderFactory.createLineBorder(
                        new Color(
                                225,
                                230,
                                235
                        )
                )
        );

        reviewsScroll.getVerticalScrollBar()
                .setUnitIncrement(12);

        panel.add(reviewsScroll);


        // ================= ALIGN EVERYTHING =================

        for (Component c :
                panel.getComponents()) {

            if (c instanceof JComponent) {

                ((JComponent) c)
                        .setAlignmentX(
                                Component.LEFT_ALIGNMENT
                        );
            }
        }


        // ================= BUTTONS =================

        JButton submitButton =
                new JButton("Submit Review");

        JButton closeButton =
                new JButton("Close");

        JPanel buttonPanel =
                new JPanel();

        JButton favoriteInDialogButton =
                new JButton("Add to Favorites");

        favoriteInDialogButton.addActionListener(
                e -> addToFavorites(dialog, place)
        );

        buttonPanel.add(submitButton);
        buttonPanel.add(favoriteInDialogButton);
        buttonPanel.add(closeButton);


        // =========================================================
        // ================= SUBMIT REVIEW =========================
        // =========================================================

        submitButton.addActionListener(e -> {

            int rating =
                    selectedRating[0];


            // Rating required

            if (rating == 0) {

                JOptionPane.showMessageDialog(
                        dialog,
                        "Please choose a rating from 1 to 5."
                );

                return;
            }


            String comment =
                    reviewArea
                            .getText()
                            .trim();


            // Review required

            if (comment.isEmpty()) {

                JOptionPane.showMessageDialog(
                        dialog,
                        "Please write a review."
                );

                return;
            }


            // Maximum 255 characters

            if (comment.length() > 255) {

                JOptionPane.showMessageDialog(
                        dialog,
                        "Your review cannot be longer than 255 characters."
                );

                return;
            }


            try {

                reviewDAO.saveReview(
                        place.getId(),
                        user.getId(),
                        rating,
                        comment
                );


                JOptionPane.showMessageDialog(
                        dialog,
                        "Your review has been saved successfully!"
                );


                dialog.dispose();


                // Refresh table so new average rating appears

                loadTable(
                        searchField.getText().trim(),
                        (String) categoryBox.getSelectedItem(),
                        (String) cityBox.getSelectedItem()
                );


            } catch (SQLException ex) {

                JOptionPane.showMessageDialog(
                        dialog,
                        "Could not save your review.\n"
                                + ex.getMessage()
                );
            }
        });


        // ================= CLOSE =================

        closeButton.addActionListener(
                e -> dialog.dispose()
        );


        // ================= SHOW DIALOG =================

        dialog.add(
                panel,
                BorderLayout.CENTER
        );

        dialog.add(
                buttonPanel,
                BorderLayout.SOUTH
        );

        dialog.setVisible(true);
    }


    // =============================================================
    // ================= DRAW STAR ICON ============================
    // =============================================================

    private static class StarIcon implements Icon {

        private int size;
        private boolean filled;

        public StarIcon(
                int size,
                boolean filled
        ) {

            this.size = size;
            this.filled = filled;
        }


        @Override
        public int getIconWidth() {
            return size;
        }


        @Override
        public int getIconHeight() {
            return size;
        }


        @Override
        public void paintIcon(
                Component c,
                Graphics g,
                int x,
                int y
        ) {

            Graphics2D g2 =
                    (Graphics2D) g.create();


            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );


            int centerX =
                    x + size / 2;

            int centerY =
                    y + size / 2;


            int outerRadius =
                    size / 2 - 2;

            int innerRadius =
                    outerRadius / 2;


            GeneralPath star =
                    new GeneralPath();


            for (int i = 0; i < 10; i++) {

                double angle =
                        -Math.PI / 2
                                + i * Math.PI / 5;


                int radius =
                        (i % 2 == 0)
                                ? outerRadius
                                : innerRadius;


                double pointX =
                        centerX
                                + Math.cos(angle)
                                * radius;


                double pointY =
                        centerY
                                + Math.sin(angle)
                                * radius;


                if (i == 0) {

                    star.moveTo(
                            pointX,
                            pointY
                    );

                } else {

                    star.lineTo(
                            pointX,
                            pointY
                    );
                }
            }


            star.closePath();


            g2.setColor(
                    new Color(
                            230,
                            170,
                            40
                    )
            );


            if (filled) {

                g2.fill(star);

            } else {

                g2.setStroke(
                        new BasicStroke(2)
                );

                g2.draw(star);
            }


            g2.dispose();
        }
    }


    // ================= HTML SAFETY =================

    private String escapeHtml(String text) {

        if (text == null) {
            return "";
        }

        return text
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }
}