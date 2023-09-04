package Admin.PasswordManage;

import Login.ConnetMYSQL;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
public class Change_Admin extends JDialog{
    private JTextField adminUsernameField;
    private JPasswordField adminPasswordField;

    public Change_Admin() {
        setTitle("修改管理员密码");
        setSize(300, 200);
        setLocationRelativeTo(null); // 居中显示窗口
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // 创建面板并设置布局
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 2, 10, 10));

        // 创建用户名和密码输入字段
        adminUsernameField = new JTextField(20);
        adminPasswordField = new JPasswordField(20);

        // 创建标签
        JLabel adminUsernameLabel = new JLabel("管理员用户名:");
        JLabel adminPasswordLabel = new JLabel("管理员密码:");

        // 创建确认修改和取消按钮
        JButton confirmButton = new JButton("确认修改");
        JButton cancelButton = new JButton("取消");

        // 添加确认修改按钮的点击事件监听器
        confirmButton.addActionListener(new ActionListener() {
            private boolean updateAdminPassword(String username, String password) {
                // 在这里执行更新管理员密码的逻辑
                Connection connection = null;
                try {
                    // 获取数据库连接
                    connection = ConnetMYSQL.connectDB();
                    if (connection != null) {
                        // 构建 SQL 更新语句
                        String updateQuery = "UPDATE admin SET password = ? WHERE username = ?";
                        try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
                            preparedStatement.setString(1, password);
                            preparedStatement.setString(2, username);

                            // 执行更新操作
                            int rowsAffected = preparedStatement.executeUpdate();

                            // 返回更新结果
                            return rowsAffected > 0;
                        }
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                } finally {
                    try {
                        if (connection != null) {
                            connection.close();
                        }
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
                return false;
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                String adminUsername = adminUsernameField.getText();
                char[] adminPasswordChars = adminPasswordField.getPassword();
                String adminPassword = new String(adminPasswordChars);

                // 显示确认对话框，以便用户可以确认是否要修改管理员密码
                int confirm = JOptionPane.showConfirmDialog(null,
                        "确认修改管理员密码？", "确认修改", JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    if (updateAdminPassword(adminUsername, adminPassword)) {
                        JOptionPane.showMessageDialog(null, "管理员密码已成功修改");
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(null, "修改管理员密码失败，请检查数据库连接或管理员信息");
                    }
                }
            }
        });

        // 添加取消按钮的点击事件监听器
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        // 添加组件到面板中
        panel.add(adminUsernameLabel);
        panel.add(adminUsernameField);
        panel.add(adminPasswordLabel);
        panel.add(adminPasswordField);
        panel.add(confirmButton);
        panel.add(cancelButton);

        // 将面板添加到窗口
        add(panel);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Change_Admin dialog = new Change_Admin();
                dialog.setVisible(true);
            }
        });
    }
}