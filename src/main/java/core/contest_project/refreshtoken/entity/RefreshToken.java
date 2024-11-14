package core.contest_project.refreshtoken.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;


@Entity
@NoArgsConstructor(access = PROTECTED)
@Getter
@Builder
@AllArgsConstructor(access = PROTECTED)
public class RefreshToken {
    @Id @GeneratedValue(strategy= IDENTITY)
    @Column(name="refresh_token_id")
    private Long id;

    private Long userId;
    private String refreshToken;

    boolean isBlacklist;

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void blacklist(){
        isBlacklist = true;
    }
}
