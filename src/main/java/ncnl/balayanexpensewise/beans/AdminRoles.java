package ncnl.balayanexpensewise.beans;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum AdminRoles {
    VICE_PRESIDENT_FOR_STUDENT_DEVELOPMENT_AND_GOVERNANCE("Vice President for Student Development and Governance"),
    CET_BALAYAN_GOVERNOR("CET Balayan Governor"),
    CICS_BALAYAN_GOVERNOR("CICS Balayan Governor"),
    COMMITTEE_CHAIRPERSON_ON_RECORDS("Committee Chairperson on Records"),
    COMMITTEE_CHAIRPERSON_ON_FINANCE("Committee Chairperson on Finance"),
    COMMITTEE_CHAIRPERSON_ON_STUDENT_SERVICES("Committee Chairperson on Student Services"),
    COMMITTEE_CHAIRPERSON_ON_VIDEOGRAPHY("Committee Chairperson on Videography"),
    COMMITTEE_CHAIRPERSON_ON_DIGITAL_GRAPHICS("Committee Chairperson on Digital Graphics"),
    COMMITTEE_CHAIRPERSON_ON_STUDENT_ACTIVITIES("Committee Chairperson on Student Activities"),
    COMMITTEE_CHAIRPERSON_ON_PUBLICITY("Committee Chairperson on Publicity");

    private final String roleName;

    AdminRoles(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleName() {
        return roleName;
    }

    @Override
    public String toString() {
        return roleName;
    }

    public static List<String> getRoleNames() {
        return Arrays.stream(values())
                .map(AdminRoles::getRoleName)
                .collect(Collectors.toList());
    }
}
