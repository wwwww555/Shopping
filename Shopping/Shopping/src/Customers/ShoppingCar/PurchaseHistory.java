package Customers.ShoppingCar;

import Login.ConnetMYSQL;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class PurchaseHistory extends JFrame {
    private Connection connection;
    private JTable purchaseHistoryTable;

    public PurchaseHistory(Connection connection, String customerId) {
        this.connection = connection;

        setTitle("购物历史");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(600, 600);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout());

        // Create a purchase history table
        String[] columnNames = {"商品名称", "购买时间"};
        DefaultTableModel purchaseHistoryTableModel = new DefaultTableModel(columnNames, 0);
        purchaseHistoryTable = new JTable(purchaseHistoryTableModel);
        JScrollPane purchaseHistoryTableScrollPane = new JScrollPane(purchaseHistoryTable);
        panel.add(purchaseHistoryTableScrollPane, BorderLayout.CENTER);

        refreshPurchaseHistory(customerId, purchaseHistoryTableModel); // Load the purchase history from the database

        JButton refreshButton = new JButton("刷新");
        panel.add(refreshButton, BorderLayout.SOUTH);
        refreshButton.addActionListener(e -> {
            refreshPurchaseHistory(customerId, purchaseHistoryTableModel);
        });

        add(panel);

        setVisible(true);
    }

    private void refreshPurchaseHistory(String customerId, DefaultTableModel purchaseHistoryTableModel) {
        purchaseHistoryTableModel.setRowCount(0);

        try {
            String sql = "SELECT product_name, purchase_date FROM shopping_history WHERE customer_id = ?";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, customerId);
            ResultSet resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                String productName = resultSet.getString("product_name");
                String purchaseDate = resultSet.getString("purchase_date");
                purchaseHistoryTableModel.addRow(new Object[]{productName, purchaseDate});
            }
            resultSet.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Connection connection = ConnetMYSQL.connectDB();
        SwingUtilities.invokeLater(() -> {
            new PurchaseHistory(connection, "123456");
        });
    }
}



