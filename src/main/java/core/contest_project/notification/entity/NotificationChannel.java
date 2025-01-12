package core.contest_project.notification.entity;

import core.contest_project.notification.NotificationType;
import core.contest_project.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@NoArgsConstructor(access= PROTECTED)
@Builder
@AllArgsConstructor(access= PROTECTED)
@Getter
public class NotificationChannel {

    @Id @GeneratedValue(strategy = IDENTITY)
    @Column(name="notification_channel_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private User user;

    @Enumerated(STRING)
    private NotificationType notificationType;

    private Long referenceId;



}
