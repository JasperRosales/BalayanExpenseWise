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
    public void updateSchedule(String eventName, Schedule schedule) {
        final String UPDATE_QUERY = "UPDATE event_schedules SET date = ?, description = ? WHERE event_name = ?";
        try (Connection connection = DatabaseConnector.getUserConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_QUERY)) {

            preparedStatement.setDate(1, Date.valueOf(schedule.getDate()));
            preparedStatement.setString(2, schedule.getDescription());
            preparedStatement.setString(3, schedule.getEventName());
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteSchedule(String eventName) {
        final String DELETE_QUERY = "DELETE FROM event_schedules WHERE event_name = ?";
        try (Connection connection = DatabaseConnector.getUserConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_QUERY)) {

            preparedStatement.setString(1, eventName);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @Override
    public List<Schedule> getAllSchedules() {
        List<Schedule> schedules = new ArrayList<>();
        String query = "SELECT * FROM event_schedules";

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


    public List<Schedule> getEventsByNameOrDate(String eventName, LocalDate eventDate) {
        List<Schedule> schedules = new ArrayList<>();

        StringBuilder sql = new StringBuilder("SELECT * FROM event_schedules WHERE 1=1");

        if (eventName != null && !eventName.trim().isEmpty()) {
            sql.append(" AND event_name LIKE ?");
        }
        if (eventDate != null) {
            sql.append(" AND event_date = ?");
        }

        try (Connection connection = DatabaseConnector.getUserConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql.toString());

            int index = 1;

            if (eventName != null && !eventName.trim().isEmpty()) {
                statement.setString(index++, "%" + eventName + "%");
            }

            if (eventDate != null) {
                statement.setDate(index++, Date.valueOf(eventDate));
            }

            ResultSet resultSet = statement.executeQuery();

            // Process the result set
            while (resultSet.next()) {
                String name = resultSet.getString("event_name");
                LocalDate date = resultSet.getDate("event_date").toLocalDate();
                String description = resultSet.getString("description");

                Schedule schedule = new Schedule(name, date, description);
                schedules.add(schedule);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return schedules;
    }



    @Override
    public Schedule getScheduleById(int id) {
        final String SELECT_QUERY = "SELECT * FROM event_schedules WHERE id = ?";
        try (Connection connection = DatabaseConnector.getUserConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_QUERY)) {

            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    Schedule schedule = new Schedule();
                    schedule.setId(resultSet.getInt("id"));
                    schedule.setDate(resultSet.getDate("date").toLocalDate());
                    schedule.setDescription(resultSet.getString("description"));
                    return schedule;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}
