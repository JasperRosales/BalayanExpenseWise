package ncnl.balayanexpensewise.repository;

import ncnl.balayanexpensewise.beans.Admin;
import java.util.List;

public interface AdminDAO {
    void addAdmin(Admin admin);
    List<Admin> getAllAdmins();
    Admin validateAdmin(String srcode, String password);
    Admin getAdminById(String srcode);
    void deleteAdmin(String srcode);
    void updateAdmin(Admin admin);
}
