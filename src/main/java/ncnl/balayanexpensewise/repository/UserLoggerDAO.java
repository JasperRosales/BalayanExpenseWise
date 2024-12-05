package ncnl.balayanexpensewise.repository;

import ncnl.balayanexpensewise.beans.UserLogger;

import java.util.List;

public interface UserLoggerDAO {

    boolean logUserAction(UserLogger userLogger);


}
