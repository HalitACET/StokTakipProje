import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;

public class OrdersScreen extends JFrame {
    private JTable table;
    private JTextField idField, customerField, dateField, statusField;

    public OrdersScreen() {
        setTitle("Stok Takip Sistemi - Siparişler");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0;
        gbc.gridy = 0;
        inputPanel.add(new JLabel("Sipariş ID:"), gbc);

        gbc.gridx = 1;
        idField = new JTextField(15);
        idField.setEditable(false); // ID alanını sadece okunabilir yapıyoruz
        inputPanel.add(idField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        inputPanel.add(new JLabel("Müşteri Adı:"), gbc);

        gbc.gridx = 1;
        customerField = new JTextField(15);
        inputPanel.add(customerField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        inputPanel.add(new JLabel("Sipariş Tarihi:"), gbc);

        gbc.gridx = 1;
        dateField = new JTextField(15);
        inputPanel.add(dateField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        inputPanel.add(new JLabel("Durum:"), gbc);

        gbc.gridx = 1;
        statusField = new JTextField(15);
        inputPanel.add(statusField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;

        JButton addButton = new JButton("Ekle");
        addButton.setBackground(Color.GREEN);
        addButton.setForeground(Color.WHITE);
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addOrder();
            }
        });
        inputPanel.add(addButton, gbc);

        gbc.gridy = 5;
        JButton updateButton = new JButton("Güncelle");
        updateButton.setBackground(Color.ORANGE);
        updateButton.setForeground(Color.WHITE);
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateOrder();
            }
        });
        inputPanel.add(updateButton, gbc);

        gbc.gridy = 6;
        JButton deleteButton = new JButton("Sil");
        deleteButton.setBackground(Color.RED);
        deleteButton.setForeground(Color.WHITE);
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteOrder();
            }
        });
        inputPanel.add(deleteButton, gbc);

        add(inputPanel, BorderLayout.NORTH);

        JButton backButton = new JButton("Geri Git");
backButton.setBackground(Color.GRAY);
backButton.setForeground(Color.WHITE);
backButton.addActionListener(new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
        dispose(); // OrdersScreen penceresini kapat
        new MainScreen().setVisible(true); // Ana ekranı göster
    }
});
gbc.gridy = 7;
inputPanel.add(backButton, gbc);


        table = new JTable();
        table.setGridColor(Color.LIGHT_GRAY);
        table.setShowGrid(true);
        table.setFillsViewportHeight(true);
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    loadSelectedOrder();
                }
            }
        });

        add(new JScrollPane(table), BorderLayout.CENTER);

        loadOrders();
    }

    private void loadOrders() {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM siparisler";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            DefaultTableModel model = new DefaultTableModel(new String[]{"Sipariş ID", "Müşteri Adı", "Sipariş Tarihi", "Durum"}, 0);
            while (resultSet.next()) {
                model.addRow(new Object[]{resultSet.getInt("siparis_id"), resultSet.getString("musteri_adi"), resultSet.getString("siparis_tarihi"), resultSet.getString("durum")});
            }
            table.setModel(model);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addOrder() {
        String customer = customerField.getText();
        String date = dateField.getText();
        String status = statusField.getText();

        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "INSERT INTO siparisler (musteri_adi, siparis_tarihi, durum) VALUES (?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, customer);
            statement.setString(2, date);
            statement.setString(3, status);
            statement.executeUpdate();
            loadOrders();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateOrder() {
        int id = Integer.parseInt(idField.getText());
        String customer = customerField.getText();
        String date = dateField.getText();
        String status = statusField.getText();

        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "UPDATE siparisler SET musteri_adi = ?, siparis_tarihi = ?, durum = ? WHERE siparis_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, customer);
            statement.setString(2, date);
            statement.setString(3, status);
            statement.setInt(4, id);
            statement.executeUpdate();
            loadOrders();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteOrder() {
        int id = Integer.parseInt(idField.getText());

        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "DELETE FROM siparisler WHERE siparis_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, id);
            statement.executeUpdate();
            loadOrders();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadSelectedOrder() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            DefaultTableModel model = (DefaultTableModel) table.getModel();
            idField.setText(model.getValueAt(selectedRow, 0).toString());
            customerField.setText(model.getValueAt(selectedRow, 1).toString());
            dateField.setText(model.getValueAt(selectedRow, 2).toString());
            statusField.setText(model.getValueAt(selectedRow, 3).toString());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new OrdersScreen().setVisible(true);
            }
        });
    }
}
