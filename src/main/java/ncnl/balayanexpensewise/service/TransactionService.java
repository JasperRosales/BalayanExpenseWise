package ncnl.balayanexpensewise.service;

import ncnl.balayanexpensewise.beans.Table;
import ncnl.balayanexpensewise.beans.Transaction;
import ncnl.balayanexpensewise.repository.TransactionDAO;
import ncnl.balayanexpensewise.config.DatabaseConnector;
import ncnl.balayanexpensewise.utils.AlarmUtils;

import java.sql.*;


public class TransactionService implements TransactionDAO {


    @Override
    public void addTransaction(String tablePath, Transaction transaction) {
        String tableName = Table.getTableName(tablePath);

        String INSERT_TRANSACTION_QUERY = "INSERT INTO " + tableName + " (name, amount, price, category) VALUES (?, ?, ?, ?)";
        try (Connection connection = DatabaseConnector.getUserConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_TRANSACTION_QUERY)) {

            preparedStatement.setString(1, transaction.getName());
            preparedStatement.setInt(2, transaction.getAmount());
            preparedStatement.setDouble(3, transaction.getPrice());
            preparedStatement.setString(4, transaction.getCategory());

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


}

