package core.contest_project.user_detail.service;

import core.contest_project.user_detail.UserDetailType;
import core.contest_project.user_detail.entity.UserDetail;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public interface UserDetailRepository {

    void saveAll(UserDetailType userDetailType, List<String> details, Long userId);
    UserDetailInfo findAllByUser(Long userId);
    void deleteAll(UserDetailType userDetailType, List<String> details, Long userId);

    void deleteAll(Long userId);

}
