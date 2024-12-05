package ncnl.balayanexpensewise.service;

import lombok.Data;
import ncnl.balayanexpensewise.beans.LabelRow;
import ncnl.balayanexpensewise.beans.User;
import ncnl.balayanexpensewise.beans.UserLogger;
import ncnl.balayanexpensewise.config.DatabaseConnector;
import ncnl.balayanexpensewise.repository.UserLoggerDAO;

import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserLoggerService implements UserLoggerDAO {

    private boolean isAdminMode = false;
    private static final String GET_LAST_5_USER_LOGS_QUERY_TEMPLATE = "SELECT fullname, role, event from %s ORDER BY timestamp DESC LIMIT 5";


    /**
     * Sets the admin mode state.
     *
     * @param adminMode true if admin mode is active, false otherwise
     */
    public void setAdminMode(boolean adminMode) {
        this.isAdminMode = adminMode;
    }

    /**
     * Retrieves the full name (first name + last name) of a user/admin by srcode.
     *
     * @param srcode the user's srcode
     * @return the full name, or null if no record is found
     */
    public String getFullName(String srcode) {
        String table = isAdminMode ? "admins" : "users";
        String sql = "SELECT firstName, lastName FROM " + table + " WHERE srcode = ?";

        try (Connection conn = DatabaseConnector.getUserConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, srcode);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String fname = rs.getString("firstName");
                    String lname = rs.getString("lastName");
                    return fname + " " + lname;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Retrieves the department of a user/admin by srcode.
     *
     * @param srcode the user's srcode
     * @return the department, or null if no record is found
     */
    public String getDepartment(String srcode) {
        if (isAdminMode) {
            return "SSC";
        }

        String sql = "SELECT department FROM users WHERE srcode = ?";

        try (Connection conn = DatabaseConnector.getUserConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, srcode);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("department");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }


    /**
     * Retrieves the role of a user/admin by srcode.
     *
     * @param srcode the user's srcode
     * @return the role, or null if no record is found
     */
    public String getRole(String srcode) {
        String table = isAdminMode ? "admins" : "users";
        String row = isAdminMode ? "admin_role" : "role";
        String sql = "SELECT " + row + " FROM " + table + " WHERE srcode = ?";

        try (Connection conn = DatabaseConnector.getUserConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, srcode);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString(row);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean logUserAction(UserLogger userLogger) {
        String sql = "INSERT INTO user_logger (srcode, fullname, department, role, event, timestamp) VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP())";
        try (Connection conn = DatabaseConnector.getUserConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, userLogger.getSrcode());
            stmt.setString(2, userLogger.getFullname());
            stmt.setString(3, userLogger.getDepartment());
            stmt.setString(4, userLogger.getRole());
            stmt.setString(5, userLogger.getEvent());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public List<UserLogger> getRecentLoggingEvents() {
        String sql = "SELECT fullname, role, event FROM user_logger ORDER BY timestamp DESC LIMIT 5";
        List<UserLogger> recentEvents = new ArrayList<>();

        try (Connection conn = DatabaseConnector.getUserConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                UserLogger event = new UserLogger(
                        rs.getString("fullname"),
                        rs.getString("role"),
                        rs.getString("event")
                );
                recentEvents.add(event);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return recentEvents;
    }
}
