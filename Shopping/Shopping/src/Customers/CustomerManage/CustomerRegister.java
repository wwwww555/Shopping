package Customers.CustomerManage;

import Login.Window;
import Login.ConnetMYSQL;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CustomerRegister extends JFrame {

    private Window parentWindow;
    private Connection connection;

    public CustomerRegister(Window parentWindow) {
        this.parentWindow = parentWindow;
        connection = ConnetMYSQL.connectDB(); // 调用修正后的方法获取数据库连接

        setTitle("用户注册");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel registerPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 10, 5, 10); // 设置组件之间的间距

        JLabel usernameLabel = new JLabel("用户名:");
        JTextField usernameField = new JTextField(20);
        gbc.gridx = 0;
        gbc.gridy = 0;
        registerPanel.add(usernameLabel, gbc);
        gbc.gridx = 1;
        registerPanel.add(usernameField, gbc);

        JLabel passwordLabel = new JLabel("密码:");
        JPasswordField passwordField = new JPasswordField(20);
        gbc.gridx = 0;
        gbc.gridy = 1;
        registerPanel.add(passwordLabel, gbc);
        gbc.gridx = 1;
        registerPanel.add(passwordField, gbc);

        JLabel phoneLabel = new JLabel("手机号:");
        JTextField phoneField = new JTextField(20);
        gbc.gridx = 0;
        gbc.gridy = 2;
        registerPanel.add(phoneLabel, gbc);
        gbc.gridx = 1;
        registerPanel.add(phoneField, gbc);

        JLabel emailLabel = new JLabel("邮箱:");
        JTextField emailField = new JTextField(20);
        gbc.gridx = 0;
        gbc.gridy = 3;
        registerPanel.add(emailLabel, gbc);
        gbc.gridx = 1;
        registerPanel.add(emailField, gbc);

        JButton registerButton = new JButton("注册");
        JButton backButton = new JButton("返回");

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                String phone = phoneField.getText();
                String email = emailField.getText();

                if (performRegistration(username, password, phone, email)) {
                    JOptionPane.showMessageDialog(CustomerRegister.this,
                            "注册成功！", "注册结果", JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                    // 返回登录窗口
                    parentWindow.setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(CustomerRegister.this,
                            "注册失败，请重试！", "注册结果", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                parentWindow.setVisible(true);
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(backButton);
        buttonPanel.add(registerButton);
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        registerPanel.add(buttonPanel, gbc);

        add(registerPanel);

        setVisible(true);
    }


    private boolean performRegistration(String username, String password, String phone, String email) {
        String sql = "INSERT INTO customers (username, password, phone, email) VALUES (?, ?, ?, ?)";
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, username);
            statement.setString(2, password);
            statement.setString(3, phone);
            statement.setString(4, email);
            int rowsInserted = statement.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void main(String[] args) {
        // 创建一个新的窗口对象
        Window window = new Window();

        // 在事件调度线程中运行窗口对象
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                // 创建并显示用户注册窗口
                new CustomerRegister(window);
            }
        });
    }
}
