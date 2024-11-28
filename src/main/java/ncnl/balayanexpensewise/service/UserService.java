package ncnl.balayanexpensewise.service;

import eu.hansolo.tilesfx.Alarm;
import ncnl.balayanexpensewise.beans.Encryptor;
import ncnl.balayanexpensewise.beans.User;
import ncnl.balayanexpensewise.config.DatabaseConnector;
import ncnl.balayanexpensewise.repository.UserDAO;
import ncnl.balayanexpensewise.utils.AlarmUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class UserService implements UserDAO {

    private final Encryptor encryptor = new Encryptor();

    @Override
    public void createUser(User user) {
        String creationQuery = "INSERT INTO user(srcode, firstName, middleName, lastName, department, role, gsuite, password) values (?,?,?,?,?,?,?,?)";

        try(Connection conn = DatabaseConnector.getUserConnection()){
            PreparedStatement psmt = conn.prepareStatement(creationQuery);

            psmt.setString(1, user.getSrcode());
            psmt.setString(2, user.getFirstName());
            psmt.setString(3, user.getMiddleName());
            psmt.setString(4, user.getLastName());
            psmt.setString(5, user.getDepartment());
            psmt.setString(6, user.getRole());
            psmt.setString(7, user.getGsuite());
            psmt.setString(8, encryptor.createHashPassword(user.getPassword()));

            psmt.executeUpdate();

        } catch (SQLException e) {
            AlarmUtils.showErrorAlert("Error on creating user");
            e.printStackTrace();
        }
    }



    @Override
    public void deleteUser(String id) {

    }

    @Override
    public void updateUser(User userOptional) {

    }

    @Override
    public User validateUser(String srcode, String password) {
        User user = null;
        String authUser = "SELECT * FROM user WHERE srcode = ?";

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

}
