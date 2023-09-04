package Customers.CustomerManage;

import Login.ConnetMYSQL;
import Customers.CustomersMain;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChangePassword extends JFrame {

    private final Connection connection;
    private final CustomersMain customerRootWindow; // 添加CustomerRootWindow的引用

    public ChangePassword(Connection connection, CustomersMain customerRootWindow) {
        this.connection = connection;
        this.customerRootWindow = customerRootWindow; // 初始化CustomerRootWindow的引用
        setTitle("密码管理");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(5, 2));

        JLabel usernameLabel = new JLabel("用户名:");
        JTextField usernameField = new JTextField();
        usernameField.setPreferredSize(new Dimension(200, 25));
        JLabel currentPasswordLabel = new JLabel("当前密码:");
        JPasswordField currentPasswordField = new JPasswordField();
        JLabel newPasswordLabel = new JLabel("新密码:");
        JPasswordField newPasswordField = new JPasswordField();

        JButton backButton = new JButton("返回");
        JButton changePasswordButton = new JButton("修改密码");

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 返回按钮的操作，关闭当前密码管理窗口并显示CustomerRootWindow窗口
                dispose();
                customerRootWindow.setVisible(true);
            }
        });

        changePasswordButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 获取输入的当前密码、新密码和用户名
                String currentPassword = new String(currentPasswordField.getPassword());
                String newPassword = new String(newPasswordField.getPassword());
                String username = usernameField.getText();

                // 调用修改密码的方法
                boolean passwordChanged = changePassword(username, currentPassword, newPassword);

                if (passwordChanged) {
                    JOptionPane.showMessageDialog(ChangePassword.this, "密码修改成功");
                } else {
                    JOptionPane.showMessageDialog(ChangePassword.this, "密码修改失败，请检查当前密码是否正确和新密码是否合法");
                }

            }
        });

        panel.add(usernameLabel);
        panel.add(usernameField);
        panel.add(currentPasswordLabel);
        panel.add(currentPasswordField);
        panel.add(newPasswordLabel);
        panel.add(newPasswordField);
        panel.add(backButton);
        panel.add(changePasswordButton);

        add(panel);

        setVisible(true);
    }

    public boolean changePassword(String username, String currentPassword, String newPassword) {
        //用于验证当前密码的正确性
        if (verifyCurrentPassword(username, currentPassword)) {
            // 如果当前密码正确，再验证新密码是否合法
            if (isPasswordValid(newPassword)) {
                // 更新数据库中的密码
                boolean passwordChanged = updatePasswordInDatabase(username, newPassword);
                if (passwordChanged) {
                    // 返回 true 表示密码修改成功
                    return true;
                } else {
                    // 返回 false 表示密码修改失败
                    return false;
                }
            } else {
                // 返回 false 表示新密码不合法
                return false;
            }
        } else {
            // 返回 false 表示当前密码不正确
            return false;
        }
    }

    // 验证当前密码的方法，需要根据你的用户认证系统进行实现
    private boolean verifyCurrentPassword(String username, String currentPassword) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            // 获取数据库连接
            connection = ConnetMYSQL.connectDB();

            // 准备查询语句，根据用户名和密码查询用户
            String query = "SELECT * FROM customers WHERE username = ? AND password = ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, currentPassword);

            // 执行查询
            resultSet = preparedStatement.executeQuery();

            // 如果查询结果包含一行或更多行，表示验证成功
            if (resultSet.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // 关闭数据库资源
            ConnetMYSQL.closeResultSet(resultSet);
            ConnetMYSQL.closeStatement(preparedStatement);
            ConnetMYSQL.closeConnection(connection);
        }

        // 如果没有验证成功或发生异常，返回 false
        return false;
    }


    // 更新数据库中的密码
    private boolean updatePasswordInDatabase(String username, String newPassword) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            // 获取数据库连接
            connection = ConnetMYSQL.connectDB();

            // 准备更新密码的SQL语句
            String sql = "UPDATE customers SET password = ? WHERE username = ?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, newPassword);
            preparedStatement.setString(2, username);

            // 执行更新
            int rowsUpdated = preparedStatement.executeUpdate();

            // 检查是否成功更新了密码
            if (rowsUpdated > 0) {
                return true; // 更新成功
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // 关闭连接和声明
            ConnetMYSQL.closeStatement(preparedStatement);
            ConnetMYSQL.closeConnection(connection);
        }

        return false; // 更新失败
    }

    // 验证密码格式
    private boolean isPasswordValid(String password) {
        // 密码长度大于8个字符，必须是大小写字母、数字和标点符号的组合
        String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }

    private boolean userExists(String username, String oldPassword) {

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            // 获取数据库连接
            connection = ConnetMYSQL.connectDB();

            // 创建 SQL 查询，根据用户名和旧密码查询用户
            String query = "SELECT * FROM customer WHERE username = ? AND password = ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, oldPassword);

            // 执行查询
            resultSet = preparedStatement.executeQuery();

            // 如果查询到结果，表示用户存在
            if (resultSet.next()) {
                return true;
            } else {
                return false; // 用户不存在或密码不匹配
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // 查询出错，也可以考虑抛出异常
        } finally {
            // 关闭数据库连接、预处理语句和结果集
            ConnetMYSQL.closeResultSet(resultSet);
            ConnetMYSQL.closeStatement(preparedStatement);
            ConnetMYSQL.closeConnection(connection);
        }
    }


    public void setVisible(boolean b) {
        if (b) {
            // 设置窗口可见
            super.setVisible(true);
        } else {
            // 隐藏窗口
            super.setVisible(false);
        }
    }

    public static void main(String[] args) {
        Connection connection = ConnetMYSQL.connectDB();
        CustomersMain customerRootWindow = new CustomersMain(connection);
        new ChangePassword(connection, customerRootWindow); // 将CustomerRootWindow的引用传递给PasswordManage
    }
}
