package ncnl.balayanexpensewise.beans;

import lombok.Data;

import java.util.Date;

@Data
public class User {
    protected String srcode;
    protected String firstName;
    protected String middleName;
    protected String lastName;
    private String department;
    protected String role;
    protected String gsuite;
    protected String password;

    public User(String srcode, String firstName, String middleName, String lastName, String role, String gsuite, String password) {
        this.srcode = srcode;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.role = role;
        this.gsuite = gsuite;
        this.password = password;
    }

    public User(String srcode, String firstName, String middleName, String lastName, String department, String role, String gsuite, String password) {
        this.srcode = srcode;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.department = department;
        this.role = role;
        this.gsuite = gsuite;
        this.password = password;
    }

    public String getInitialMiddleName(String middleName){
        return middleName.substring(0,1).toUpperCase() + ".";
    }

    public String getFullname(String firstName, String middleName, String lastName){
        return firstName + middleName + lastName;
    }




}
