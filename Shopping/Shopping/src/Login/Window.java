package Login;

import Admin.AdminMain;
import Customers.*;
import Customers.CustomerManage.*;
import Customers.CustomerManage.ForgetPassword;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Window extends JFrame {
    private int loginAttempts = 0;
    private final int maxLoginAttempts = 5;
    private JTextField usernameField;
    private JPasswordField passwordField;

    public Window() {
        setTitle("登录窗口");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(350, 200);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 2)); // 修改为4行2列

        JLabel usernameLabel = new JLabel("用户名:");
        usernameField = new JTextField();
        JLabel passwordLabel = new JLabel("密码:");
        passwordField = new JPasswordField();

        JButton adminLoginButton = new JButton("管理员登录");
        adminLoginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                if (validateAdmin(username, password)) {
                    // 创建并显示 AdminMain 窗口
                    AdminMain adminMainWindow = new AdminMain();
                    adminMainWindow.setVisible(true);

                    // 关闭当前窗口
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(null, "登录失败，请检查用户名和密码");
                }
            }
        });

        JButton userLoginButton = new JButton("用户登录");
        userLoginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                if (CustomersAdmin(username, password)) {
                    // 创建并显示 CustomersMain 窗口
                    openCustomerRootWindow();
                } else {
                    loginAttempts++;
                    if (loginAttempts >= maxLoginAttempts) {
                        JOptionPane.showMessageDialog(null, "登录尝试次数过多，账户已锁定");
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(null, "登录失败，请重试");
                    }
                }
            }

        });

        JButton registerButton = new JButton("注册");
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 创建并显示用户注册窗口
                CustomerRegister customerRegisterWindow = new CustomerRegister(Window.this);  // 将 LoginWindow 对象作为参数传递给 CustomerRegister 的构造函数
                customerRegisterWindow.setVisible(true);

                // 关闭当前窗口
                dispose();
            }
        });

        JButton forgotPasswordButton = new JButton("忘记密码");
        forgotPasswordButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                ForgetPassword forgotpassword = new ForgetPassword(Window.this);  // 将 LoginWindow 对象作为参数传递给 ForgotPassword 的构造函数
                forgotpassword.setVisible(true);

                // 关闭当前窗口
                dispose();
            }
        });

        panel.add(usernameLabel);
        panel.add(usernameField);
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(adminLoginButton);
        panel.add(userLoginButton);
        panel.add(registerButton);
        panel.add(forgotPasswordButton);

        add(panel);

        setVisible(true);
    }

    private boolean validateAdmin(String username, String password) {
        Connection conn = ConnetMYSQL.connectDB();
        if (conn != null) {
            String query = "SELECT * FROM admin WHERE username = ? AND password = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, username);
                stmt.setString(2, password);
                ResultSet rs = stmt.executeQuery();
                return rs.next();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private boolean CustomersAdmin(String username, String password) {
        Connection conn = ConnetMYSQL.connectDB();
        if (conn != null) {
            String query = "SELECT * FROM customers WHERE username = ? AND password = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, username);
                stmt.setString(2, password);
                ResultSet rs = stmt.executeQuery();
                return rs.next();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private boolean checkLogin(String username, String password) {
        Connection conn = ConnetMYSQL.connectDB();
        if (conn != null) {
            String query = "SELECT * FROM customers WHERE username = ? AND password = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, username);
                stmt.setString(2, password);
                ResultSet rs = stmt.executeQuery();
                return rs.next();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private void openCustomerRootWindow() {
        Connection connection = ConnetMYSQL.connectDB();

        if (connection != null) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    CustomersMain customerRootWindow = new CustomersMain(connection);
                    customerRootWindow.setVisible(true);
                    dispose();
                }
            });
        } else {
            JOptionPane.showMessageDialog(null, "无法连接到数据库，请检查数据库连接");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Window();
            }
        });
    }
}

