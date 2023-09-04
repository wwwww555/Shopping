package Customers;

import Login.ConnetMYSQL;
import Customers.CustomerManage.*;
import Customers.ShoppingCar.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;

public class CustomersMain extends JFrame {
    private final Connection connection;
    private static CustomersMain customermain;

    public CustomersMain(Connection connection) {
        this.connection = connection;
        setTitle("用户菜单");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);

        JPanel buttonPanel = new JPanel(new GridLayout(3, 1));

        JButton passwordManageButton = new JButton("密码管理");
        JButton shoppingButton = new JButton("购物");
        JButton exitButton = new JButton("退出系统");

        passwordManageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openPasswordManageWindow();
            }
        });

        shoppingButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openShoppingWindow();
            }
        });

        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exitSystem();
            }
        });

        buttonPanel.add(shoppingButton);
        buttonPanel.add(passwordManageButton);
        buttonPanel.add(exitButton);

        add(buttonPanel);
        setVisible(true);
    }

    private void openPasswordManageWindow() {
         ChangePassword passwordManage = new ChangePassword(connection, this);
         passwordManage.setVisible(true);
        this.dispose();
    }

    private void openShoppingWindow() {
         ShoppingCart shoppingCart = new ShoppingCart(connection);
         ShoppingCar_Main shoppingmain = new ShoppingCar_Main(connection, shoppingCart, this);
         shoppingmain.setVisible(true);
        this.dispose();
    }

    private void exitSystem() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.exit(0);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Connection connection = ConnetMYSQL.connectDB();
                customermain= new CustomersMain(connection);
            }
        });
    }
}
