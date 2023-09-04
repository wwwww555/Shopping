package Customers.CustomerManage;

import Login.Window;
import Login.ConnetMYSQL;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ForgetPassword extends JFrame {
    private Window parentWindow;

    private Connection connection;
    private JTextField usernameField;
    private JTextField emailField;

    public ForgetPassword(Window parentWindow) {
        this.parentWindow = parentWindow;
        connection = ConnetMYSQL.connectDB(); // 调用修正后的方法获取数据库连接

        setTitle("忘记密码重置");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 150);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 2));

        JLabel usernameLabel = new JLabel("用户名：");
        usernameField = new JTextField();
        JLabel emailLabel = new JLabel("注册邮箱：");
        emailField = new JTextField();

        JButton resetButton = new JButton("重置密码");
        resetButton.addActionListener(new ResetButtonListener());

        JButton backButton = new JButton("返回");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                parentWindow.setVisible(true);
            }
        });

        panel.add(usernameLabel);
        panel.add(usernameField);
        panel.add(emailLabel);
        panel.add(emailField);
        panel.add(resetButton);
        panel.add(backButton);

        add(panel);
        setVisible(true);
    }

    private class ResetButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String username = usernameField.getText();
            String email = emailField.getText();

            // 连接数据库
            Connection connection = null;
            PreparedStatement statement = null;
            ResultSet resultSet = null;
            try {
                connection = ConnetMYSQL.connectDB();

                // 查询数据库中的用户名和邮箱是否匹配
                String sql = "SELECT * FROM customers WHERE username = ? AND email = ?";
                statement = connection.prepareStatement(sql);
                statement.setString(1, username);
                statement.setString(2, email);
                resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    // 匹配成功，生成新密码并保存到数据库
                    String randomPassword = generateRandomPassword();
                    sql = "UPDATE customers SET password = ? WHERE username = ?";
                    statement = connection.prepareStatement(sql);
                    statement.setString(1, randomPassword);
                    statement.setString(2, username);
                    statement.executeUpdate();

                    // 发送密码重置邮件
                    sendResetPasswordEmail(email, randomPassword);

                    // 提示用户密码已重置
                    JOptionPane.showMessageDialog(ForgetPassword.this, "密码重置成功，请检查您的邮箱。您可以使用新密码登录。");
                } else {
                    // 匹配失败
                    JOptionPane.showMessageDialog(ForgetPassword.this, "用户名和邮箱不匹配，请重新输入。");
                }

                // 清空输入框内容
                usernameField.setText("");
                emailField.setText("");
            } catch (SQLException ex) {
                ex.printStackTrace();
            } finally {
                ConnetMYSQL.closeResultSet(resultSet);
                ConnetMYSQL.closeStatement(statement);
                ConnetMYSQL.closeConnection(connection);
            }
        }
    }

    // 生成随机密码的函数，仅用于示例，可以根据实际需求进行修改
    private String generateRandomPassword() {
        String characters = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder randomPassword = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            int index = (int) (Math.random() * characters.length());
            randomPassword.append(characters.charAt(index));
        }
        return randomPassword.toString();
    }

    // 发送密码重置邮件的函数，仅用于示例，可以根据实际需求进行修改
    private void sendResetPasswordEmail(String email, String password) {
        String subject = "密码重置";
        String content = "您的新密码是：" + password;

        // 发送邮件
        System.out.println("邮件已发送到您的邮箱：" + email);
        System.out.println("新密码：" + password);
    }

    public static void main(String[] args) {
        // 创建一个新的窗口对象
        Window window = new Window();
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new ForgetPassword(window);
            }
        });
    }
}
