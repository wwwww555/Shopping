package Customers.ShoppingCar;

import Login.*;
import Customers.CustomersMain;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ShoppingCar_Main extends JFrame {
    private String customerId;
    private ShoppingCart shoppingCart;
    private Connection connection;
    private DefaultTableModel productTableModel;
    private JTable productTable;
    private CustomersMain customermain;
    private ShoppingCar_Car shoppingcar;

    public ShoppingCar_Main(Connection connection, ShoppingCart shoppingCart, CustomersMain customermain) {
        this.connection = connection;
        this.customermain = customermain;
        this.shoppingCart = shoppingCart;

        setTitle("购物");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout());

        JButton viewAllProductsButton = new JButton("查看所有商品");
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(viewAllProductsButton);

        // Create a product table
        String[] columnNames = {"商品编号", "商品名称", "生产厂家", "生产日期", "型号", "零售价格","数量"};
        productTableModel = new DefaultTableModel(columnNames, 0);
        productTable = new JTable(productTableModel);
        JScrollPane productTableScrollPane = new JScrollPane(productTable);
        panel.add(productTableScrollPane, BorderLayout.CENTER);

        JButton refreshButton = new JButton("刷新");
        buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addToCartButton = new JButton("将商品加入购物车");
        JButton viewCartButton = new JButton("查看购物车");
        JButton viewHistoryButton = new JButton("查看购物历史");
        JButton backButton = new JButton("返回");

        buttonPanel.add(refreshButton);
        buttonPanel.add(addToCartButton);
        buttonPanel.add(viewCartButton);
        buttonPanel.add(viewHistoryButton);
        buttonPanel.add(backButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        backButton.addActionListener(e -> goBack());

        add(panel);


        loadProductList();
        showAllProducts();

        refreshButton.addActionListener(e -> {
            showAllProducts(); // 调用显示所有商品的方法
        });

        addToCartButton.addActionListener(e -> {
            showAddToCartDialog(); // 调用添加到购物车对话框的方法
        });

        // 添加查看购物历史按钮的点击事件处理
        viewHistoryButton.addActionListener(e -> {
            String customerId = ""; // 设置具体的customerId值
            showShoppingHistory(customerId); // 调用显示购物历史的方法并传入customerId参数
        });

        viewCartButton.addActionListener(e -> {
            showShoppingCart(); // 调用显示购物车的方法
        });

        setVisible(true);
    }

    public void showShoppingHistory(String customerId) {
        this.customerId = customerId; // 设置customerId值

        // 创建连接
        Connection connection = ConnetMYSQL.connectDB();
        SwingUtilities.invokeLater(() -> {
            new PurchaseHistory(connection, this.customerId);
        });
    }


    // Add a method to display all products
    private void showAllProducts() {
        // Clear the product table
        productTableModel.setRowCount(0);

        // Load and display all products
        List<Product> products = loadProductsFromDatabase();
        for (Product product : products) {
            // Add each product to the table
            productTableModel.addRow(new Object[]{
                    product.getProductCode(),
                    product.getProductName(),
                    product.getManufacturer(),
                    product.getProductionDate(),
                    product.getModel(),
                    product.getRetailPrice(),
                    product.getQuantity()
            });
        }
        System.out.println("Number of products loaded from database: " + products.size());
    }

    private void showAddToCartDialog() {
        int selectedRowIndex = productTable.getSelectedRow();
        if (selectedRowIndex != -1) {
            // Get the product code of the selected product
            String productCode = (String) productTable.getValueAt(selectedRowIndex, 0);
            String productName = (String) productTable.getValueAt(selectedRowIndex, 1);
            int quantityToAdd = Integer.parseInt(JOptionPane.showInputDialog(null, "请输入要购买的数量:"));

            Product product = lookupProductByCode(productCode);

            if (product != null && quantityToAdd > 0 && quantityToAdd <= product.getQuantity()) {
                shoppingCart.addItem(product, quantityToAdd);
                JOptionPane.showMessageDialog(null, "已将 " + productName + " 添加到购物车");

                // Update the cart list
                if (shoppingcar != null) {
                    shoppingcar.updateCartList();
                }
            } else {
                JOptionPane.showMessageDialog(null, "商品不存在或数量不合法");
            }
        } else {
            JOptionPane.showMessageDialog(null, "请选择要添加到购物车的商品");
        }
    }


    private void updateShoppingCartInDatabase() {
        try {
            // 将购物车项插入购物车表
            String insertSql = "INSERT INTO shopping_cart (customer_id, product_id, quantity) VALUES (?, ?, ?)";
            PreparedStatement insertStmt = connection.prepareStatement(insertSql);
            for (Map.Entry<Product, Integer> entry : shoppingCart.getItems().entrySet()) {
                Product product = entry.getKey();
                int quantity = entry.getValue();
                if (product != null) {
                    insertStmt.setString(1, customerId);
                    insertStmt.setString(2, product.getProductCode());
                    insertStmt.setInt(3, quantity);
                    insertStmt.executeUpdate();
                }
            }
            insertStmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadProductList() {
        // Load the product list from the database
        List<Product> products = loadProductsFromDatabase();
        for (Product product : products) {
            // Add each product to the table
            productTableModel.addRow(new Object[]{
                    product.getProductCode(),
                    product.getProductName(),
                    product.getManufacturer(),
                    product.getProductionDate(),
                    product.getModel(),
                    product.getRetailPrice(),
                    product.getQuantity()
            });
        }
    }

    private List<Product> loadProductsFromDatabase() {
        List<Product> products = new ArrayList<>();
        try {
            String sql = "SELECT * FROM products";
            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                String productCode = resultSet.getString("product_id");
                String productName = resultSet.getString("product_name");
                String manufacturer = resultSet.getString("manufacturer");
                String productionDateStr = resultSet.getString("production_date");
                Date productionDate = parseDate(productionDateStr); // Parse the date string
                String model = resultSet.getString("model");
                double retailPrice = resultSet.getDouble("retail_price");
                int quantity = resultSet.getInt("quantity");

                Product product = new Product(productCode, productName, manufacturer, productionDate, model, retailPrice, quantity);
                products.add(product);
            }
            resultSet.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    // Helper method to parse date string
    private Date parseDate(String dateStr) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            return dateFormat.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Product lookupProductByCode(String productCodeToLookup) {
        Product product = null;
        try {
            String sql = "SELECT * FROM products WHERE product_id = ?";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, productCodeToLookup);
            ResultSet resultSet = stmt.executeQuery();

            if (resultSet.next()) {
                String productCode = resultSet.getString("product_id");
                String productName = resultSet.getString("product_name");
                String manufacturer = resultSet.getString("manufacturer");
                String productionDateStr = resultSet.getString("production_date");
                Date productionDate = parseDate(productionDateStr); // Parse the date string
                String model = resultSet.getString("model");
                double retailPrice = resultSet.getDouble("retail_price");
                int quantity = resultSet.getInt("quantity");

                product = new Product(productCode, productName, manufacturer, productionDate, model, retailPrice, quantity);
            }

            resultSet.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return product;
    }

    private void showShoppingCart() {
        if (shoppingcar == null) {
            shoppingcar = new ShoppingCar_Car(shoppingCart);
        } else {
            shoppingcar.updateCartList();
            shoppingcar.setVisible(true);
        }
    }

    private void goBack() {
        setVisible(false);
        // Return to the previous window
        customermain.setVisible(true);
    }

    public static void main(String[] args) {
        Connection connection = ConnetMYSQL.connectDB();
        SwingUtilities.invokeLater(() -> {
            CustomersMain customerRootWindow = new CustomersMain(connection);
            ShoppingCart shoppingCart = new ShoppingCart(connection);
            new ShoppingCar_Main(connection,shoppingCart, customerRootWindow);
        });
    }
}

