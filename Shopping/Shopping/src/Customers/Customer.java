package Customers;

import java.util.Date;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Customer {
    private int customerId;
    private String username;
    private String membershipLevel;
    private Date registrationDate;
    private double totalSpent;
    private String phoneNumber;
    private String email;

    // 添加数据库连接作为构造函数参数
    public Customer(int customerId, String username, String membershipLevel, Date registrationDate,
                    double totalSpent, String phoneNumber, String email, Connection connection) {
        this.customerId = customerId;
        this.username = username;
        this.membershipLevel = membershipLevel;
        this.registrationDate = registrationDate;
        this.totalSpent = totalSpent;
        this.phoneNumber = phoneNumber;
        this.email = email;

        // 在构造函数中初始化客户信息（如果需要的话）
        if (connection != null) {
            initializeCustomerInfo(connection);
        }
    }

    // 连接数据库并初始化客户信息
    private void initializeCustomerInfo(Connection connection) {
        String sql = "SELECT * FROM customers WHERE customer_id = ?";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, customerId);
            ResultSet resultSet = stmt.executeQuery();

            if (resultSet.next()) {
                // 从查询结果中获取客户信息
                this.username = resultSet.getString("username");
                this.membershipLevel = resultSet.getString("membership_level");
                this.registrationDate = resultSet.getDate("registration_date");
                this.totalSpent = resultSet.getDouble("total_spent");
                this.phoneNumber = resultSet.getString("phone");
                this.email = resultSet.getString("email");
            }

            resultSet.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 提供访问器（getter）和修改器（setter）方法以获取和设置客户信息的各个属性
    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMembershipLevel() {
        return membershipLevel;
    }

    public void setMembershipLevel(String membershipLevel) {
        this.membershipLevel = membershipLevel;
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }

    public double getTotalSpent() {
        return totalSpent;
    }

    public void setTotalSpent(double totalSpent) {
        this.totalSpent = totalSpent;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "customerId=" + customerId +
                ", username='" + username + '\'' +
                ", membershipLevel='" + membershipLevel + '\'' +
                ", registrationDate=" + registrationDate +
                ", totalSpent=" + totalSpent +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
