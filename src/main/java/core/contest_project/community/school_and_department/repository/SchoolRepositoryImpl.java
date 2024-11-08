package core.contest_project.community.school_and_department.repository;

import core.contest_project.community.school_and_department.entity.School;
import core.contest_project.community.school_and_department.service.SchoolRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@Slf4j
public class SchoolRepositoryImpl implements SchoolRepository {
    private final SchoolJpaRepository schoolJpaRepository;


    @Override
    public Slice<String> findAllByPrefix(String prefix, Pageable pageable) {
        if(prefix == null || prefix.isEmpty()){
            return schoolJpaRepository.findAllBy(pageable).map(School::getSchoolName);
        }
        return schoolJpaRepository.findAllByPrefix(prefix, pageable).map(School::getSchoolName);
    }
}
