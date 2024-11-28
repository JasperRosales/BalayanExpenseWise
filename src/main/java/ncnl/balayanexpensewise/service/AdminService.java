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

public class AdminService implements AdminDAO {

    Encryptor encryptor = new Encryptor();

    @Override
    public void addAdmin(Admin admin) {
        String creationQuery = "INSERT INTO admins (srcode, firstName, middleName, lastName, admin_role, gsuite, has_dual_roles, other_role, password) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnector.getUserConnection()) {
            PreparedStatement psmt = conn.prepareStatement(creationQuery);

            psmt.setString(1, admin.getSrcode());
            psmt.setString(2, admin.getFirstName());
            psmt.setString(3, admin.getMiddleName());
            psmt.setString(4, admin.getLastName());
            psmt.setString(5, admin.getRole());
            psmt.setString(6, admin.getGsuite());
            psmt.setBoolean(7, admin.isHasDualRoles());
            psmt.setString(8, admin.getOtherRole());
            psmt.setString(9, encryptor.createHashPassword(admin.getPassword()));

            psmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("[ERROR] Failed to create admin.");
            e.printStackTrace();
        }
    }

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
    public List<Admin> getAllAdmins() {
        List<Admin> adminList = new ArrayList<>();
        String query = "SELECT * FROM admins";

        try (Connection conn = DatabaseConnector.getUserConnection()) {
            PreparedStatement psmt = conn.prepareStatement(query);
            ResultSet rs = psmt.executeQuery();

            while (rs.next()) {
                Admin admin = new Admin(
                        rs.getString("srcode"),
                        rs.getString("firstName"),
                        rs.getString("middleName"),
                        rs.getString("lastName"),
                        rs.getString("admin_role"),
                        rs.getString("gsuite"),
                        rs.getBoolean("has_dual_roles"),
                        rs.getString("other_role"),
                        rs.getString("password") // Hash password
                );
                adminList.add(admin);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return adminList;
    }

    @Override
    public Admin getAdminById(String srcode) {
        Admin admin = null;
        String query = "SELECT * FROM admins WHERE srcode = ?";

        try (Connection conn = DatabaseConnector.getUserConnection()) {
            PreparedStatement psmt = conn.prepareStatement(query);
            psmt.setString(1, srcode);
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

    @Override
    public void deleteAdmin(String srcode) {
        String query = "DELETE FROM admins WHERE srcode = ?";

        try (Connection conn = DatabaseConnector.getUserConnection()) {
            PreparedStatement psmt = conn.prepareStatement(query);
            psmt.setString(1, srcode);
            psmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateAdmin(Admin admin) {
        String query = "UPDATE admins SET firstName = ?, middleName = ?, lastName = ?, admin_role = ?, gsuite = ?, has_dual_roles = ?, other_role = ?, password = ? WHERE srcode = ?";

        try (Connection conn = DatabaseConnector.getUserConnection()) {
            PreparedStatement psmt = conn.prepareStatement(query);
            psmt.setString(1, admin.getFirstName());
            psmt.setString(2, admin.getMiddleName());
            psmt.setString(3, admin.getLastName());
            psmt.setString(4, admin.getRole());
            psmt.setString(5, admin.getGsuite());
            psmt.setBoolean(6, admin.isHasDualRoles());
            psmt.setString(7, admin.getOtherRole());
            psmt.setString(8, admin.getPassword());
            psmt.setString(9, admin.getSrcode());
            psmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}



