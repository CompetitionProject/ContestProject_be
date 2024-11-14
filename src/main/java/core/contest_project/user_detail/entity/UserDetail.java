package core.contest_project.user_detail.entity;

import core.contest_project.user.entity.User;
import core.contest_project.user_detail.UserDetailType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDetail {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="user_detail_id")
    private Long id;

    @ManyToOne(fetch=LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String name;

    @Enumerated(EnumType.STRING)
    private UserDetailType detailType;
}
