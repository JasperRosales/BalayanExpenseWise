package ncnl.balayanexpensewise.beans;


import javafx.scene.control.Label;
import lombok.Data;

@Data
public class UserLogger {
    private String srcode;
    private String fullname;
    private String department;
    private String role;
    private String event;

    public UserLogger(String srcode, String fullname, String department, String role, String event) {
        this.srcode = srcode;
        this.fullname = fullname;
        this.department = department;
        this.role = role;
        this.event = event;
    }

    public UserLogger(String fullname, String role, String event) {
        this.fullname = fullname;
        this.role = role;
        this.event = event;
    }

    public void updateLabels(Label nameLabel, Label roleLabel, Label eventLabel) {
        nameLabel.setText(this.fullname);
        roleLabel.setText(this.role);
        eventLabel.setText(this.event);
    }
}
