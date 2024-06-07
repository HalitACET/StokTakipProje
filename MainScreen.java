import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainScreen extends JFrame {
    public MainScreen() {
        setTitle("Stok Takip Sistemi - Ana Ekran");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.LIGHT_GRAY);

        panel.add(Box.createRigidArea(new Dimension(0, 10))); // Üstte boşluk

        JButton productsButton = createStyledButton("Ürünler");
        productsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new ProductsScreen().setVisible(true);
            }
        });
        panel.add(productsButton);

        panel.add(Box.createRigidArea(new Dimension(0, 10))); // Butonlar arası boşluk

        JButton stockMovementsButton = createStyledButton("Stok Hareketleri");
        stockMovementsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new StockMovementsScreen().setVisible(true);
            }
        });
        panel.add(stockMovementsButton);

        panel.add(Box.createRigidArea(new Dimension(0, 10))); // Butonlar arası boşluk

        JButton suppliersButton = createStyledButton("Tedarikçiler");
        suppliersButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new SuppliersScreen().setVisible(true);
            }
        });
        panel.add(suppliersButton);

        panel.add(Box.createRigidArea(new Dimension(0, 10))); // Butonlar arası boşluk

        JButton ordersButton = createStyledButton("Siparişler");
        ordersButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new OrdersScreen().setVisible(true);
            }
        });
        panel.add(ordersButton);

        panel.add(Box.createRigidArea(new Dimension(0, 10))); // Butonlar arası boşluk

        JButton salesReportsButton = createStyledButton("Satış Raporları");
        salesReportsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new SalesReportsScreen().setVisible(true);
            }
        });
        panel.add(salesReportsButton);

        panel.add(Box.createRigidArea(new Dimension(0, 10))); // Altta boşluk

        add(panel);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(200, 40));
        button.setBackground(new Color(70, 130, 180));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        return button;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MainScreen().setVisible(true);
            }
        });
    }
}

class ProductsScreen extends JFrame {
    public ProductsScreen() {
        setTitle("Ürünler");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
    }
}

class StockMovementsScreen extends JFrame {
    public StockMovementsScreen() {
        setTitle("Stok Hareketleri");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
    }
}

class SuppliersScreen extends JFrame {
    public SuppliersScreen() {
        setTitle("Tedarikçiler");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
    }
}

class OrdersScreen extends JFrame {
    public OrdersScreen() {
        setTitle("Siparişler");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
    }
}

class SalesReportsScreen extends JFrame {
    public SalesReportsScreen() {
        setTitle("Satış Raporları");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
    }
}
