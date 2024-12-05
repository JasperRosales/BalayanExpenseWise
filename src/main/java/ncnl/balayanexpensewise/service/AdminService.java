package ncnl.balayanexpensewise.service;

import ncnl.balayanexpensewise.beans.Admin;
import ncnl.balayanexpensewise.beans.Encryptor;
import ncnl.balayanexpensewise.repository.AdminDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import ncnl.balayanexpensewise.config.DatabaseConnector;
import ncnl.balayanexpensewise.utils.AlarmUtils;

public class AdminService implements AdminDAO {

    public Admin validateAdmin(String srcode, String password) {
        Admin admin = null;
        String authAdmin = "SELECT * FROM admins WHERE srcode = ?";

        try (Connection conn = DatabaseConnector.getUserConnection()) {
            PreparedStatement psmt = conn.prepareStatement(authAdmin);
            psmt.setString(1, srcode);
            ResultSet rs = psmt.executeQuery();

            if (rs.next()) {
                String storedHash = rs.getString("password");

                Encryptor encryptor = new Encryptor();
                String hashedPassword = encryptor.createHashPassword(password);

                if (storedHash.equals(hashedPassword)) {
                    admin = new Admin(
                            rs.getString("srcode"),
                            rs.getString("firstName"),
                            rs.getString("middleName"),
                            rs.getString("lastName"),
                            rs.getString("admin_role"),
                            rs.getString("gsuite"),
                            rs.getBoolean("has_dual_roles"),
                            rs.getString("other_role"),
                            rs.getString("password")
                    );
                } else {
                    System.out.println("[ERROR] Invalid Password");
                }
            } else {
                System.out.println("[ERROR] Admin not found.");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return admin;
    }


    @Override
    public void deleteAdmin(Integer tableId) {
        String DELETE_ADMIN = "DELETE FROM admins WHERE table_id =?";

        try (Connection conn = DatabaseConnector.getUserConnection()) {
            PreparedStatement psmt = conn.prepareStatement(DELETE_ADMIN);
            psmt.setInt(1, tableId);
            psmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Integer getTableIdByRoleAndDepartment(String role, String department) {
        String SELECT_TABLE_ID = "SELECT table_id FROM admins WHERE admin_role = ?";

        try (Connection conn = DatabaseConnector.getUserConnection()) {
            PreparedStatement psmt = conn.prepareStatement(SELECT_TABLE_ID);
            psmt.setString(1, role);
            ResultSet rs = psmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("table_id");
            } else {
                System.out.println("No record found with the specified role and department.");
                return -1;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }


    @Override
    public void updateAdmin(Integer tableId ,Admin admin) {
        String UPDATE_ADMIN = "UPDATE admins SET srcode = ?, firstName = ?, middleName = ?, lastName = ?, admin_role = ?, gsuite = ?, has_dual_roles = ?, other_role = ?, password = ? WHERE table_id = ?";

        try (Connection conn = DatabaseConnector.getUserConnection()) {
            PreparedStatement psmt = conn.prepareStatement(UPDATE_ADMIN);
            psmt.setString(1, admin.getSrcode());
            psmt.setString(2, admin.getFirstName());
            psmt.setString(3, admin.getMiddleName());
            psmt.setString(4, admin.getLastName());
            psmt.setString(5, admin.getRole());
            psmt.setString(6, admin.getGsuite());
            psmt.setBoolean(7, admin.isHasDualRoles());
            psmt.setString(8, admin.getOtherRole());
            psmt.setString(9, admin.getPassword());
            psmt.setInt(10, tableId);
            psmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public Admin findByFirstLastName(String firstName, String lastName){
        Admin admin = null;
        String query = "SELECT * FROM admins WHERE firstName = ? and lastName = ?";

        try (Connection conn = DatabaseConnector.getUserConnection()) {
            PreparedStatement psmt = conn.prepareStatement(query);
            psmt.setString(1, firstName);
            psmt.setString(2, lastName);
            ResultSet rs = psmt.executeQuery();

            if (rs.next()) {
                admin = new Admin(
                        rs.getString("srcode"),
                        rs.getString("firstName"),
                        rs.getString("middleName"),
                        rs.getString("lastName"),
                        rs.getString("admin_role"),
                        rs.getString("gsuite"),
                        rs.getBoolean("has_dual_roles"),
                        rs.getString("other_role"),
                        rs.getString("password")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return admin;
    }



}



