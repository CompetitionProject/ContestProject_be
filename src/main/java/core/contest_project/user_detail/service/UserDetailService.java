package core.contest_project.user_detail.service;

import core.contest_project.user.service.data.UserDomain;
import core.contest_project.user_detail.UserDetailType;
import core.contest_project.user_detail.repository.UserDetailJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserDetailService {
    private final UserDetailRepository userDetailRepository;
    private final UserDetailJpaRepository userDetailJpaRepository;

    public UserDetailInfo getUserDetail(UserDomain user) {
        return userDetailRepository.findAllByUser(user.getId());
    }

    public void update(UserDetailInfo detailsToUpdate, UserDomain user ){
        UserDetailInfo currentDetails = getUserDetail(user);

        if(detailsToUpdate==null){
            return;
        }
        update(UserDetailType.CONTEST_EXPERIENCE, currentDetails.getContestExperiences(), detailsToUpdate.getContestExperiences(), user.getId());
        update(UserDetailType.AWARD, currentDetails.getAwardUrls(), detailsToUpdate.getAwardUrls(), user.getId());
        update(UserDetailType.CERTIFICATION, currentDetails.getCertificates(), detailsToUpdate.getCertificates(), user.getId());
        update(UserDetailType.STACK, currentDetails.getStacks(), detailsToUpdate.getStacks(), user.getId());

    }

    public void update(UserDetailType detailType, List<String> currentDetails, List<String> updateRequest, Long userId){
        log.info("[UserDetailService][update]");
        log.info("currentDetails = {}" ,currentDetails);

        if(updateRequest == null){updateRequest = new ArrayList<>();}

        // 지울 거.
        List<String> toDelete=new ArrayList<>(currentDetails);
        toDelete.removeAll(updateRequest);
        log.info("toDelete={}",toDelete);
        if(!toDelete.isEmpty()) {userDetailRepository.deleteAll(detailType,toDelete, userId);}

        // 추가할 거.
        List<String> toAdd=new ArrayList<>(updateRequest);
        toAdd.removeAll(currentDetails);
        log.info("toAdd={}",toAdd);
        if(!toAdd.isEmpty()){userDetailRepository.saveAll(detailType, toAdd, userId);}
    }

    private List<String> getDetails(UserDetailType detailType, UserDetailInfo currentAllDetails){

        if(detailType==UserDetailType.AWARD){
            return currentAllDetails.getAwardUrls();
        }
        else if(detailType==UserDetailType.CERTIFICATION){
            return currentAllDetails.getCertificates();
        }
        else if(detailType==UserDetailType.STACK){
            return currentAllDetails.getStacks();
        }
        else if(detailType==UserDetailType.CONTEST_EXPERIENCE){
            return currentAllDetails.getContestExperiences();
        }

        return new ArrayList<>();
    }

    public List<UserDomain> setUserDetailsInBatch(List<UserDomain> users) {
        if (users.isEmpty()) {
            return users;
        }

        List<Long> userIds = users.stream()
                .map(UserDomain::getId)
                .collect(Collectors.toList());

        Map<Long, UserDetailInfo> userDetailMap = userDetailJpaRepository.findAllByUserIdsAsMap(userIds);

        return users.stream()
                .map(user -> user.withUserDetail(userDetailMap.get(user.getId())))
                .collect(Collectors.toList());
    }
}
