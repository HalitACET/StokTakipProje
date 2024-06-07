import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;

public class ProductsScreen extends JFrame {
    private JTable table;
    private JTextField idField, nameField, categoryIdField, stockField, priceField;

    public ProductsScreen() {
        setTitle("Stok Takip Sistemi - Ürünler");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        inputPanel.add(new JLabel("Ürün ID:"), gbc);

        gbc.gridx = 1;
        idField = new JTextField(15);
        idField.setEditable(false); // ID alanını sadece okunabilir yapıyoruz
        inputPanel.add(idField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        inputPanel.add(new JLabel("Ürün Adı:"), gbc);

        gbc.gridx = 1;
        nameField = new JTextField(15);
        inputPanel.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        inputPanel.add(new JLabel("Kategori ID:"), gbc);

        gbc.gridx = 1;
        categoryIdField = new JTextField(15);
        inputPanel.add(categoryIdField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        inputPanel.add(new JLabel("Stok Miktarı:"), gbc);

        gbc.gridx = 1;
        stockField = new JTextField(15);
        inputPanel.add(stockField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        inputPanel.add(new JLabel("Birim Fiyatı:"), gbc);

        gbc.gridx = 1;
        priceField = new JTextField(15);
        inputPanel.add(priceField, gbc);

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
                addProduct();
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
                updateProduct();
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
                deleteProduct();
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
gbc.gridy = 8;
inputPanel.add(backButton, gbc);
        
        table = new JTable();
        table.setGridColor(Color.LIGHT_GRAY);
        table.setShowGrid(true);
        table.setFillsViewportHeight(true);
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    loadSelectedProduct();
                }
            }
        });

        add(new JScrollPane(table), BorderLayout.CENTER);

        loadProducts();
    }

    private void loadProducts() {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "select A.*,B.kategori_adi from stoktakip.urunler A INNER JOIN stoktakip.kategoriler B on A.kategori_id=B.kategori_id";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            DefaultTableModel model = new DefaultTableModel(new String[]{"Ürün ID", "Ürün Adı", "Kategori ID", "Stok Miktarı", "Birim Fiyatı", "Kategori Adı"}, 0);
            while (resultSet.next()) {
                model.addRow(new Object[]{resultSet.getInt("urun_id"), resultSet.getString("urun_adi"), resultSet.getInt("kategori_id"), resultSet.getInt("stok_miktari"), resultSet.getDouble("birim_fiyati"), resultSet.getString("kategori_adi")});
            }
            table.setModel(model);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addProduct() {
        String name = nameField.getText();
        int categoryId = Integer.parseInt(categoryIdField.getText());
        int stock = Integer.parseInt(stockField.getText());
        double price = Double.parseDouble(priceField.getText());

        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "INSERT INTO urunler (urun_adi, kategori_id, stok_miktari, birim_fiyati) VALUES (?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, name);
            statement.setInt(2, categoryId);
            statement.setInt(3, stock);
            statement.setDouble(4, price);
            statement.executeUpdate();
            loadProducts();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateProduct() {
        int id = Integer.parseInt(idField.getText());
        String name = nameField.getText();
        int categoryId = Integer.parseInt(categoryIdField.getText());
        int stock = Integer.parseInt(stockField.getText());
        double price = Double.parseDouble(priceField.getText());

        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "UPDATE urunler SET urun_adi = ?, kategori_id = ?, stok_miktari = ?, birim_fiyati = ? WHERE urun_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, name);
            statement.setInt(2, categoryId);
            statement.setInt(3, stock);
            statement.setDouble(4, price);
            statement.setInt(5, id);
            statement.executeUpdate();
            loadProducts();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteProduct() {
        int id = Integer.parseInt(idField.getText());

        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "DELETE FROM urunler WHERE urun_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, id);
            statement.executeUpdate();
            loadProducts();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadSelectedProduct() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            DefaultTableModel model = (DefaultTableModel) table.getModel();
            idField.setText(model.getValueAt(selectedRow, 0).toString());
            nameField.setText(model.getValueAt(selectedRow, 1).toString());
            categoryIdField.setText(model.getValueAt(selectedRow, 2).toString());
            stockField.setText(model.getValueAt(selectedRow, 3).toString());
            priceField.setText(model.getValueAt(selectedRow, 4).toString());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ProductsScreen().setVisible(true);
            }
        });
    }
}