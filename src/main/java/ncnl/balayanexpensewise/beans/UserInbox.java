package ncnl.balayanexpensewise.beans;

import lombok.Data;

@Data
public class UserInbox {
    private int id;
    private String name;
    private String role;
    private String purpose;
    private String department;
    private String control;

    public UserInbox(User user, String purpose) {
        this.name = user.getFirstName() + " " + user.getLastName();
        this.role = user.getRole();
        this.department = user.getDepartment();
        this.purpose = purpose;
    }

    public UserInbox() {}
}
