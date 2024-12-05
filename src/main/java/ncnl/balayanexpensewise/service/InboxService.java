package ncnl.balayanexpensewise.service;

import ncnl.balayanexpensewise.beans.Inbox;
import ncnl.balayanexpensewise.beans.Table;
import ncnl.balayanexpensewise.config.DatabaseConnector;
import ncnl.balayanexpensewise.repository.InboxDA0;
import ncnl.balayanexpensewise.utils.AlarmUtils;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class InboxService implements InboxDA0 {

    @Override
    public void addInboxRecord(Inbox inbox) {

        String INSERT_TRANSACTION_QUERY_RECORD = "INSERT INTO admin_inbox (description, department, category, amount, price) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = DatabaseConnector.getUserConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_TRANSACTION_QUERY_RECORD)) {

            preparedStatement.setString(1, inbox.getDescription());
            preparedStatement.setString(2, inbox.getDepartment());
            preparedStatement.setString(3, inbox.getCategory());
            preparedStatement.setInt(4, inbox.getAmount());
            preparedStatement.setDouble(5, inbox.getPrice());

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                AlarmUtils.showCustomSuccessAlert("Transaction added successfully.");
            } else {
                AlarmUtils.showErrorAlert("Transaction failed!");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public List<Inbox> getAllInboxRecords() {
        String SELECT_QUERY = "SELECT id, description, department, category, amount, price, total_price, timestamp, remarks FROM admin_inbox";
        List<Inbox> inboxList = new ArrayList<>();

        try (Connection connection = DatabaseConnector.getUserConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_QUERY);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                Inbox inbox = new Inbox();
                inbox.setId(resultSet.getInt("id"));
                inbox.setDescription(resultSet.getString("description"));
                inbox.setDepartment(resultSet.getString("department"));
                inbox.setCategory(resultSet.getString("category"));
                inbox.setAmount(resultSet.getInt("amount"));
                inbox.setPrice(resultSet.getDouble("price"));
                inbox.setTotalPrice(resultSet.getDouble("total_price"));
                inbox.setRemarks(resultSet.getString("remarks"));

                Timestamp dbTimestamp = resultSet.getTimestamp("timestamp");
                if (dbTimestamp != null) {
                    LocalDateTime timestamp = dbTimestamp.toLocalDateTime();
                    inbox.setTimestamp(timestamp);
                }

                inboxList.add(inbox);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return inboxList;
    }



    @Override
    public void deleteInboxRecord(int id) {
        String DELETE_TRANSACTION_QUERY = "DELETE FROM admin_inbox WHERE id = ?";

        try (Connection connection = DatabaseConnector.getUserConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_TRANSACTION_QUERY)) {

            preparedStatement.setInt(1, id);

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                AlarmUtils.showCustomSuccessAlert("Transaction deleted successfully.");
            } else {
                AlarmUtils.showErrorAlert("No matching transaction found!");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            AlarmUtils.showErrorAlert("An error occurred: " + e.getMessage());
        }
    }
}
