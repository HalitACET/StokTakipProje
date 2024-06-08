import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;

public class SuppliersScreen extends JFrame {
    private JTable table;
    private JTextField idField, nameField, contactField;

    public SuppliersScreen() {
        setTitle("Stok Takip Sistemi - Tedarikçiler");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0;
        gbc.gridy = 0;
        inputPanel.add(new JLabel("Tedarikçi ID:"), gbc);

        gbc.gridx = 1;
        idField = new JTextField(15);
        idField.setEditable(false); // ID alanını sadece okunabilir yapıyoruz
        inputPanel.add(idField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        inputPanel.add(new JLabel("Tedarikçi Adı:"), gbc);

        gbc.gridx = 1;
        nameField = new JTextField(15);
        inputPanel.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        inputPanel.add(new JLabel("İletişim Bilgisi:"), gbc);

        gbc.gridx = 1;
        contactField = new JTextField(15);
        inputPanel.add(contactField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;

        JButton addButton = new JButton("Ekle");
        addButton.setBackground(Color.GREEN);
        addButton.setForeground(Color.WHITE);
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addSupplier();
            }
        });
        inputPanel.add(addButton, gbc);

        gbc.gridy = 4;
        JButton updateButton = new JButton("Güncelle");
        updateButton.setBackground(Color.ORANGE);
        updateButton.setForeground(Color.WHITE);
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateSupplier();
            }
        });
        inputPanel.add(updateButton, gbc);

        gbc.gridy = 5;
        JButton deleteButton = new JButton("Sil");
        deleteButton.setBackground(Color.RED);
        deleteButton.setForeground(Color.WHITE);
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteSupplier();
            }
        });
        inputPanel.add(deleteButton, gbc);

        gbc.gridy = 6;
        JButton backButton = new JButton("Geri Git");
        backButton.setBackground(Color.GRAY);
        backButton.setForeground(Color.WHITE);
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // SuppliersScreen penceresini kapat
                new MainScreen().setVisible(true); // Ana ekranı göster
            }
        });
        inputPanel.add(backButton, gbc);

        add(inputPanel, BorderLayout.NORTH);

        table = new JTable();
        table.setGridColor(Color.LIGHT_GRAY);
        table.setShowGrid(true);
        table.setFillsViewportHeight(true);
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    loadSelectedSupplier();
                }
            }
        });

        add(new JScrollPane(table), BorderLayout.CENTER);

        loadSuppliers();
    }

    private void loadSuppliers() {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM tedarikciler";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            DefaultTableModel model = new DefaultTableModel(new String[]{"Tedarikçi ID", "Tedarikçi Adı", "İletişim Bilgisi"}, 0);
            while (resultSet.next()) {
                model.addRow(new Object[]{resultSet.getInt("tedarikci_id"), resultSet.getString("tedarikci_adi"), resultSet.getString("iletisim_bilgisi")});
            }
            table.setModel(model);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addSupplier() {
        String name = nameField.getText();
        String contact = contactField.getText();

        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "INSERT INTO tedarikciler (tedarikci_adi, iletisim_bilgisi) VALUES (?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, name);
            statement.setString(2, contact);
            statement.executeUpdate();
            loadSuppliers();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateSupplier() {
        int id = Integer.parseInt(idField.getText());
        String name = nameField.getText();
        String contact = contactField.getText();

        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "UPDATE tedarikciler SET tedarikci_adi = ?, iletisim_bilgisi = ? WHERE tedarikci_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, name);
            statement.setString(2, contact);
            statement.setInt(3, id);
            statement.executeUpdate();
            loadSuppliers();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteSupplier() {
        int id = Integer.parseInt(idField.getText());

        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "DELETE FROM tedarikciler WHERE tedarikci_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, id);
            statement.executeUpdate();
            loadSuppliers();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadSelectedSupplier() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            DefaultTableModel model = (DefaultTableModel) table.getModel();
            idField.setText(model.getValueAt(selectedRow, 0).toString());
            nameField.setText(model.getValueAt(selectedRow, 1).toString());
            contactField.setText(model.getValueAt(selectedRow, 2).toString());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new SuppliersScreen().setVisible(true);
            }
        });
    }
}
