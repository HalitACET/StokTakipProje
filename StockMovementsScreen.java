import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;

public class StockMovementsScreen extends JFrame {
    private JTable table;
    private JTextField idField, productIdField, amountField, typeField, dateField;

    public StockMovementsScreen() {
        setTitle("Stok Takip Sistemi - Stok Hareketleri");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0;
        gbc.gridy = 0;
        inputPanel.add(new JLabel("Hareket ID:"), gbc);

        gbc.gridx = 1;
        idField = new JTextField(15);
        idField.setEditable(false); // ID alanını sadece okunabilir yapıyoruz
        inputPanel.add(idField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        inputPanel.add(new JLabel("Ürün ID:"), gbc);

        gbc.gridx = 1;
        productIdField = new JTextField(15);
        inputPanel.add(productIdField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        inputPanel.add(new JLabel("Miktar:"), gbc);

        gbc.gridx = 1;
        amountField = new JTextField(15);
        inputPanel.add(amountField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        inputPanel.add(new JLabel("Hareket Türü:"), gbc);

        gbc.gridx = 1;
        typeField = new JTextField(15);
        inputPanel.add(typeField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        inputPanel.add(new JLabel("Hareket Tarihi:"), gbc);

        gbc.gridx = 1;
        dateField = new JTextField(15);
        inputPanel.add(dateField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;

        JButton addButton = new JButton("Ekle");
        addButton.setBackground(Color.GREEN);
        addButton.setForeground(Color.WHITE);
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addMovement();
            }
        });
        inputPanel.add(addButton, gbc);

        gbc.gridy = 6;
        JButton updateButton = new JButton("Güncelle");
        updateButton.setBackground(Color.ORANGE);
        updateButton.setForeground(Color.WHITE);
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateMovement();
            }
        });
        inputPanel.add(updateButton, gbc);

        gbc.gridy = 7;
        JButton deleteButton = new JButton("Sil");
        deleteButton.setBackground(Color.RED);
        deleteButton.setForeground(Color.WHITE);
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteMovement();
            }
        });
        inputPanel.add(deleteButton, gbc);

        gbc.gridy = 8;
        JButton backButton = new JButton("Geri Git");
        backButton.setBackground(Color.GRAY);
        backButton.setForeground(Color.WHITE);
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // StockMovementsScreen penceresini kapat
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
                    loadSelectedMovement();
                }
            }
        });

        add(new JScrollPane(table), BorderLayout.CENTER);

        loadMovements();
    }

    private void loadMovements() {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT A.*, B.urun_adi FROM stok_hareketleri A INNER JOIN urunler B ON A.urun_id = B.urun_id";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            DefaultTableModel model = new DefaultTableModel(new String[]{"Hareket ID", "Ürün ID", "Miktar", "Hareket Türü", "Hareket Tarihi", "Ürün Adı"}, 0);
            while (resultSet.next()) {
                model.addRow(new Object[]{resultSet.getInt("hareket_id"), resultSet.getInt("urun_id"), resultSet.getInt("miktar"), resultSet.getString("hareket_turu"), resultSet.getString("hareket_tarihi"), resultSet.getString("urun_adi")});
            }
            table.setModel(model);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addMovement() {
        int productId = Integer.parseInt(productIdField.getText());
        int amount = Integer.parseInt(amountField.getText());
        String type = typeField.getText();
        String date = dateField.getText();

        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "INSERT INTO stok_hareketleri (urun_id, miktar, hareket_turu, hareket_tarihi) VALUES (?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, productId);
            statement.setInt(2, amount);
            statement.setString(3, type);
            statement.setString(4, date);
            statement.executeUpdate();
            loadMovements();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (type.equals("Çıkış")) {
            try (Connection connection = DatabaseConnection.getConnection()) {
                String query = "UPDATE urunler SET stok_miktari = stok_miktari - ? WHERE urun_id = ?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setInt(1, amount);
                statement.setInt(2, productId);
                statement.executeUpdate();
                loadMovements();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            try (Connection connection = DatabaseConnection.getConnection()) {
                String query = "UPDATE satis_raporlari SET toplam_satis = toplam_satis + ? WHERE urun_id = ?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setInt(1, amount);
                statement.setInt(2, productId);
                statement.executeUpdate();
                loadMovements();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else if (type.equals("Giriş")) {
            try (Connection connection = DatabaseConnection.getConnection()) {
                String query = "UPDATE urunler SET stok_miktari = stok_miktari + ? WHERE urun_id = ?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setInt(1, amount);
                statement.setInt(2, productId);
                statement.executeUpdate();
                loadMovements();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateMovement() {
        int id = Integer.parseInt(idField.getText());
        int productId = Integer.parseInt(productIdField.getText());
        int amount = Integer.parseInt(amountField.getText());
        String type = typeField.getText();
        String date = dateField.getText();

        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "UPDATE stok_hareketleri SET urun_id = ?, miktar = ?, hareket_turu = ?, hareket_tarihi = ? WHERE hareket_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, productId);
            statement.setInt(2, amount);
            statement.setString(3, type);
            statement.setString(4, date);
            statement.setInt(5, id);
            statement.executeUpdate();
            loadMovements();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteMovement() {
        int id = Integer.parseInt(idField.getText());

        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "DELETE FROM stok_hareketleri WHERE hareket_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, id);
            statement.executeUpdate();
            loadMovements();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadSelectedMovement() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            DefaultTableModel model = (DefaultTableModel) table.getModel();
            idField.setText(model.getValueAt(selectedRow, 0).toString());
            productIdField.setText(model.getValueAt(selectedRow, 1).toString());
            amountField.setText(model.getValueAt(selectedRow, 2).toString());
            typeField.setText(model.getValueAt(selectedRow, 3).toString());
            dateField.setText(model.getValueAt(selectedRow, 4).toString());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new StockMovementsScreen().setVisible(true);
            }
        });
    }
}
