package Admin;

import Login.ConnetMYSQL;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Vector;

public class CustomerManage extends JFrame {
    private JTabbedPane tabbedPane;
    private Connection connection;
    private JButton backButton;

    public CustomerManage() {
        setTitle("客户管理");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);

        connection = ConnetMYSQL.connectDB();

        tabbedPane = new JTabbedPane();

        addListCustomersTab();
        addSearchCustomerTab();
        addDeleteCustomerTab();

        add(tabbedPane, BorderLayout.CENTER);

        backButton = new JButton("返回");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                AdminMain adminRootWindow = new AdminMain();
                adminRootWindow.setVisible(true);
            }
        });
        add(backButton, BorderLayout.SOUTH);
    }

    private void addListCustomersTab() {
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BorderLayout());

        Vector<Vector<String>> data = new Vector<>();
        Vector<String> columnNames = new Vector<>();
        columnNames.add("客户ID");
        columnNames.add("用户名");
        columnNames.add("用户级别");
        columnNames.add("注册时间");
        columnNames.add("累计消费金额");
        columnNames.add("手机号");
        columnNames.add("邮箱");

        JTable table = new JTable(data, columnNames);
        JScrollPane scrollPane = new JScrollPane(table);

        listPanel.add(scrollPane, BorderLayout.CENTER);

        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT customer_id, username, membership_level, registration_date, total_spent, phone, email FROM customers");
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Vector<String> row = new Vector<>();
                row.add(resultSet.getString("customer_id"));
                row.add(resultSet.getString("username"));
                row.add(resultSet.getString("membership_level"));
                row.add(resultSet.getString("registration_date"));
                row.add(resultSet.getString("total_spent"));
                row.add(resultSet.getString("phone"));
                row.add(resultSet.getString("email"));
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

        // 将按钮面板添加到顶部
        listPanel.add(buttonPanel, BorderLayout.NORTH);

        tabbedPane.addTab("列出所有客户信息", listPanel);
    }

    private void updateTableData(Vector<Vector<String>> data, JTable table) {
        try {
            Statement stmt = connection.createStatement();
            ResultSet resultSet = stmt.executeQuery("SELECT * FROM customers");

            // 清空旧数据
            data.clear();

            // 重新填充数据
            while (resultSet.next()) {
                Vector<String> row = new Vector<>();
                row.add(resultSet.getString("customer_id"));
                row.add(resultSet.getString("username"));
                row.add(resultSet.getString("membership_level"));
                row.add(resultSet.getString("registration_date"));
                row.add(resultSet.getString("total_spent"));
                row.add(resultSet.getString("phone"));
                row.add(resultSet.getString("email"));
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

    private void addDeleteCustomerTab() {
        JPanel deletePanel = new JPanel();
        deletePanel.setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(3, 2));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // 增加内边距

        JTextField customerIdField = new JTextField();
        JTextField usernameField = new JTextField();

        inputPanel.add(new JLabel("输入客户ID："));
        inputPanel.add(customerIdField);
        inputPanel.add(new JLabel("输入用户名："));
        inputPanel.add(usernameField);

        deletePanel.add(inputPanel, BorderLayout.CENTER); // 居中显示

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20)); // 增加内边距

        JButton deleteButton = new JButton("删除客户");
        deleteButton.setPreferredSize(new Dimension(120, 30)); // 增大按钮大小
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String customerId = customerIdField.getText();
                String username = usernameField.getText();

                if (!customerId.isEmpty() && !username.isEmpty()) {
                    int option = JOptionPane.showConfirmDialog(null, "确定要删除该客户吗？", "确认删除", JOptionPane.YES_NO_OPTION);
                    if (option == JOptionPane.YES_OPTION) {
                        try {
                            PreparedStatement preparedStatement = connection.prepareStatement(
                                    "DELETE FROM customers WHERE customer_id = ? AND username = ?");
                            preparedStatement.setString(1, customerId);
                            preparedStatement.setString(2, username);
                            int rowsAffected = preparedStatement.executeUpdate();

                            if (rowsAffected > 0) {
                                JOptionPane.showMessageDialog(null, "客户删除成功");
                                customerIdField.setText("");
                                usernameField.setText("");
                            } else {
                                JOptionPane.showMessageDialog(null, "未找到匹配的客户信息");
                            }

                            preparedStatement.close();
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                            JOptionPane.showMessageDialog(null, "删除客户失败");
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "请输入客户ID和用户名");
                }
            }
        });

        buttonPanel.add(deleteButton);
        deletePanel.add(buttonPanel, BorderLayout.SOUTH);

        tabbedPane.addTab("删除客户信息", deletePanel);
    }


    private void addSearchCustomerTab() {
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(3, 2));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextField searchByIdField = new JTextField();
        JTextField searchByUsernameField = new JTextField();

        inputPanel.add(new JLabel("按客户ID查询："));
        inputPanel.add(searchByIdField);
        inputPanel.add(new JLabel("按用户名查询："));
        inputPanel.add(searchByUsernameField);

        searchPanel.add(inputPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        JButton searchButton = new JButton("查询");
        searchButton.setPreferredSize(new Dimension(120, 30));
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String customerId = searchByIdField.getText();
                String username = searchByUsernameField.getText();

                if (!customerId.isEmpty() && username.isEmpty()) {
                    // 执行按ID查询的逻辑，显示查询结果
                    displayCustomerInfoById(customerId);
                } else if (customerId.isEmpty() && !username.isEmpty()) {
                    // 执行按用户名查询的逻辑，显示查询结果
                    displayCustomerInfoByUsername(username);
                } else {
                    JOptionPane.showMessageDialog(null, "请输入客户ID或用户名");
                }
            }
        });

        buttonPanel.add(searchButton);
        searchPanel.add(buttonPanel, BorderLayout.SOUTH);

        tabbedPane.addTab("查询客户信息", searchPanel);
    }

    private void displayCustomerInfo(String customerInfo) {
        JOptionPane.showMessageDialog(null, customerInfo, "客户信息", JOptionPane.INFORMATION_MESSAGE);
    }


    private void displayCustomerInfoById(String customerId) {
        try {
            // 执行按ID查询的逻辑，获取客户信息
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT username, membership_level, registration_date, total_spent, phone, email " +
                            "FROM customers " +
                            "WHERE customer_id = ?");
            preparedStatement.setString(1, customerId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                // 从结果集中获取客户信息
                String username = resultSet.getString("username");
                String membershipLevel = resultSet.getString("membership_level");
                String registrationDate = resultSet.getString("registration_date");
                String totalSpent = resultSet.getString("total_spent");
                String phoneNumber = resultSet.getString("phone");
                String email = resultSet.getString("email");

                // 创建一个新的窗口来显示客户信息
                JFrame infoFrame = new JFrame("客户信息");
                JPanel infoPanel = new JPanel(new GridLayout(6, 2));
                infoPanel.add(new JLabel("用户名:"));
                infoPanel.add(new JLabel(username));
                infoPanel.add(new JLabel("用户级别:"));
                infoPanel.add(new JLabel(membershipLevel));
                infoPanel.add(new JLabel("注册时间:"));
                infoPanel.add(new JLabel(registrationDate));
                infoPanel.add(new JLabel("累计消费金额:"));
                infoPanel.add(new JLabel(totalSpent));
                infoPanel.add(new JLabel("手机号:"));
                infoPanel.add(new JLabel(phoneNumber));
                infoPanel.add(new JLabel("邮箱:"));
                infoPanel.add(new JLabel(email));

                infoFrame.add(infoPanel);
                infoFrame.setSize(400, 300);
                infoFrame.setLocationRelativeTo(this);
                infoFrame.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(null, "未找到客户信息");
            }

            resultSet.close();
            preparedStatement.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "查询客户信息失败");
        }
    }

    private void displayCustomerInfoByUsername(String username) {
        try {
            // 执行按用户名查询的逻辑，获取客户信息
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT customer_id, membership_level, registration_date, total_spent, phone, email " +
                            "FROM customers " +
                            "WHERE username = ?");
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                // 从结果集中获取客户信息
                String customerId = resultSet.getString("customer_id");
                String membershipLevel = resultSet.getString("membership_level");
                String registrationDate = resultSet.getString("registration_date");
                String totalSpent = resultSet.getString("total_spent");
                String phoneNumber = resultSet.getString("phone");
                String email = resultSet.getString("email");

                // 创建一个新的窗口来显示客户信息
                JFrame infoFrame = new JFrame("客户信息");
                JPanel infoPanel = new JPanel(new GridLayout(6, 2));
                infoPanel.add(new JLabel("客户ID:"));
                infoPanel.add(new JLabel(customerId));
                infoPanel.add(new JLabel("用户级别:"));
                infoPanel.add(new JLabel(membershipLevel));
                infoPanel.add(new JLabel("注册时间:"));
                infoPanel.add(new JLabel(registrationDate));
                infoPanel.add(new JLabel("累计消费金额:"));
                infoPanel.add(new JLabel(totalSpent));
                infoPanel.add(new JLabel("手机号:"));
                infoPanel.add(new JLabel(phoneNumber));
                infoPanel.add(new JLabel("邮箱:"));
                infoPanel.add(new JLabel(email));

                infoFrame.add(infoPanel);
                infoFrame.setSize(400, 300);
                infoFrame.setLocationRelativeTo(null);
                infoFrame.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(null, "未找到匹配的客户信息");
            }

            resultSet.close();
            preparedStatement.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "查询客户信息失败");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                CustomerManage app = new CustomerManage();
                app.setVisible(true);
            }
        });
    }
}