package ncnl.balayanexpensewise.utils;

import ncnl.balayanexpensewise.config.DatabaseConnector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class DisplayUtil {

    /**
     * Calculate the budget dynamically based on the given table name.
     *
     * @param tableName the name of the table (e.g., "ssc_transactions", "cics_transactions", or "cet_transactions")
     * @return the calculated budget formatted to two decimal places (e.g., ₱1000.00)
     */
    public static String getBudget(String tableName) {
        String SERVICE_QUERY = "SELECT SUM(total_price) AS totalService FROM " + tableName + " WHERE category = 'Service'";
        String EXPENSE_QUERY = "SELECT SUM(total_price) AS totalExpense FROM " + tableName + " WHERE category = 'Expense'";
        double totalBudget = 0.0;

        try (Connection connection = DatabaseConnector.getUserConnection();
             PreparedStatement serviceStatement = connection.prepareStatement(SERVICE_QUERY);
             PreparedStatement expenseStatement = connection.prepareStatement(EXPENSE_QUERY)) {

            // Retrieve the total amount for "Service"
            try (ResultSet serviceResult = serviceStatement.executeQuery()) {
                if (serviceResult.next()) {
                    totalBudget += serviceResult.getDouble("totalService");
                }
            }

//            // Retrieve the total amount for "Expense"
            try (ResultSet expenseResult = expenseStatement.executeQuery()) {
                if (expenseResult.next()) {
                    totalBudget -= expenseResult.getDouble("totalExpense");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error calculating budget for table: " + tableName, e);
        }

        NumberFormat formatter = NumberFormat.getNumberInstance();
        formatter.setMinimumFractionDigits(2);
        formatter.setMaximumFractionDigits(2);
        return formatter.format(totalBudget);
    }


    /**
     * Calculate the total service amount dynamically based on the given table name and month.
     *
     * @param tableName the name of the table (e.g., "ssc_transactions", "cics_transactions", or "cet_transactions")
     * @param month the name of the month (e.g., "January", "February", etc.)
     * @return the calculated total service amount formatted to two decimal places (e.g., ₱1000.00)
     */
    public static String getTotalServiceByMonth(String tableName, String month) {
        // Convert the month name to its numeric value (e.g., January -> 1)
        int monthNumber = convertMonthToInt(month);

        // SQL query to calculate total service for the given month
        String SERVICE_QUERY = "SELECT SUM(total_price) AS totalService FROM " + tableName +
                " WHERE category = 'Service' AND MONTH(transaction_date) = ?";
        double totalService = 0.0;

        try (Connection connection = DatabaseConnector.getUserConnection();
             PreparedStatement serviceStatement = connection.prepareStatement(SERVICE_QUERY)) {

            // Set the month parameter in the query
            serviceStatement.setInt(1, monthNumber);

            // Retrieve the total amount for "Service"
            try (ResultSet serviceResult = serviceStatement.executeQuery()) {
                if (serviceResult.next()) {
                    totalService = serviceResult.getDouble("totalService");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error calculating total service for table: " + tableName, e);
        }

        return String.format("%.2f", totalService);
    }

    /**
     * Calculate the total expense amount dynamically based on the given table name and month.
     *
     * @param tableName the name of the table (e.g., "ssc_transactions", "cics_transactions", or "cet_transactions")
     * @param month the name of the month (e.g., "January", "February", etc.)
     * @return the calculated total expense amount formatted to two decimal places (e.g., ₱1000.00)
     */
    public static String getTotalExpenseByMonth(String tableName, String month) {
        int monthNumber = convertMonthToInt(month);

        String EXPENSE_QUERY = "SELECT SUM(total_price) AS totalExpense FROM " + tableName +
                " WHERE category = 'Expense' AND MONTH(transaction_date) = ?";
        double totalExpense = 0.0;

        try (Connection connection = DatabaseConnector.getUserConnection();
             PreparedStatement expenseStatement = connection.prepareStatement(EXPENSE_QUERY)) {

            expenseStatement.setInt(1, monthNumber);

            try (ResultSet expenseResult = expenseStatement.executeQuery()) {
                if (expenseResult.next()) {
                    totalExpense = expenseResult.getDouble("totalExpense");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error calculating total expense for table: " + tableName, e);
        }

        return String.format("%.2f", totalExpense);
    }


    public static List<Integer> getYearlyExpensesFromDatabase(String tableName) {
        String QUERY = """
        SELECT MONTH(transaction_date) AS month, SUM(total_price) AS totalExpense FROM %s WHERE category = 'Expense' 
        AND YEAR(transaction_date) = YEAR(CURRENT_DATE) GROUP BY MONTH(transaction_date) ORDER BY MONTH(transaction_date) 
        """.formatted(tableName);

        List<Integer> yearlyExpenses = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            yearlyExpenses.add(0);
        }

        try (Connection connection = DatabaseConnector.getUserConnection();
             PreparedStatement statement = connection.prepareStatement(QUERY)) {

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int month = resultSet.getInt("month");
                    int totalExpense = resultSet.getInt("totalExpense");

                    yearlyExpenses.set(month - 1, totalExpense);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error fetching yearly expenses for table: " + tableName, e);
        }

        return yearlyExpenses;
    }

    public static List<Integer> getYearlyServicesFromDatabase(String tableName) {
        String QUERY = """
        SELECT MONTH(transaction_date) AS month, 
               SUM(total_price) AS totalService
        FROM %s
        WHERE category = 'Service' 
          AND YEAR(transaction_date) = YEAR(CURRENT_DATE)
        GROUP BY MONTH(transaction_date)
        ORDER BY MONTH(transaction_date)
        """.formatted(tableName);

        List<Integer> yearlyServices = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            yearlyServices.add(0); // Initialize with zero for all months
        }

        try (Connection connection = DatabaseConnector.getUserConnection();
             PreparedStatement statement = connection.prepareStatement(QUERY)) {

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int month = resultSet.getInt("month");
                    int totalService = resultSet.getInt("totalService");

                    yearlyServices.set(month - 1, totalService);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error fetching yearly services for table: " + tableName, e);
        }

        return yearlyServices;
    }



    /**
     * Convert the month name to its corresponding numeric value.
     *
     * @param month the name of the month (e.g., "January", "February", etc.)
     * @return the numeric value of the month (e.g., 1 for January, 2 for February, etc.)
     */
    private static int convertMonthToInt(String month) {
        switch (month) {
            case "January": return 1;
            case "February": return 2;
            case "March": return 3;
            case "April": return 4;
            case "May": return 5;
            case "June": return 6;
            case "July": return 7;
            case "August": return 8;
            case "September": return 9;
            case "October": return 10;
            case "November": return 11;
            case "December": return 12;
            default: throw new IllegalArgumentException("Invalid month name: " + month);
        }
    }

}
