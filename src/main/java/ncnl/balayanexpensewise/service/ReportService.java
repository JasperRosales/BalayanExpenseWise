package ncnl.balayanexpensewise.service;

import ncnl.balayanexpensewise.beans.Table;
import ncnl.balayanexpensewise.utils.PDFReportGenerator;
import ncnl.balayanexpensewise.beans.Transaction;
import ncnl.balayanexpensewise.config.DatabaseConnector;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportService {

    public List<Transaction> getTransactions(String tablePath) {
        String tableName = Table.getTableName(tablePath);
        List<Transaction> transactions = new ArrayList<>();

        String QUERY = "SELECT id, name, amount, price, category, transaction_date FROM " + tableName;

        try (Connection connection = DatabaseConnector.getUserConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(QUERY);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                // Map each row to a Transaction object
                Transaction transaction = new Transaction(
                        resultSet.getString("name"),
                        resultSet.getDouble("price"),
                        resultSet.getInt("amount"),
                        resultSet.getString("category")
                );

                transaction.setId(resultSet.getInt("id"));
                transaction.setTransactionDate(resultSet.getTimestamp("transaction_date").toLocalDateTime());
                transaction.setTotalPrice(transaction.getPrice() * transaction.getAmount());

                transactions.add(transaction);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return transactions;
    }

    public void generateTransactionReport(String tablePath, String templatePath, String outputPath) {
        List<Transaction> transactions = getTransactions(tablePath);

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("ReportTitle", "Transaction Report");
        parameters.put("GeneratedBy", "BalayanExpenseWise");

        PDFReportGenerator generator = new PDFReportGenerator();
        generator.generateReport(transactions, parameters, templatePath, outputPath);
    }
}
