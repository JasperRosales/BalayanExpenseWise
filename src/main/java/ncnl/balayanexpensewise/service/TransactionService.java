package ncnl.balayanexpensewise.service;

import ncnl.balayanexpensewise.beans.Table;
import ncnl.balayanexpensewise.beans.Transaction;
import ncnl.balayanexpensewise.repository.TransactionDAO;
import ncnl.balayanexpensewise.config.DatabaseConnector;
import ncnl.balayanexpensewise.utils.AlarmUtils;

import java.sql.*;


public class TransactionService implements TransactionDAO {

    private static final String UPDATE_TRANSACTION_QUERY = "UPDATE transactions SET name = ?, amount = ?, price = ?, total_price = ?, category = ? WHERE id = ?";
    private static final String DELETE_TRANSACTION_QUERY = "DELETE FROM transactions WHERE id = ?";


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
                AlarmUtils.showSuccessAlert("Transaction added successfully.");
            } else {
                AlarmUtils.showErrorAlert("Transaction failed!");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateTransaction(Transaction transaction) {
        try (Connection connection = DatabaseConnector.getUserConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_TRANSACTION_QUERY)) {

            preparedStatement.setString(1, transaction.getName());
            preparedStatement.setInt(2, transaction.getAmount());
            preparedStatement.setDouble(3, transaction.getPrice());
            preparedStatement.setDouble(4, transaction.getTotalPrice());
            preparedStatement.setString(5, transaction.getCategory());
            preparedStatement.setInt(6, transaction.getId());

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Transaction updated successfully.");
            } else {
                System.out.println("Failed to update transaction.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteTransaction(int transactionId) {
        try (Connection connection = DatabaseConnector.getUserConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_TRANSACTION_QUERY)) {

            preparedStatement.setInt(1, transactionId);

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Transaction deleted successfully.");
            } else {
                System.out.println("Failed to delete transaction.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}

