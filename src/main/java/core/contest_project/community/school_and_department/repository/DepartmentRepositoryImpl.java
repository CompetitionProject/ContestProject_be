package core.contest_project.community.school_and_department.repository;

import core.contest_project.community.school_and_department.entity.Department;
import core.contest_project.community.school_and_department.service.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@Slf4j
public class DepartmentRepositoryImpl implements DepartmentRepository {
    private final DepartmentJpaRepository departmentJpaRepository;

    @Override
    public Slice<String> findAllBySchoolNameAndPrefix(String schoolName, String prefix, Pageable pageable) {
        if(prefix == null || prefix.isEmpty()){
            return departmentJpaRepository.findAllBySchool(schoolName, pageable).map(Department::getDepartmentName);
        }
        return departmentJpaRepository.findAllBySchoolAndPrefix(schoolName, prefix, pageable).map(Department::getDepartmentName);
    }
}
