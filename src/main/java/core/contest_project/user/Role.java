package core.contest_project.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Role {
    USER("USER"),
    ADMIN("ADMIN");

    private final String role;




    public Role getRole(String string){
        for(Role role : Role.values()){
            role = role.getRole(string);
            if(role.equals(string)){
                return role;
            }
        }
        return null;
    }

}
