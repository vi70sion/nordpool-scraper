package org.example.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class NordPoolPriceRepository {

    public void insertData(Connection connection, String date, String area, String price) {

        String sql = "INSERT INTO daily_prices (date, area, price) VALUES (?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, date);
            preparedStatement.setString(2, area);
            preparedStatement.setString(3, price);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Klaida įrašant duomenis. " + e.getMessage());
        }

    }


}
