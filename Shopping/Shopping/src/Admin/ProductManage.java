package Admin;

import Login.ConnetMYSQL;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Vector;
public class ProductManage extends JFrame {
    private JTabbedPane tabbedPane;
    private Connection connection;
    private JButton backButton;

    public ProductManage() {
        setTitle("商品管理");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);

        // 创建选项卡面板
        tabbedPane = new JTabbedPane();

        // 连接数据库
        connection = ConnetMYSQL.connectDB();

        backButton = new JButton("返回"); // 初始化 backButton
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 隐藏当前窗口
                setVisible(false);
                // 创建 AdminRootWindow 并显示
                AdminMain adminRootWindow = new AdminMain();
                adminRootWindow.setVisible(true);
            }
        });
        add(backButton, BorderLayout.SOUTH);

        // 添加选项卡
        addListProductsTab();
        addAddProductTab();
        addEditProductTab();
        addSearchProductTab();
        addDeleteProductTab();

        // 将选项卡面板添加到窗口
        add(tabbedPane);
    }

    private void addListProductsTab() {
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BorderLayout());

        // 创建表格来显示商品信息
        Vector<Vector<String>> data = new Vector<>();
        Vector<String> columnNames = new Vector<>();
        columnNames.add("商品编号");
        columnNames.add("商品名称");
        columnNames.add("生产厂家");
        columnNames.add("生产日期");
        columnNames.add("型号");
        columnNames.add("进货价");
        columnNames.add("零售价格");
        columnNames.add("数量");

        JTable table = new JTable(data, columnNames);
        JScrollPane scrollPane = new JScrollPane(table);

        listPanel.add(scrollPane, BorderLayout.CENTER);

        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT product_id, product_name, manufacturer, production_date, model, purchase_price, retail_price,quantity FROM products");
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Vector<String> row = new Vector<>();
                row.add(resultSet.getString("product_id"));
                row.add(resultSet.getString("product_name"));
                row.add(resultSet.getString("manufacturer"));
                row.add(resultSet.getString("production_date"));
                row.add(resultSet.getString("model"));
                row.add(resultSet.getString("purchase_price"));
                row.add(resultSet.getString("retail_price"));
                row.add(resultSet.getString("quantity"));
                data.add(row);
            }

            resultSet.close();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // 创建更新按钮
        JButton updateButton = new JButton("更新");
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 调用更新表格数据的方法
                updateTableData(data, table);
            }
        });

        // 创建一个面板来容纳按钮
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.add(updateButton);
        buttonPanel.add(backButton);

        // 将按钮面板添加到顶部
        listPanel.add(buttonPanel, BorderLayout.NORTH);

        tabbedPane.addTab("商品展示", listPanel);
    }

    // 更新表格数据的方法
    private void updateTableData(Vector<Vector<String>> data, JTable table) {
        try {
            Statement stmt = connection.createStatement();
            ResultSet resultSet = stmt.executeQuery("SELECT * FROM products");

            // 清空旧数据
            data.clear();

            // 重新填充数据
            while (resultSet.next()) {
                Vector<String> row = new Vector<>();
                row.add(resultSet.getString("product_id"));
                row.add(resultSet.getString("product_name"));
                row.add(resultSet.getString("manufacturer"));
                row.add(resultSet.getString("production_date"));
                row.add(resultSet.getString("model"));
                row.add(resultSet.getString("purchase_price"));
                row.add(resultSet.getString("retail_price"));
                row.add(resultSet.getString("quantity"));
                // 添加其他列的数据
                data.add(row);
            }

            // 刷新表格
            table.revalidate();
            table.repaint();

            stmt.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void addAddProductTab() {
        JPanel addPanel = new JPanel();
        addPanel.setLayout(new GridLayout(9, 2));

        // 添加商品信息的输入字段
        JTextField productIdField = new JTextField();
        JTextField productNameField = new JTextField();
        JTextField manufacturerField = new JTextField();
        JTextField productionDateField = new JTextField();
        JTextField modelField = new JTextField();
        JTextField purchasePriceField = new JTextField();
        JTextField retailPriceField = new JTextField();
        JTextField quantityField = new JTextField();

        addPanel.add(new JLabel("商品编号"));
        addPanel.add(productIdField);
        addPanel.add(new JLabel("商品名称"));
        addPanel.add(productNameField);
        addPanel.add(new JLabel("生产厂家"));
        addPanel.add(manufacturerField);
        addPanel.add(new JLabel("生产日期"));
        addPanel.add(productionDateField);
        addPanel.add(new JLabel("型号"));
        addPanel.add(modelField);
        addPanel.add(new JLabel("进货价"));
        addPanel.add(purchasePriceField);
        addPanel.add(new JLabel("零售价格"));
        addPanel.add(retailPriceField);
        addPanel.add(new JLabel("数量"));
        addPanel.add(quantityField);

        JButton addButton = new JButton("添加");
        addButton.setPreferredSize(new Dimension(100, 30));
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 从输入字段中获取商品信息
                String productId = productIdField.getText();
                String productName = productNameField.getText();
                String manufacturer = manufacturerField.getText();
                String productionDate = productionDateField.getText();
                String model = modelField.getText();
                String purchasePrice = purchasePriceField.getText();
                String retailPrice = retailPriceField.getText();
                String quantity = quantityField.getText();

                // 将商品信息插入数据库
                try {
                    PreparedStatement stmt = connection.prepareStatement("INSERT INTO products VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
                    stmt.setString(1, productId);
                    stmt.setString(2, productName);
                    stmt.setString(3, manufacturer);
                    stmt.setString(4, productionDate);
                    stmt.setString(5, model);
                    stmt.setString(6, purchasePrice);
                    stmt.setString(7, retailPrice);
                    stmt.setString(8, quantity);

                    stmt.executeUpdate();
                    stmt.close();
                    JOptionPane.showMessageDialog(null, "商品已添加");

                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "添加商品失败");

                } finally {
                    // 清空输入字段的文本内容
                    productIdField.setText("");
                    productNameField.setText("");
                    manufacturerField.setText("");
                    productionDateField.setText("");
                    modelField.setText("");
                    purchasePriceField.setText("");
                    retailPriceField.setText("");
                    quantityField.setText("");
                }
            }
        });

        addPanel.add(addButton);

        tabbedPane.addTab("添加商品", addPanel);
    }

    private void addEditProductTab() {
        JPanel editPanel = new JPanel();
        editPanel.setLayout(new GridLayout(9, 2));

        // 添加商品信息的输入字段
        JTextField productIdField = new JTextField();
        JTextField productNameField = new JTextField();
        JTextField manufacturerField = new JTextField();
        JTextField productionDateField = new JTextField();
        JTextField modelField = new JTextField();
        JTextField purchasePriceField = new JTextField();
        JTextField retailPriceField = new JTextField();
        JTextField quantityField = new JTextField();

        editPanel.add(new JLabel("商品编号"));
        editPanel.add(productIdField);
        editPanel.add(new JLabel("商品名称"));
        editPanel.add(productNameField);
        editPanel.add(new JLabel("生产厂家"));
        editPanel.add(manufacturerField);
        editPanel.add(new JLabel("生产日期"));
        editPanel.add(productionDateField);
        editPanel.add(new JLabel("型号"));
        editPanel.add(modelField);
        editPanel.add(new JLabel("进货价"));
        editPanel.add(purchasePriceField);
        editPanel.add(new JLabel("零售价格"));
        editPanel.add(retailPriceField);
        editPanel.add(new JLabel("数量"));
        editPanel.add(quantityField);

        // 添加按钮来执行商品信息更新
        JButton updateButton = new JButton("更新");
        updateButton.setPreferredSize(new Dimension(100, 30));
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 从输入字段中获取商品信息
                String productId = productIdField.getText();
                String productName = productNameField.getText();
                String manufacturer = manufacturerField.getText();
                String productionDate = productionDateField.getText();
                String model = modelField.getText();
                String purchasePrice = purchasePriceField.getText();
                String retailPrice = retailPriceField.getText();
                String quantity = quantityField.getText();

                // 执行商品信息更新
                try {
                    PreparedStatement stmt = connection.prepareStatement(
                            "UPDATE products SET product_name = ?, manufacturer = ?, " +
                                    "production_date = ?, model = ?, purchase_price = ?, " +
                                    "retail_price = ?, quantity = ? WHERE product_id = ?");

                    stmt.setString(1, productName);
                    stmt.setString(2, manufacturer);
                    stmt.setString(3, productionDate);
                    stmt.setString(4, model);
                    stmt.setString(5, purchasePrice);
                    stmt.setString(6, retailPrice);
                    stmt.setString(7, quantity);
                    stmt.setString(8, productId);

                    int rowsUpdated = stmt.executeUpdate();
                    stmt.close();

                    if (rowsUpdated > 0) {
                        JOptionPane.showMessageDialog(null, "商品信息已更新");
                    } else {
                        JOptionPane.showMessageDialog(null, "未找到该商品或更新失败");
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "更新商品信息失败");
                }
            }
        });

        editPanel.add(updateButton);

        tabbedPane.addTab("修改商品信息", editPanel);
    }


    private void addDeleteProductTab() {
        JPanel deletePanel = new JPanel();
        deletePanel.setLayout(new GridLayout(3, 2));

        // 添加商品编号的输入字段
        JTextField productIdField = new JTextField();

        // 添加按钮来执行商品信息删除
        JButton deleteButton = new JButton("删除");
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 获取用户输入的商品编号
                String productId = productIdField.getText();

                // 提示用户确认删除操作
                int confirm = JOptionPane.showConfirmDialog(null,
                        "确认删除商品？删除后无法恢复，请确认是否继续删除操作。",
                        "确认删除", JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    // 执行商品信息删除
                    try {
                        PreparedStatement stmt = connection.prepareStatement(
                                "DELETE FROM products WHERE product_id = ?");
                        stmt.setString(1, productId);

                        int rowsDeleted = stmt.executeUpdate();
                        stmt.close();

                        if (rowsDeleted > 0) {
                            JOptionPane.showMessageDialog(null, "商品已删除");
                        } else {
                            JOptionPane.showMessageDialog(null, "未找到该商品或删除失败");
                        }
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(null, "删除商品信息失败");
                    } finally {
                        // 清空输入字段的文本内容
                        productIdField.setText("");
                    }
                }
            }
        });

        deletePanel.add(new JLabel("商品编号"));
        deletePanel.add(productIdField);
        deletePanel.add(deleteButton);

        tabbedPane.addTab("删除商品信息", deletePanel);
    }


    private void addSearchProductTab() {
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new GridLayout(3, 2));

        // 添加商品ID的输入字段
        JTextField productIdField = new JTextField();
        searchPanel.add(new JLabel("商品ID"));
        searchPanel.add(productIdField);

        // 添加商品名称的输入字段
        JTextField productNameField = new JTextField();
        searchPanel.add(new JLabel("商品名称"));
        searchPanel.add(productNameField);

        JButton searchButton = new JButton("查询");
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 获取用户输入的查询条件
                String productId = productIdField.getText();
                String productName = productNameField.getText();

                // 构建查询语句
                StringBuilder queryBuilder = new StringBuilder("SELECT * FROM products WHERE");

                if (!productId.isEmpty()) {
                    queryBuilder.append(" product_id = '" + productId + "'");
                } else if (!productName.isEmpty()) {
                    queryBuilder.append(" product_name LIKE '%" + productName + "%'");
                } else {
                    JOptionPane.showMessageDialog(null, "请输入商品ID或商品名称进行查询");
                    return;
                }

                // 执行查询并显示结果
                try {
                    Statement stmt = connection.createStatement();
                    ResultSet resultSet = stmt.executeQuery(queryBuilder.toString());

                    // 创建表格来显示查询结果
                    Vector<Vector<String>> data = new Vector<>();
                    Vector<String> columnNames = new Vector<>();
                    columnNames.add("商品编号");
                    columnNames.add("商品名称");
                    columnNames.add("生产厂家");
                    columnNames.add("生产日期");
                    columnNames.add("型号");
                    columnNames.add("进货价");
                    columnNames.add("零售价格");
                    columnNames.add("数量");

                    boolean found = false; // 用于标记是否找到匹配的商品

                    while (resultSet.next()) {
                        Vector<String> row = new Vector<>();
                        row.add(resultSet.getString("product_id"));
                        row.add(resultSet.getString("product_name"));
                        row.add(resultSet.getString("manufacturer"));
                        row.add(resultSet.getString("production_date"));
                        row.add(resultSet.getString("model"));
                        row.add(resultSet.getString("purchase_price"));
                        row.add(resultSet.getString("retail_price"));
                        row.add(resultSet.getString("quantity"));
                        data.add(row);
                        found = true; // 找到匹配的商品
                    }

                    stmt.close();

                    if (found) {
                        // 创建并显示结果表格
                        JTable resultTable = new JTable(data, columnNames);
                        JScrollPane scrollPane = new JScrollPane(resultTable);
                        JOptionPane.showMessageDialog(null, scrollPane, "查询结果", JOptionPane.PLAIN_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(null, "未找到匹配的商品");
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "查询商品信息失败");
                }finally {
                    // 清空输入字段的文本内容
                    productIdField.setText("");
                    productNameField.setText("");
                }
            }
        });

        searchPanel.add(searchButton);

        tabbedPane.addTab("查询商品信息", searchPanel);
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                ProductManage app = new ProductManage();
                app.setVisible(true);
            }
        });
    }
}
