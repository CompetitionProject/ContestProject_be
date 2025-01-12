package core.contest_project.notification.repostiry;

import core.contest_project.notification.NotificationType;
import core.contest_project.notification.entity.NotificationChannel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NotiChannelJpaRepository extends JpaRepository<NotificationChannel, Long> {

    @Modifying
    @Query("delete from NotificationChannel ch" +
            " where ch.user.id=:userId and ch.notificationType.type=:type and ch.referenceId=:referenceId")
    void deleteNotiChannel(@Param("userId") Long userId, @Param("type")NotificationType type, @Param("referenceId") Long referenceId);
}
