package core.contest_project.common.security;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import static lombok.AccessLevel.PROTECTED;

@Getter
@Builder
@AllArgsConstructor(access = PROTECTED)
@ToString
public class KaKaoInfo {
    private String nickname;
    private String email;
    private String profileUrl;


}
