package ncnl.balayanexpensewise.beans;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum UserRoles {
        PRESIDENT("President"),
        EXECUTIVE_VICE_PRESIDENT("Executive Vice President"),
        SECRETARY_GENERAL("Secretary General"),
        DEPUTY_SECRETARY_GENERAL("Deputy Secretary General"),
        FINANCE_OFFICER("Finance Officer"),
        DEPUTY_FINANCE_OFFICER("Deputy Finance Officer"),
        AUDITOR("Auditor"),
        PRESS_RELATION_OFFICER("Press Relation Officer"),
        BUSINESS_MANAGER("Business Manager"),
        COMMITTEE_ON_PUBTECH_AFFAIRS("Committee on PubTech Affairs"),
        COMMITTEE_ON_CREATIVE_DESIGN("Committee on Creative Design"),
        COMMITTEE_ON_MEDIA_AFFAIRS("Committee on Media Affairs");

    private final String roleName;

    UserRoles(String roleName) {
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
                .map(UserRoles::getRoleName)
                .collect(Collectors.toList());
    }
}
