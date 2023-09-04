package Admin.PasswordManage;

import Login.ConnetMYSQL;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Change_Customers extends JDialog {
    private JTextField userIdField;
    private JTextField usernameField;

    public Change_Customers() {
        setTitle("用户密码重置");
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(300, 200);
        setLocationRelativeTo(null); // 居中显示窗口

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 2, 10, 10));

        // 用户ID输入
        JLabel userIdLabel = new JLabel("用户ID:");
        userIdField = new JTextField(10);
        panel.add(userIdLabel);
        panel.add(userIdField);

        // 用户名输入
        JLabel usernameLabel = new JLabel("用户名:");
        usernameField = new JTextField(10);
        panel.add(usernameLabel);
        panel.add(usernameField);

        // 确认重置按钮
        JButton confirmButton = new JButton("确认重置");
        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int userId = Integer.parseInt(userIdField.getText());
                String username = usernameField.getText();

                // 弹出确认重置的对话框
                int confirm = JOptionPane.showConfirmDialog(null,
                        "确认重置密码？", "确认重置", JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    // 在这里执行密码重置的逻辑
                    String newPassword = resetUserPassword(userId, username);
                    if (newPassword != null) {
                        JOptionPane.showMessageDialog(null, "密码已成功重置为：" + newPassword);
                    } else {
                        JOptionPane.showMessageDialog(null, "密码重置失败，请检查数据库连接或用户信息");
                    }

                    // 关闭当前窗口
                    dispose();
                }
            }

            private String resetUserPassword(int userId, String username) {
                // 将新密码设置为 "000000"
                String newPassword = "000000";

                // 将新密码更新到数据库中
                if (updatePasswordInDatabase(userId, newPassword)) {
                    // 返回新密码
                    return newPassword;
                } else {
                    return null; // 返回 null 表示密码重置失败
                }
            }

            private boolean updatePasswordInDatabase(int userId, String newPassword) {
                // 将新密码更新到数据库的逻辑
                Connection connection = ConnetMYSQL.connectDB();
                try {
                    String updateQuery = "UPDATE customers SET password = ? WHERE customer_id = ?";
                    PreparedStatement preparedStatement = connection.prepareStatement(updateQuery);
                    preparedStatement.setString(1, newPassword);
                    preparedStatement.setInt(2, userId);
                    int rowsAffected = preparedStatement.executeUpdate();

                    // 关闭资源
                    preparedStatement.close();
                    connection.close();

                    return rowsAffected > 0;
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    return false;
                } finally {
                    try {
                        if (connection != null) {
                            connection.close();
                        }
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
            }

        });

        // 返回按钮
        JButton backButton = new JButton("返回");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 关闭当前窗口
                dispose();
            }
        });

        panel.add(confirmButton);
        panel.add(backButton);

        // 将面板添加到对话框
        add(panel, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Change_Customers dialog = new Change_Customers();
                dialog.setVisible(true);
            }
        });
    }
}

