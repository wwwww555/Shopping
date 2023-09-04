package Customers.ShoppingCar;

import java.util.Date;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Product {
    private String productCode;
    private String productName;
    private String manufacturer;
    private Date productionDate;
    private String model;
    private double purchasePrice;
    private double retailPrice;
    private int quantity;

    // 构造函数
    public Product(String productCode, String productName, String manufacturer, Date productionDate,
                   String model, double purchasePrice, double retailPrice, int quantity) {
        this.productCode = productCode;
        this.productName = productName;
        this.manufacturer = manufacturer;
        this.productionDate = productionDate;
        this.model = model;
        this.purchasePrice = purchasePrice;
        this.retailPrice = retailPrice;
        this.quantity = quantity;
    }

    public Product() {

    }

    public Product(String productCode, String productName, double purchasePrice, double retailPrice, int quantity) {
        Product product = new Product(productCode, productName, purchasePrice, retailPrice, quantity);
    }

    public Product(String productCode, String productName, String manufacturer, Date productionDate, String model, double retailPrice, int quantity) {
        this.productCode = productCode;
        this.productName = productName;
        this.manufacturer = manufacturer;
        this.productionDate = productionDate;
        this.model = model;
        this.retailPrice = retailPrice;
        this.quantity = quantity;
    }


    // Getter 和 Setter 方法

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public Date getProductionDate() {
        return productionDate;
    }

    public void setProductionDate(Date productionDate) {
        this.productionDate = productionDate;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public double getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(double purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public double getRetailPrice() {
        return retailPrice;
    }

    public void setRetailPrice(double retailPrice) {
        this.retailPrice = retailPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    // 通过数据库连接和产品编号查询产品信息
    // 通过数据库连接和产品编号查询产品信息
    public static Product getProductByCode(Connection connection, String productCode) throws SQLException {
        String sql = "SELECT * FROM products WHERE product_id = ?";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setString(1, productCode);
        ResultSet resultSet = stmt.executeQuery();

        if (resultSet.next()) {
            String productName = resultSet.getString("product_name");
            String manufacturer = resultSet.getString("manufacturer");
            Date productionDate = resultSet.getDate("production_date");
            String model = resultSet.getString("model");
            double purchasePrice = resultSet.getDouble("purchase_price");
            double retailPrice = resultSet.getDouble("retail_price");
            int quantity = resultSet.getInt("quantity");

            return new Product(productCode, productName, manufacturer, productionDate, model, purchasePrice, retailPrice, quantity);
        } else {
            // 如果没有找到产品，返回 null 或抛出异常
            return null;
        }
    }
}

