package core.contest_project.notification.repostiry;

import core.contest_project.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NotificationJpaRepository extends JpaRepository<Notification, Long> {

    @Query("select count(*) from Notification  noti" +
            " where noti.user.id=:userId and noti.isRead=false")
    Long countUnReadNotificationByUserId(@Param("userId")Long userId);

}
