package ncnl.balayanexpensewise.repository;

import ncnl.balayanexpensewise.beans.Admin;
import java.util.List;

public interface AdminDAO {
    Admin validateAdmin(String srcode, String password);
    void deleteAdmin(Integer tableId);
    void updateAdmin(Integer tableId, Admin admin);
}
