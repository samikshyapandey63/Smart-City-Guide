import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;

public class FavoritesPanel extends JPanel {

    private DashboardFrame parent;
    private User user;
    private FavoriteDAO favoriteDAO = new FavoriteDAO();

    private DefaultTableModel model;
    private JTable table;
    private JLabel emptyLabel;
    private ArrayList<Place> shown = new ArrayList<>();

    public FavoritesPanel(DashboardFrame parent, User user) {

        this.parent = parent;
        this.user = user;

        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));


        // ================= TITLE =================

        JLabel title = new JLabel("My Favorites");
        title.setFont(new Font("Arial", Font.BOLD, 25));

        emptyLabel = new JLabel("You have no favorites yet. Add places from the Places page.");
        emptyLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        emptyLabel.setForeground(new Color(110, 110, 110));

        JPanel topPanel = new JPanel(new BorderLayout(0, 8));
        topPanel.setBackground(Color.WHITE);
        topPanel.add(title, BorderLayout.NORTH);
        topPanel.add(emptyLabel, BorderLayout.CENTER);


        // ================= TABLE =================

        String[] columns = {"Name", "Category", "City", "Area", "Phone", "Hours", "Rating"};

        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(30);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);


        // ================= BUTTONS =================

        JButton detailsButton = new JButton("View Details");
        JButton removeButton = new JButton("Remove from Favorites");

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        actionPanel.setBackground(Color.WHITE);
        actionPanel.add(detailsButton);
        actionPanel.add(removeButton);

        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(actionPanel, BorderLayout.SOUTH);


        // ================= ACTIONS =================

        detailsButton.addActionListener(e -> {
            Place place = selectedPlace();
            if (place != null) {
                parent.showPlaceDetails(place);     // same details window as the Places page
                reload();                           // the rating may have changed
            }
        });

        removeButton.addActionListener(e -> {
            Place place = selectedPlace();
            if (place == null) {
                return;
            }

            int choice = JOptionPane.showConfirmDialog(this,
                    "Remove " + place.getName() + " from your favorites?",
                    "Remove favorite", JOptionPane.YES_NO_OPTION);

            if (choice == JOptionPane.YES_OPTION) {
                try {
                    favoriteDAO.removeFavorite(user.getId(), place.getId());
                    reload();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this,
                            "Could not remove the favorite.\n" + ex.getMessage());
                }
            }
        });

        reload();
    }


    // the place selected in the table, or null (after telling the user)
    private Place selectedPlace() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a place first.");
            return null;
        }
        return shown.get(row);
    }


    // loads this user's favorites from MySQL again
    public void reload() {
        model.setRowCount(0);
        shown.clear();

        try {
            shown = favoriteDAO.getFavorites(user.getId());
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Could not load your favorites. Is MySQL running?\n" + ex.getMessage());
        }

        for (Place p : shown) {
            model.addRow(new Object[]{
                    p.getName(), p.getCategory(), p.getCity(), p.getArea(),
                    p.getPhone(), p.getHours(), p.getRatingText()});
        }

        emptyLabel.setVisible(shown.isEmpty());
    }
}
