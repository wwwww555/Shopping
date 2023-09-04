package Admin;

import Admin.PasswordManage.PasswordMain;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
public class AdminMain extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private List<Window> openWindows = new ArrayList<>(); // 用于存储已打开的窗口

    public AdminMain() {
        setTitle("管理员主界面");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);

        JPanel buttonPanel = new JPanel(new GridLayout(4, 4));

        // 添加退出系统按钮
        JButton exitButton = new JButton("退出系统");
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 关闭所有已打开的窗口
                closeAllWindows();
                // 退出应用程序
                System.exit(0);
            }
        });

        // 添加商品管理按钮
        JButton productManagementButton = new JButton("商品管理");
        productManagementButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 创建商品管理窗口并显示
                ProductManage productManagementWindow = new ProductManage();
                productManagementWindow.setVisible(true);

                // 关闭当前窗口
                closeCurrentWindow();
                // 将新窗口添加到已打开窗口列表
                openWindows.add(productManagementWindow);
            }
        });

        JButton userManagementButton = new JButton("用户管理");
        userManagementButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 创建用户管理窗口并显示
                CustomerManage userManagementWindow = new CustomerManage();
                userManagementWindow.setVisible(true);

                // 关闭当前窗口
                closeCurrentWindow();
                // 将新窗口添加到已打开窗口列表
                openWindows.add(userManagementWindow);
            }
        });

        // 添加密码管理按钮
        JButton passwordManagementButton = new JButton("密码管理");
        passwordManagementButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 创建密码管理界面并显示
                PasswordMain passwordManagementWindow = new PasswordMain(AdminMain.this);
                passwordManagementWindow.setVisible(true);

                // 关闭当前窗口
                dispose();
            }
        });

        // 将按钮添加到面板
        buttonPanel.add(productManagementButton);
        buttonPanel.add(userManagementButton);
        buttonPanel.add(passwordManagementButton);
        buttonPanel.add(exitButton);

        // 添加面板到窗口
        add(buttonPanel);

        // 显示窗口
        setVisible(true);
    }

    // 关闭当前窗口
    private void closeCurrentWindow() {
        this.dispose();
    }

    // 在 AdminMain 类中添加关闭所有窗口的方法
    private void closeAllWindows() {
        for (Window window : openWindows) {
            window.dispose();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new AdminMain();
            }
        });
    }
}

