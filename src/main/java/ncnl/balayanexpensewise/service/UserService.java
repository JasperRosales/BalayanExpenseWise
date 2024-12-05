package ncnl.balayanexpensewise.service;

import ncnl.balayanexpensewise.beans.Encryptor;
import ncnl.balayanexpensewise.beans.User;
import ncnl.balayanexpensewise.config.DatabaseConnector;
import ncnl.balayanexpensewise.utils.AlarmUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserService{

    Encryptor encryptor = new Encryptor();

    public User validateUser(String srcode, String password) {
        User user = null;
        String authUser = "SELECT * FROM users WHERE srcode = ?";

        try (Connection conn = DatabaseConnector.getUserConnection()){
            PreparedStatement psmt = conn.prepareStatement(authUser);

            psmt.setString(1, srcode);
            ResultSet rs = psmt.executeQuery();

            if(rs.next()){
                String storedHash =rs.getString("password");

                if(storedHash.equals(encryptor.createHashPassword(password))){
                    user = new User(
                            rs.getString("srcode"),
                            rs.getString( "firstName"),
                            rs.getString("middleName"),
                            rs.getString("lastName"),
                            rs.getString("department"),
                            rs.getString("role"),
                            rs.getString("gsuite"),
                            rs.getString("password")
                    );
                } else{
                    System.out.println("[ERROR] Invalid Password");
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return user;
    }




    public List<String> getAssignedRolesByDepartment(String department) {
        List<String> assignedRoles = new ArrayList<>();

        String table = ("SSC".equals(department)) ? "admins" : "users";
        String role = ("SSC". equals(department)) ? "admin_role" : "role";
        String GET_ROLES_DISTINCT_QUERY = "SELECT " + role + " FROM " + table + "  WHERE department = ? and firstName = ?";

        try (Connection conn = DatabaseConnector.getUserConnection()) {
            PreparedStatement psmt = conn.prepareStatement(GET_ROLES_DISTINCT_QUERY);
            psmt.setString(1, department);
            psmt.setString(2, "firstName");
            ResultSet rs = psmt.executeQuery();

            while (rs.next()) {
                assignedRoles.add(rs.getString(role));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return assignedRoles;
    }

    public User findByFirstLastName(String firstName, String lastName) {
        User user = null;
        String authUser = "SELECT * FROM users WHERE firstName = ? and lastName = ?";

        try (Connection conn = DatabaseConnector.getUserConnection()){
            PreparedStatement psmt = conn.prepareStatement(authUser);

            psmt.setString(1, firstName);
            psmt.setString(2, lastName);
            ResultSet rs = psmt.executeQuery();

            if(rs.next()){
                user = new User(
                        rs.getString("srcode"),
                        rs.getString( "firstName"),
                        rs.getString("middleName"),
                        rs.getString("lastName"),
                        rs.getString("department"),
                        rs.getString("role"),
                        rs.getString("gsuite"),
                        rs.getString("password")
                );
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return user;

    }

    public Integer getIdByRoleAndDepartment(String role, String department) {
        String GET_ID_QUERY = "SELECT table_id FROM users WHERE role = ? AND department = ?";
        Integer tableId = null;

        try (Connection connection = DatabaseConnector.getUserConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_ID_QUERY)) {

            preparedStatement.setString(1, role);
            preparedStatement.setString(2, department);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    tableId = resultSet.getInt("table_id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return tableId;
    }


    public void updateUserById(int tableId, User updatedUser) {
        String UPDATE_USER_QUERY = "UPDATE users SET srcode = ?, firstName = ?, middleName = ?, lastName = ?, gsuite = ?, password = ? WHERE table_id = ?";

        try (Connection conn = DatabaseConnector.getUserConnection();
             PreparedStatement psmt = conn.prepareStatement(UPDATE_USER_QUERY)) {

            psmt.setString(1, updatedUser.getSrcode());
            psmt.setString(2, updatedUser.getFirstName());
            psmt.setString(3, updatedUser.getMiddleName());
            psmt.setString(4, updatedUser.getLastName());
            psmt.setString(5, updatedUser.getGsuite());
            psmt.setString(6, updatedUser.getPassword());

            psmt.setInt(7, tableId);

            int rowsAffected = psmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("User updated successfully.");
            } else {
                System.out.println("No records updated.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteUser(Integer tableId) {
        String DELETE_USER_QUERY = "DELETE FROM users WHERE table_id =?";

        try (Connection conn = DatabaseConnector.getUserConnection()) {
            PreparedStatement psmt = conn.prepareStatement(DELETE_USER_QUERY);
            psmt.setInt(1, tableId);
            psmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



}
