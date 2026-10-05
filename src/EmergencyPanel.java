import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;

public class EmergencyPanel extends JPanel {

    private EmergencyDAO emergencyDAO = new EmergencyDAO();

    private JLabel title;
    private JLabel infoLabel;
    private DefaultTableModel model;
    private String city = "";

    public EmergencyPanel() {

        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));


        // ================= TITLE =================

        title = new JLabel("Emergency Numbers");
        title.setFont(new Font("Arial", Font.BOLD, 25));

        infoLabel = new JLabel(" ");
        infoLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        infoLabel.setForeground(new Color(110, 110, 110));

        JPanel topPanel = new JPanel(new BorderLayout(0, 8));
        topPanel.setBackground(Color.WHITE);
        topPanel.add(title, BorderLayout.NORTH);
        topPanel.add(infoLabel, BorderLayout.CENTER);


        // ================= TABLE =================

        String[] columns = {"Service", "Number"};

        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(model);
        table.setRowHeight(42);
        table.setFont(new Font("Arial", Font.PLAIN, 20));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 15));

        JLabel note = new JLabel("In a life-threatening emergency, call right away.");
        note.setFont(new Font("Arial", Font.ITALIC, 13));
        note.setForeground(new Color(180, 0, 0));

        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(note, BorderLayout.SOUTH);
    }


    // shows the numbers of the given city (called again whenever the city changes)
    public void setCity(String city) {
        this.city = city;
        title.setText("Emergency Numbers - " + city);
        load();
    }


    private void load() {
        model.setRowCount(0);

        ArrayList<String[]> contacts;
        try {
            contacts = emergencyDAO.getContacts(city);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Could not load emergency numbers. Is MySQL running?\n" + ex.getMessage());
            return;
        }

        for (String[] contact : contacts) {
            model.addRow(new Object[]{contact[0], contact[1]});
        }

        if (contacts.isEmpty()) {
            infoLabel.setText("No emergency numbers are saved for " + city + " yet.");
        } else {
            infoLabel.setText("Showing the numbers for " + city
                    + ". Change the city on the Places page.");
        }
    }
}