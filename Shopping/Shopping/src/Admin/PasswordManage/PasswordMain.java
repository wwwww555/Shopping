package Admin.PasswordManage;

import Admin.AdminMain;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PasswordMain extends JFrame {
    private AdminMain adminMain; // 用于返回管理员主界面

    public PasswordMain(AdminMain adminMain) {
        this.adminMain = adminMain;

        setTitle("密码管理界面");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);

        JPanel buttonPanel = new JPanel(new GridLayout(3, 1));

        // 添加管理员密码修改按钮
        JButton adminPasswordButton = new JButton("管理员密码修改");
        adminPasswordButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Change_Admin adminPasswordDialog = new Change_Admin();
                adminPasswordDialog.setVisible(true);
            }
        });

        // 添加用户密码重置按钮
        JButton userPasswordButton = new JButton("用户密码重置");
        userPasswordButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 创建用户密码重置窗口并显示
                Change_Customers userPasswordResetWindow = new Change_Customers();
                 userPasswordResetWindow.setVisible(true);
            }
        });

        // 添加返回按钮
        JButton backButton = new JButton("返回");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 返回管理员主界面
                adminMain.setVisible(true);
                // 关闭当前窗口
                dispose();
            }
        });

        // 将按钮添加到面板
        buttonPanel.add(adminPasswordButton);
        buttonPanel.add(userPasswordButton);
        buttonPanel.add(backButton);

        // 添加面板到窗口
        add(buttonPanel);

        // 显示窗口
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                AdminMain adminMain = new AdminMain();
                new PasswordMain(adminMain);
            }
        });
    }
}
