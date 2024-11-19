package core.contest_project.user_detail.repository;

import core.contest_project.user_detail.UserDetailType;
import core.contest_project.user_detail.entity.UserDetail;
import core.contest_project.user_detail.service.UserDetailInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public interface UserDetailJpaRepository extends JpaRepository<UserDetail, Long> {

    @Query("select detail from UserDetail detail" +
            " where detail.user.id=:userId")
    List<UserDetail> findAllByUserId(@Param("userId")Long userId);


    @Modifying
    @Query("delete from UserDetail detail where detail.user.id=:userId and detail.name=:name and detail.detailType=:type")
    void deleteUserDetailByUserIdAndDetailType(@Param("userId")Long userId, @Param("name")String name, @Param("type") UserDetailType type);

    @Query("SELECT ud FROM UserDetail ud " +
            "JOIN FETCH ud.user " +  // User를 함께 조회
            "WHERE ud.user.id IN :userIds")
    List<UserDetail> findAllByUserIds(@Param("userIds") List<Long> userIds);

    default Map<Long, UserDetailInfo> findAllByUserIdsAsMap(List<Long> userIds) {
        if (userIds.isEmpty()) {
            return Collections.emptyMap();
        }

        List<UserDetail> userDetails = findAllByUserIds(userIds);
        Map<Long, List<UserDetail>> userDetailMap = userDetails.stream()
                .collect(Collectors.groupingBy(detail -> detail.getUser().getId()));

        // userIds의 모든 ID에 대해 맵 생성 (UserDetail이 없는 경우도 처리)
        return userIds.stream()
                .collect(Collectors.toMap(
                        userId -> userId,
                        userId -> {
                            List<UserDetail> details = userDetailMap.getOrDefault(userId, Collections.emptyList());
                            return UserDetailInfo.builder()
                                    .contestExperiences(filterByType(details, UserDetailType.CONTEST_EXPERIENCE))
                                    .awardUrls(filterByType(details, UserDetailType.AWARD))
                                    .certificates(filterByType(details, UserDetailType.CERTIFICATION))
                                    .stacks(filterByType(details, UserDetailType.STACK))
                                    .build();
                        }
                ));
    }

    private List<String> filterByType(List<UserDetail> details, UserDetailType type) {
        return details.stream()
                .filter(detail -> detail.getDetailType() == type)
                .map(UserDetail::getName)
                .collect(Collectors.toList());
    }
}
