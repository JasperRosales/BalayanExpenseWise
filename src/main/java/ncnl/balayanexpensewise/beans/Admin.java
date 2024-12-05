package ncnl.balayanexpensewise.beans;

import lombok.Data;

import java.util.Arrays;
import java.util.List;

public class Admin extends User{
    private boolean hasDualRoles;
    private String otherRole;


    public Admin(String srcode, String firstName, String middleName, String lastName, String role, String gsuite, Boolean hasDualRoles, String otherRole, String password) {
        super(srcode, firstName, middleName, lastName, role, gsuite, password);
        this.hasDualRoles = hasDualRoles;
        this.otherRole = (hasDualRoles) ? otherRole : null;
    }

    public Admin(String srcode, String firstName, String middleName, String lastName, String role, String gsuite, String department, String otherRole, String password) {
        super(srcode, firstName, middleName, lastName, department, role, gsuite, password);
        this.hasDualRoles = isHasDualRoles();
        this.otherRole = (hasDualRoles) ? otherRole : null;
    }






    public boolean isHasDualRoles() {
        return hasDualRoles;
    }

    public String getOtherRole() {
        return otherRole;
    }


}
