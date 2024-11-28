package ncnl.balayanexpensewise.repository;

import ncnl.balayanexpensewise.beans.Schedule;

import java.util.List;

public interface ScheduleDAO {
    void addSchedule(Schedule schedule);
    void updateSchedule(String name, Schedule schedule);
    void deleteSchedule(String name);
    List<Schedule> getAllSchedules();
    Schedule getScheduleById(int id);
}
