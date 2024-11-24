package core.contest_project.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Role {
    ROLE_USER("ROLE_USER"),
    ROLE_ADMIN("ROLE_ADMIN");

    private final String role;

    public static Role getRole(String string) {
        for (Role role : Role.values()) {
            if (role.getRole().equals(string)) {
                return role;
            }
        }
        return null;
    }
}