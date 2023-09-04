package Customers.ShoppingCar;

import Login.*;

import java.awt.*;
import java.sql.Connection;
import java.util.Map;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.PreparedStatement;


public class ShoppingCar_Car extends JFrame {
    private Connection connection;
    private ShoppingCart shoppingCart;
    private JList<String> cartList;
    private DefaultTableModel cartTableModel;
    private JTable cartTable;


    public ShoppingCar_Car(ShoppingCart shoppingCart) {
        this.connection = connection;
        this.shoppingCart = shoppingCart;
        setTitle("购物车");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout());

        // 创建购物车列表
        String[] columnNames = {"商品ID", "商品名称", "商品数量"};
        DefaultTableModel cartTableModel = new DefaultTableModel(columnNames, 0);
        cartTable = new JTable(cartTableModel);
        JScrollPane cartTableScrollPane = new JScrollPane(cartTable);
        panel.add(cartTableScrollPane, BorderLayout.CENTER);

        // 创建操作按钮面板
        JPanel buttonPanel = new JPanel(new GridLayout(2, 2));
        JButton removeFromCartButton = new JButton("将商品从购物车中移除");
        JButton modifyCartButton = new JButton("修改购物车中的商品");
        JButton checkoutButton = new JButton("付账");
        JButton backButton = new JButton("返回");

        buttonPanel.add(removeFromCartButton);
        buttonPanel.add(modifyCartButton);
        buttonPanel.add(checkoutButton);
        buttonPanel.add(backButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        backButton.addActionListener(e -> dispose());

        removeFromCartButton.addActionListener(e -> {
            int selectedRow = cartTable.getSelectedRow();
            if (selectedRow >= 0) {
                String productId = (String) cartTableModel.getValueAt(selectedRow, 0);
                showRemoveFromCartDialog(productId);
            } else {
                JOptionPane.showMessageDialog(null, "请选择要移除的商品");
            }
        });

        modifyCartButton.addActionListener(e -> {
            int selectedRow = cartTable.getSelectedRow();
            if (selectedRow >= 0) {
                String productId = (String) cartTableModel.getValueAt(selectedRow, 0);
                showModifyCartDialog(productId);
            } else {
                JOptionPane.showMessageDialog(null, "请选择要修改的商品");
            }
        });

        checkoutButton.addActionListener(e -> processCheckout());

        add(panel);
        setVisible(true);

        // 初始化购物车列表
        updateCartList();

    }

    private void showRemoveFromCartDialog(String selectedProduct) {
        // 在这里显示从购物车中移除商品的对话框
        String productCodeToRemove = JOptionPane.showInputDialog(null, "请输入要移除的商品编号:");

        if (productCodeToRemove != null) {
            removeFromCart(productCodeToRemove);
        }
    }

    private void removeFromCart(String productCodeToRemove) {
        // 在购物车中查找要移除的商品
        Product productToRemove = null;
        for (Product product : shoppingCart.getItems().keySet()) {
            if (product.getProductCode().equals(productCodeToRemove)) {
                productToRemove = product;
                break;
            }
        }

        if (productToRemove != null) {
            shoppingCart.removeItem(productToRemove); // 从购物车中移除商品
            updateCartList(); // 更新购物车列表
            JOptionPane.showMessageDialog(null, "商品已从购物车中移除");
        } else {
            JOptionPane.showMessageDialog(null, "购物车中没有该商品");
        }
    }

    public void updateCartList() {
        DefaultTableModel cartTableModel = (DefaultTableModel) cartTable.getModel();
        cartTableModel.setRowCount(0);

        // 遍历购物车中的商品并将它们添加到购物车列表
        for (Map.Entry<Product, Integer> entry : shoppingCart.getItems().entrySet()) {
            Product product = entry.getKey();
            int quantity = entry.getValue();
            String[] rowData = {product.getProductCode(), product.getProductName(), String.valueOf(quantity)};
            cartTableModel.addRow(rowData);
        }
    }

    private void showModifyCartDialog(String selectedProduct) {
        // 这里显示修改购物车商品数量的对话框
        String productCodeToModify = JOptionPane.showInputDialog(null, "请输入要修改数量的商品编号:");
        if (productCodeToModify != null) {
            int newQuantity = Integer.parseInt(JOptionPane.showInputDialog(null, "请输入新的数量:"));
            modifyCartItem(productCodeToModify, newQuantity);
        }
    }

    private void modifyCartItem(String productCodeToModify, int newQuantity) {
        // 在购物车中查找要修改数量的商品
        Product productToModify = null;
        for (Product product : shoppingCart.getItems().keySet()) {
            if (product.getProductCode().equals(productCodeToModify)) {
                productToModify = product;
                break;
            }
        }

        if (productToModify != null) {
            if (newQuantity <= 0) {
                shoppingCart.removeItem(productToModify); // 数量为零或负数时移除商品
            } else {
                shoppingCart.updateItemQuantity(productToModify, newQuantity); // 更新商品数量
            }
            updateCartList(); // 更新购物车列表
            JOptionPane.showMessageDialog(null, "商品数量已更新");
        } else {
            JOptionPane.showMessageDialog(null, "购物车中没有该商品");
        }
    }

    private void processCheckout() {
        // 处理结账逻辑
        boolean paymentSuccessful = simulatePayment();

        if (paymentSuccessful) {
            shoppingCart.clear();
            updateCartList(); // 更新购物车列表
            JOptionPane.showMessageDialog(null, "付款成功，订单已完成");
        } else {
            JOptionPane.showMessageDialog(null, "付款失败，请重试");
        }
    }

    private boolean simulatePayment() {
        return true;
    }

    public static void main(String[] args) {
        Connection connection = ConnetMYSQL.connectDB(); // 创建数据库连接

        SwingUtilities.invokeLater(() -> {
            // 创建一个示例购物车并传递数据库连接给它
            ShoppingCart shoppingCart = new ShoppingCart(connection);
            new ShoppingCar_Car(shoppingCart);
        });
    }
}