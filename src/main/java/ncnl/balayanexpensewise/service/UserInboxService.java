package ncnl.balayanexpensewise.service;

import ncnl.balayanexpensewise.beans.UserInbox;
import ncnl.balayanexpensewise.config.DatabaseConnector;
import ncnl.balayanexpensewise.utils.AlarmUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserInboxService {

    public void addUserInboxRecord(UserInbox userInbox) {
        String INSERT_QUERY = "INSERT INTO user_inbox (name, role, purpose, department) VALUES (?, ?, ?, ?)";

        try (Connection connection = DatabaseConnector.getUserConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_QUERY)) {

            preparedStatement.setString(1, userInbox.getName());
            preparedStatement.setString(2, userInbox.getRole());
            preparedStatement.setString(3, userInbox.getPurpose());
            preparedStatement.setString(4, userInbox.getDepartment());

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                AlarmUtils.showCustomSuccessAlert("User Inbox record added successfully.");
            } else {
                AlarmUtils.showErrorAlert("Failed to add User Inbox record.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            AlarmUtils.showErrorAlert("Error: " + e.getMessage());
        }
    }

    public List<UserInbox> getAllUserInboxRecords() {
        String SELECT_QUERY = "SELECT id, name, role, purpose, department FROM user_inbox";
        List<UserInbox> inboxList = new ArrayList<>();

        try (Connection connection = DatabaseConnector.getUserConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_QUERY);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                UserInbox userInbox = new UserInbox();
                userInbox.setId(resultSet.getInt("id"));
                userInbox.setName(resultSet.getString("name"));
                userInbox.setRole(resultSet.getString("role"));
                userInbox.setPurpose(resultSet.getString("purpose"));
                userInbox.setDepartment(resultSet.getString("department"));

                inboxList.add(userInbox);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            AlarmUtils.showErrorAlert("Error fetching records: " + e.getMessage());
        }

        return inboxList;
    }

    public void updateUserInboxRecord(int id, String newPurpose) {
        String UPDATE_QUERY = "UPDATE user_inbox SET purpose = ? WHERE id = ?";

        try (Connection connection = DatabaseConnector.getUserConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_QUERY)) {

            preparedStatement.setString(1, newPurpose);
            preparedStatement.setInt(2, id);

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                AlarmUtils.showCustomSuccessAlert("User Inbox record updated successfully.");
            } else {
                AlarmUtils.showErrorAlert("No matching record found for update.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            AlarmUtils.showErrorAlert("Error updating record: " + e.getMessage());
        }
    }

    public void deleteUserInboxRecord(int id) {
        String DELETE_QUERY = "DELETE FROM user_inbox WHERE id = ?";

        try (Connection connection = DatabaseConnector.getUserConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_QUERY)) {

            preparedStatement.setInt(1, id);

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                AlarmUtils.showCustomSuccessAlert("User Inbox record deleted successfully.");
            } else {
                AlarmUtils.showErrorAlert("No matching record found for deletion.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            AlarmUtils.showErrorAlert("Error deleting record: " + e.getMessage());
        }
    }

    public int countUserInboxRecords() {
        String COUNT_QUERY = "SELECT COUNT(*) FROM user_inbox";
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
