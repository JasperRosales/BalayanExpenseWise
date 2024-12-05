package ncnl.balayanexpensewise.service;

import ncnl.balayanexpensewise.beans.LabelRow;
import ncnl.balayanexpensewise.beans.TransactionLogger;
import ncnl.balayanexpensewise.config.DatabaseConnector;
import ncnl.balayanexpensewise.repository.LoggerDAO;
import ncnl.balayanexpensewise.utils.AlarmUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LoggerService implements LoggerDAO {




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


    public int countTransactionInboxRecords() {
        String COUNT_QUERY = "SELECT COUNT(*) FROM admin_inbox";
        int count = 0;

        try (Connection connection = DatabaseConnector.getUserConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(COUNT_QUERY);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            if (resultSet.next()) {
                count = resultSet.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            AlarmUtils.showErrorAlert("Error counting records: " + e.getMessage());
        }

        return count;
    }
}
