package core.contest_project.notification.entity;

import core.contest_project.notification.NotificationType;
import core.contest_project.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@NoArgsConstructor(access = PROTECTED)
@Builder
@AllArgsConstructor(access = PROTECTED)
@Getter
public class Notification {
    @Id @GeneratedValue(strategy = IDENTITY)
    @Column(name="notification_id")
    public Long id;

    @ManyToOne(fetch=LAZY)
    @JoinColumn
    private User user;

    @Enumerated(STRING)
    private NotificationType type;
    private Long referenceId;

    private String content;

    private boolean isRead;


    public void read(){
        isRead = true;
    }

}
