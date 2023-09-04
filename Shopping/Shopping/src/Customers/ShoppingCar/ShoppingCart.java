package Customers.ShoppingCar;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ShoppingCart {
    private Map<Product, Integer> items; // 用于存储购物车中的商品和对应的数量
    private Connection connection; // 数据库连接

    public ShoppingCart(Connection connection) {
        this.connection = connection;
        items = new HashMap<>();
    }

    // 获取购物车中的所有商品和对应的数量
    public Map<Product, Integer> getItems() {
        return items;
    }

    // 添加商品到购物车
    public void addItem(Product product, int quantity) {
        if (product != null && quantity > 0) {
            if (items.containsKey(product)) {
                // 如果购物车中已经有该商品，更新数量
                int existingQuantity = items.get(product);
                items.put(product, existingQuantity + quantity);
            } else {
                // 否则，将商品添加到购物车
                items.put(product, quantity);
            }
        }
    }

    // 从购物车中移除商品
    public void removeItem(Product product) {
        if (product != null && items.containsKey(product)) {
            items.remove(product);
        }
    }

    // 更新购物车中商品的数量
    public void updateItemQuantity(Product product, int newQuantity) {
        if (product != null && items.containsKey(product) && newQuantity > 0) {
            items.put(product, newQuantity);
        }
    }

    // 清空购物车
    public void clearCart() {
        items.clear();
    }

    // 从数据库加载购物车信息
    public void loadCartFromDatabase(String customerId) {
        String sql = "SELECT product_id, quantity FROM shopping_cart WHERE customer_id = ?";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, customerId);
            ResultSet resultSet = stmt.executeQuery();

            while (resultSet.next()) {
                String productId = resultSet.getString("product_id");
                int quantity = resultSet.getInt("quantity");

                // 根据商品ID从数据库或商品列表中查找商品对象
                Product product = lookupProductById(productId);

                if (product != null) {
                    // 将商品添加到购物车
                    items.put(product, quantity);
                }
            }

            resultSet.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 根据商品ID查找商品信息（需要根据你的数据库结构进行调整）
    private Product lookupProductById(String productId) {
        // 在这里添加根据商品ID查询商品信息的逻辑
        String sql = "SELECT * FROM products WHERE product_id = ?";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, productId);
            ResultSet resultSet = stmt.executeQuery();

            if (resultSet.next()) {
                // 从查询结果中获取商品信息
                String productCode = resultSet.getString("product_code");
                String productName = resultSet.getString("product_name");
                String manufacturer = resultSet.getString("manufacturer");
                Date productionDate = resultSet.getDate("production_date");
                String model = resultSet.getString("model");
                double purchasePrice = resultSet.getDouble("purchase_price");
                double retailPrice = resultSet.getDouble("retail_price");
                int quantity = resultSet.getInt("quantity");

                // 创建商品对象并返回
                Product product = new Product();
                product.setProductCode(productCode);
                product.setProductName(productName);
                product.setManufacturer(manufacturer);
                product.setProductionDate(productionDate);
                product.setModel(model);
                product.setPurchasePrice(purchasePrice);
                product.setRetailPrice(retailPrice);
                product.setQuantity(quantity);

                return product;
            }

            resultSet.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // 如果找不到对应商品，返回 null
        return null;
    }

    // 输出购物车信息
    public void printCart() {
        for (Map.Entry<Product, Integer> entry : items.entrySet()) {
            Product product = entry.getKey();
            int quantity = entry.getValue();
            System.out.println("商品: " + product.getProductName() + ", 数量: " + quantity);
        }
    }

    public Map<Product, Integer> getShoppingHistory(String customerId) {
        Map<Product, Integer> shoppingHistory = new HashMap<>();

        // 编写 SQL 查询，从购物历史表中获取数据
        String sql = "SELECT product_id, quantity FROM shopping_history WHERE customer_id = ?";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, customerId);
            ResultSet resultSet = stmt.executeQuery();

            while (resultSet.next()) {
                String productId = resultSet.getString("product_id");
                int quantity = resultSet.getInt("quantity");

                // 根据商品ID从数据库或商品列表中查找商品对象
                Product product = lookupProductById(productId);

                if (product != null) {
                    // 将商品添加到购物历史
                    shoppingHistory.put(product, quantity);
                }
            }

            resultSet.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return shoppingHistory;
    }

    public void clear() {
        items.clear(); // 清空购物车中的商品
    }
}
