package ncnl.balayanexpensewise.beans;

import lombok.Data;

import java.util.Arrays;
import java.util.List;

@Data
public class Admin extends User{
    private boolean hasDualRoles;
    private String otherRole;

    List<String> adminRoles = Arrays.asList(
            "SSC Adviser",
            "CICS Adviser",
            "CET Adviser",
            "Vice President for Student Development and Governance",
            "CET Governor",
            "CICS Governor",
            "Committee Chairperson on Records",
            "Committee Chairperson on Finance",
            "Committee Chairperson on Student Services",
            "Committee Chairperson on Videography",
            "Committee Chairperson on Student Activities",
            "Committee Chairperson on Publicity"
    );

    public Admin(String srcode, String firstName, String middleName, String lastName, String role, String gsuite, Boolean hasDualRoles, String otherRole, String password) {
        super(srcode, firstName, middleName, lastName, role, gsuite, password);
        this.hasDualRoles = hasDualRoles;
        this.otherRole = (hasDualRoles) ? otherRole : null;
    }

    public boolean checkIfAdmin(String Role){
        return adminRoles.stream().anyMatch(e -> e.equalsIgnoreCase(Role));
    }

    public boolean isHasDualRoles() {
        return hasDualRoles;
    }

    public String getOtherRole() {
        return otherRole;
    }

    public List<String> getAdminRoles() {
        return adminRoles;
    }

    public void setHasDualRoles(boolean hasDualRoles) {
        this.hasDualRoles = hasDualRoles;
    }

    public void setOtherRole(String otherRole) {
        this.otherRole = otherRole;
    }

    public void setAdminRoles(List<String> adminRoles) {
        this.adminRoles = adminRoles;
    }
}
