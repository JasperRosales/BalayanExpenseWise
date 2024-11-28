package ncnl.balayanexpensewise.service;

import ncnl.balayanexpensewise.beans.LabelRow;
import ncnl.balayanexpensewise.beans.TransactionLogger;
import ncnl.balayanexpensewise.config.DatabaseConnector;
import ncnl.balayanexpensewise.repository.LoggerDAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LoggerService implements LoggerDAO {

    @Override
    public void logTransaction(String tableName, TransactionLogger transactionLogger) {
        final String INSERT_TRANSACTION_QUERY_TEMPLATE = "INSERT INTO %s(name, amount, price, budget) VALUES (?, ?, ?, ?)";

        String query = String.format(INSERT_TRANSACTION_QUERY_TEMPLATE, tableName);
        try (Connection connection = DatabaseConnector.getUserConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, transactionLogger.getDescription());
            preparedStatement.setInt(2, transactionLogger.getAmount());
            preparedStatement.setDouble(3, transactionLogger.getPrice());
            preparedStatement.setDouble(4, transactionLogger.getBudget());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @Override
    public List<TransactionLogger> getTransactionLogs(String tableName) {
        final String GET_LAST_5_TRANSACTION_LOGS_QUERY_TEMPLATE = "SELECT name, amount, price, category, total_price FROM %s ORDER BY transaction_date DESC LIMIT 5";

        List<TransactionLogger> transactionLogs = new ArrayList<>();
        String query = String.format(GET_LAST_5_TRANSACTION_LOGS_QUERY_TEMPLATE, tableName);
        try (Connection connection = DatabaseConnector.getUserConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                TransactionLogger transactionLogger = new TransactionLogger();
                transactionLogger.setDescription(resultSet.getString("name"));
                transactionLogger.setAmount(resultSet.getInt("amount"));
                transactionLogger.setPrice(resultSet.getDouble("price"));
                transactionLogger.setCategory(resultSet.getString("category"));
                transactionLogger.setBudget(resultSet.getDouble("total_price"));

                transactionLogs.add(transactionLogger);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transactionLogs;
    }

}
