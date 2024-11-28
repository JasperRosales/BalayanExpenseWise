package ncnl.balayanexpensewise.repository;

import ncnl.balayanexpensewise.beans.User;

import java.util.Optional;

public interface UserDAO{
    void createUser(User user);
    void deleteUser(String id);
    void updateUser(User userOptional);
    User validateUser(String srcode, String password);

}
