package core.contest_project.notification.service;

import core.contest_project.notification.NotificationType;
import core.contest_project.notification.entity.Notification;
import core.contest_project.notification.entity.NotificationChannel;
import core.contest_project.notification.repostiry.NotiChannelJpaRepository;
import core.contest_project.notification.repostiry.NotificationJpaRepository;
import core.contest_project.user.entity.User;
import core.contest_project.user.repository.UserJpaRepository;
import core.contest_project.user.service.data.UserDomain;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
    private final NotiChannelJpaRepository notiChannelJpaRepository;
    private final NotificationJpaRepository notificationJpaRepository;
    private final UserJpaRepository userJpaRepository;

    public Long subscribeNotiChannel(UserDomain user, NotificationType type, Long referenceId ) {
        User findUser = userJpaRepository.getReferenceById(user.getId());

        NotificationChannel notiChannel = NotificationChannel.builder()
                .user(findUser)
                .notificationType(type)
                .referenceId(referenceId)
                .build();

        return notiChannelJpaRepository.save(notiChannel).getId();
    }


    public void unsubscribeNotiChannel(UserDomain user, NotificationType type, Long referenceId) {
        notiChannelJpaRepository.deleteNotiChannel(user.getId(), type, referenceId);
    }


    public void sendNotification(UserDomain user, NotificationType type, Long referenceId, String content){
        User findUser = userJpaRepository.getReferenceById(user.getId());

        Notification notification = Notification.builder()
                .user(findUser)
                .referenceId(referenceId)
                .content(content)
                .type(type)
                .isRead(false)
                .build();

        notificationJpaRepository.save(notification);
    }


    public Long getUnReadNotificationCount(UserDomain user){
        return notificationJpaRepository.countUnReadNotificationByUserId(user.getId());
    }

    public void readNotification(UserDomain user, Long notificationId){
        Notification notification = notificationJpaRepository.findById(notificationId).get();
        notification.read();
    }

}
