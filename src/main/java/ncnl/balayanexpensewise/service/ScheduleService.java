package ncnl.balayanexpensewise.service;

import ncnl.balayanexpensewise.beans.Schedule;
import ncnl.balayanexpensewise.config.DatabaseConnector;
import ncnl.balayanexpensewise.repository.ScheduleDAO;
import ncnl.balayanexpensewise.utils.AlarmUtils;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ScheduleService implements ScheduleDAO {

    @Override
    public void addSchedule(Schedule schedule) {
        final String INSERT_QUERY = "INSERT INTO event_schedules (event_name, event_date, description) VALUES (?, ?, ?)";
        try (Connection connection = DatabaseConnector.getUserConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_QUERY)) {

            preparedStatement.setString(1, schedule.getEventName());
            preparedStatement.setDate(2, Date.valueOf(schedule.getDate()));
            preparedStatement.setString(3, schedule.getDescription());
            preparedStatement.executeUpdate();

            AlarmUtils.showCustomSuccessAlert("Successfully Added an event");

        } catch (SQLException e) {
            e.printStackTrace();
            AlarmUtils.showErrorAlert("Something went wrong");
        }
    }

    @Override
    public void updateSchedule(Integer id, Schedule schedule) {
        final String UPDATE_QUERY = "UPDATE event_schedules SET event_name = ?, event_date = ?, description = ? WHERE id = ?";

        try (Connection connection = DatabaseConnector.getUserConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_QUERY)) {

            preparedStatement.setString(1,  schedule.getEventName());
            preparedStatement.setDate(2, Date.valueOf(schedule.getDate()));
            preparedStatement.setString(3, schedule.getDescription());
            preparedStatement.setInt(4, id);

            preparedStatement.executeUpdate();

            AlarmUtils.showCustomSuccessAlert("Successfully edited");
            int rowsAffected = preparedStatement.executeUpdate();
            System.out.println("Rows affected: " + rowsAffected);

        } catch (SQLException e) {
            e.printStackTrace();
            AlarmUtils.showErrorAlert("Something went wrong");
        }


    }


    @Override
    public void deleteSchedule(Integer id) {
        final String DELETE_QUERY = "DELETE FROM event_schedules WHERE id = ?";

        try (Connection connection = DatabaseConnector.getUserConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_QUERY)) {

            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();

            AlarmUtils.showCustomSuccessAlert("Successfully Deleted the event");

        } catch (SQLException e) {
            e.printStackTrace();
            AlarmUtils.showErrorAlert("Something went wrong during deletion");
        }
    }


    @Override
    public List<Schedule> getAllSchedules() {
        List<Schedule> schedules = new ArrayList<>();
        String query = "SELECT * FROM event_schedules ORDER BY event_date ASC";
        ;

        try (Connection conn = DatabaseConnector.getUserConnection();
             PreparedStatement psmt = conn.prepareStatement(query);
             ResultSet rs = psmt.executeQuery()) {

            while (rs.next()) {
                Schedule schedule = new Schedule();
                schedule.setEventName(rs.getString("event_name"));
                schedule.setDate(rs.getDate("event_date").toLocalDate());
                schedule.setDescription(rs.getString("description"));

                schedules.add(schedule);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return schedules;
    }


    public Schedule getNearestEvent() {
        final String NEAREST_EVENT_QUERY = "SELECT event_name, event_date, description FROM event_schedules WHERE event_date >= CURDATE() ORDER BY event_date ASC LIMIT 1";

        try (Connection connection = DatabaseConnector.getUserConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(NEAREST_EVENT_QUERY);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            if (resultSet.next()) {
                Schedule schedule = new Schedule();
                schedule.setEventName(resultSet.getString("event_name"));
                schedule.setDate(resultSet.getDate("event_date").toLocalDate());
                schedule.setDescription(resultSet.getString("description"));
                return schedule;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }


    public int getScheduleIdByDetails(String eventName, LocalDate eventDate) {
        final String SELECT_ID_QUERY = "SELECT id FROM event_schedules WHERE event_name = ? AND event_date = ?";

        try (Connection connection = DatabaseConnector.getUserConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ID_QUERY)) {

            preparedStatement.setString(1, eventName);
            preparedStatement.setDate(2, Date.valueOf(eventDate));

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }



}
