import javax.swing.*;
import java.awt.*;
import java.awt.geom.GeneralPath;
import java.sql.SQLException;
import java.util.ArrayList;

// the "Recommended for you" row that sits above the places table
public class RecommendationPanel extends JPanel {

    private static final int CARD_COUNT = 5;

    private DashboardFrame parent;
    private User user;
    private RecommendationDAO recommendationDAO = new RecommendationDAO();

    private JLabel titleLabel;
    private JLabel subtitleLabel;
    private JPanel cardsPanel;
    private String city = "";

    public RecommendationPanel(DashboardFrame parent, User user) {

        this.parent = parent;
        this.user = user;

        setLayout(new BorderLayout(0, 6));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));


        // ================= HEADER =================

        titleLabel = new JLabel("Recommended for you");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 17));

        subtitleLabel = new JLabel(" ");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        subtitleLabel.setForeground(new Color(110, 110, 110));

        JPanel textPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        textPanel.setOpaque(false);
        textPanel.add(titleLabel);
        textPanel.add(subtitleLabel);

        JButton clearButton = new JButton("Clear my history");
        clearButton.setFont(new Font("Arial", Font.PLAIN, 11));
        clearButton.setMargin(new Insets(2, 8, 2, 8));
        clearButton.setFocusPainted(false);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.add(textPanel, BorderLayout.WEST);
        headerPanel.add(clearButton, BorderLayout.EAST);


        // ================= CARDS =================

        cardsPanel = new JPanel(new GridLayout(1, CARD_COUNT, 10, 0));
        cardsPanel.setBackground(Color.WHITE);
        cardsPanel.setPreferredSize(new Dimension(100, 150));

        add(headerPanel, BorderLayout.NORTH);
        add(cardsPanel, BorderLayout.CENTER);


        // ================= CLEAR HISTORY =================

        clearButton.addActionListener(e -> {

            int choice = JOptionPane.showConfirmDialog(this,
                    "Forget your searches, the places you opened and the places you hid?\n"
                            + "Your favorites and reviews are kept.",
                    "Clear my history", JOptionPane.YES_NO_OPTION);

            if (choice == JOptionPane.YES_OPTION) {
                try {
                    recommendationDAO.clearHistory(user.getId());
                    reload(city);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this,
                            "Could not clear your history.\n" + ex.getMessage());
                }
            }
        });
    }


    // works out the picks for this city again and redraws the cards
    public void reload(String city) {

        this.city = city;
        cardsPanel.removeAll();

        try {
            ArrayList<Recommendation> picks =
                    recommendationDAO.getRecommendations(user.getId(), city, CARD_COUNT);

            boolean anyPersonal = false;
            for (Recommendation r : picks) {
                if (r.isPersonal()) {
                    anyPersonal = true;
                }
            }

            if (anyPersonal) {
                titleLabel.setText("Recommended for you");
                subtitleLabel.setText("Based on your searches, favorites and ratings");
            } else {
                titleLabel.setText("Popular in " + city);
                subtitleLabel.setText("Search and rate places to get picks made for you");
            }

            if (picks.isEmpty()) {
                cardsPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
                JLabel none = new JLabel("No places to recommend in " + city + " yet.");
                none.setFont(new Font("Arial", Font.ITALIC, 13));
                cardsPanel.add(none);

            } else {
                cardsPanel.setLayout(new GridLayout(1, CARD_COUNT, 10, 0));
                for (Recommendation r : picks) {
                    cardsPanel.add(makeCard(r));
                }
                // empty slots keep every card the same width
                for (int i = picks.size(); i < CARD_COUNT; i++) {
                    JPanel blank = new JPanel();
                    blank.setOpaque(false);
                    cardsPanel.add(blank);
                }
            }

        } catch (SQLException ex) {
            titleLabel.setText("Recommendations");
            subtitleLabel.setText("Not available yet. Run recommendations.sql in Workbench first.");
            cardsPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        }

        cardsPanel.revalidate();
        cardsPanel.repaint();
    }


    // ================= ONE CARD =================

    private JPanel makeCard(Recommendation rec) {

        Place place = rec.getPlace();

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(new Color(248, 250, 252));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 225, 230)),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));

        // name (wraps onto two lines when it is long)
        String shortName = place.getName();
        if (shortName.length() > 19) {
            shortName = shortName.substring(0, 18) + "...";
        }
        JLabel nameLabel = new JLabel(shortName);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 13));
        nameLabel.setToolTipText(place.getName());

        JLabel infoLabel = new JLabel(place.getCategory() + " - " + place.getArea());
        infoLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        infoLabel.setForeground(new Color(110, 110, 110));

        // rating: one small star + the text
        JLabel ratingLabel = new JLabel(place.getRatingText());
        ratingLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        ratingLabel.setIcon(new SmallStar(13));
        ratingLabel.setIconTextGap(4);

        // why this place was picked (a text area wraps long sentences reliably)
        JTextArea reasonText = new JTextArea(rec.getReason());
        reasonText.setFont(new Font("Arial", Font.ITALIC, 11));
        reasonText.setForeground(new Color(60, 100, 150));
        reasonText.setLineWrap(true);
        reasonText.setWrapStyleWord(true);
        reasonText.setEditable(false);
        reasonText.setFocusable(false);
        reasonText.setOpaque(false);
        reasonText.setBorder(null);
        reasonText.setPreferredSize(new Dimension(135, 32));
        reasonText.setMaximumSize(new Dimension(150, 32));

        JButton viewButton = makeSmallButton("View");
        JButton saveButton = makeSmallButton("Save");
        JButton hideButton = makeSmallButton("Hide");

        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        buttonRow.setOpaque(false);
        buttonRow.add(viewButton);
        buttonRow.add(saveButton);
        buttonRow.add(hideButton);

        card.add(nameLabel);
        card.add(Box.createVerticalStrut(2));
        card.add(infoLabel);
        card.add(Box.createVerticalStrut(3));
        card.add(ratingLabel);
        card.add(Box.createVerticalStrut(4));
        card.add(reasonText);
        card.add(Box.createVerticalGlue());
        card.add(buttonRow);

        for (Component c : card.getComponents()) {
            if (c instanceof JComponent) {
                ((JComponent) c).setAlignmentX(Component.LEFT_ALIGNMENT);
            }
        }


        // ================= CARD BUTTONS =================

        viewButton.addActionListener(e -> parent.showPlaceDetails(place));

        saveButton.addActionListener(e -> {
            parent.addToFavorites(parent, place);
            reload(city);
        });

        hideButton.addActionListener(e -> {
            try {
                recommendationDAO.hidePlace(user.getId(), place.getId());
                reload(city);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this,
                        "Could not hide this place.\n" + ex.getMessage());
            }
        });

        return card;
    }


    private JButton makeSmallButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 11));
        button.setMargin(new Insets(2, 7, 2, 7));
        button.setFocusPainted(false);
        return button;
    }


    // a small filled star next to the rating
    private static class SmallStar implements Icon {

        private int size;

        public SmallStar(int size) {
            this.size = size;
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
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            double centerX = x + size / 2.0;
            double centerY = y + size / 2.0;
            double outer = size / 2.0 - 1;
            double inner = outer / 2;

            GeneralPath star = new GeneralPath();
            for (int i = 0; i < 10; i++) {
                double angle = -Math.PI / 2 + i * Math.PI / 5;
                double radius = (i % 2 == 0) ? outer : inner;
                double px = centerX + Math.cos(angle) * radius;
                double py = centerY + Math.sin(angle) * radius;
                if (i == 0) {
                    star.moveTo(px, py);
                } else {
                    star.lineTo(px, py);
                }
            }
            star.closePath();

            g2.setColor(new Color(230, 170, 40));
            g2.fill(star);
            g2.dispose();
        }
    }


    private String escapeHtml(String text) {
        if (text == null) {
            return "";
        }
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
