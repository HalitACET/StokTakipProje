import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class SalesReportsScreen extends JFrame {
    private JTable table;

    public SalesReportsScreen() {
        setTitle("Stok Takip Sistemi - Satış Raporları");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton loadButton = new JButton("Raporları Yükle");
        loadButton.setBackground(Color.BLUE);
        loadButton.setForeground(Color.WHITE);
        loadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadReports();
            }
        });
        panel.add(loadButton, BorderLayout.NORTH);

        JButton backButton = new JButton("Geri Git");
        backButton.setBackground(Color.GRAY);
        backButton.setForeground(Color.WHITE);
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // SalesReportsScreen penceresini kapat
                new MainScreen().setVisible(true); // Ana ekranı göster
            }
        });
        panel.add(backButton, BorderLayout.SOUTH);
        
        table = new JTable();
        table.setGridColor(Color.LIGHT_GRAY);
        table.setShowGrid(true);
        table.setFillsViewportHeight(true);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        add(panel);
    }

    private void loadReports() {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "select A.*,B.urun_adi from stoktakip.satis_raporlari A inner join stoktakip.urunler B on A.urun_id=B.urun_id order by A.toplam_satis desc";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            DefaultTableModel model = new DefaultTableModel(new String[]{"Rapor ID", "Ürün ID", "Toplam Satış", "Rapor Tarihi", "Ürün Adı"}, 0);
            while (resultSet.next()) {
                model.addRow(new Object[]{resultSet.getInt("rapor_id"), resultSet.getInt("urun_id"), resultSet.getInt("toplam_satis"), resultSet.getDate("rapor_tarihi"), resultSet.getString("urun_adi")});
            }
            table.setModel(model);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new SalesReportsScreen().setVisible(true);
            }
        });
    }
}
