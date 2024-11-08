package core.contest_project.community.user_detail.repository;

import core.contest_project.community.user_detail.UserDetailType;
import core.contest_project.community.user_detail.entity.UserDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserDetailJpaRepository extends JpaRepository<UserDetail, Long> {

    @Query("select detail from UserDetail detail" +
            " where detail.user.id=:userId")
    List<UserDetail> findAllByUserId(@Param("userId")Long userId);


    @Modifying
    @Query("delete from UserDetail detail where detail.user.id=:userId and detail.name=:name and detail.detailType=:type")
    void deleteUserDetailByUserIdAndDetailType(@Param("userId")Long userId, @Param("name")String name, @Param("type") UserDetailType type);
}
