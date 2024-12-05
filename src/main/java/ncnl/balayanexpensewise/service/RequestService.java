package ncnl.balayanexpensewise.service;

import ncnl.balayanexpensewise.beans.Admin;
import ncnl.balayanexpensewise.beans.Encryptor;
import ncnl.balayanexpensewise.beans.User;
import ncnl.balayanexpensewise.config.DatabaseConnector;
import ncnl.balayanexpensewise.utils.AlarmUtils;
import ncnl.balayanexpensewise.utils.Validator;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RequestService {

    private final Encryptor encryptor = new Encryptor();


    public boolean addRequest(String requestType, User user, String dualRole) {
        String ADD_REQUEST_QUERY = "INSERT INTO pending_member_requests (request_type, firstName, middleName, lastName, new_password, srcode, dual_role, role, status, department) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnector.getUserConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(ADD_REQUEST_QUERY)) {
             preparedStatement.setString(1, requestType);
             preparedStatement.setString(2, user.getFirstName());
             preparedStatement.setString(3, user.getMiddleName());
             preparedStatement.setString(4, user.getLastName());
             preparedStatement.setString(5, encryptor.createHashPassword(user.getPassword()));
             preparedStatement.setString(6, user.getSrcode());
             preparedStatement.setString(7, dualRole);
             preparedStatement.setString(8, user.getRole());
             preparedStatement.setString(9, "PENDING");
             preparedStatement.setString(10, user.getDepartment());

            return preparedStatement.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public Admin getRequestById(int requestId) {
        String GET_REQUEST_BY_ID_QUERY =
                "SELECT * FROM pending_member_requests WHERE request_id = ?";
        Admin temp = null;

        try (Connection connection = DatabaseConnector.getUserConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_REQUEST_BY_ID_QUERY)) {

            preparedStatement.setInt(1, requestId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    String srcode = resultSet.getString("srcode");
                    String firstName = resultSet.getString("firstName");
                    String middleName = resultSet.getString("middleName");
                    String lastName = resultSet.getString("lastName");
                    String department = resultSet.getString("department");
                    String role = resultSet.getString("role");
                    String gsuite = Validator.generateGSuiteEmail(srcode);
                    String otherRole = resultSet.getString("dual_role");
                    String password = resultSet.getString("new_password");

                    temp = new Admin(srcode, firstName, middleName, lastName, role, gsuite ,department, otherRole, password);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return temp;
    }


    public void deleteRequestById(int requestId) {
        String DELETE_REQUEST_BY_ID_QUERY = "DELETE FROM pending_member_requests WHERE request_id = ?";

        try (Connection connection = DatabaseConnector.getUserConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_REQUEST_BY_ID_QUERY)) {

            preparedStatement.setInt(1, requestId);

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                AlarmUtils.showCustomSuccessAlert("Inbox record deleted successfully.");
            } else {
                AlarmUtils.showErrorAlert("No matching record found for deletion.");
            }
        } catch (SQLException e) {
            System.err.println("Error occurred while deleting request: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
