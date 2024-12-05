package ncnl.balayanexpensewise.repository;

import ncnl.balayanexpensewise.beans.Schedule;

import java.util.List;

public interface ScheduleDAO {
    void addSchedule(Schedule schedule);
    void updateSchedule(Integer id, Schedule schedule);
    void deleteSchedule(Integer id);
    List<Schedule> getAllSchedules();
}
