package ncnl.balayanexpensewise.repository;

import ncnl.balayanexpensewise.beans.UserLogger;

import java.util.List;

public interface UserLoggerDAO {

    /**
     * Logs a user action (login or logout).
     *
     * @param userLogger the UserLogger object containing user log details.
     * @return true if the action was logged successfully, false otherwise.
     */
    boolean logUserAction(UserLogger userLogger);

    /**
     * Retrieves the most recent log entry for a specific user.
     *
     * @param srcode the unique identifier for the user.
     * @return the most recent UserLogger entry for the specified user.
     */
    UserLogger getMostRecentLogByUser(String srcode);

    /**
     * Retrieves the most recent login or logout for any user.
     *
     * @return the most recent UserLogger entry across all users.
     */
    UserLogger getMostRecentLog();

    /**
     * Retrieves all log entries for a specific user.
     *
     * @param srcode the unique identifier for the user.
     * @return a list of UserLogger entries for the specified user.
     */
    List<UserLogger> getAllLogsByUser(String srcode);

    /**
     * Deletes all logs for a specific user.
     *
     * @param srcode the unique identifier for the user.
     * @return true if logs were deleted successfully, false otherwise.
     */
    boolean deleteLogsByUser(String srcode);

    /**
     * Deletes all logs in the system.
     *
     * @return true if all logs were deleted successfully, false otherwise.
     */
    boolean deleteAllLogs();
}
