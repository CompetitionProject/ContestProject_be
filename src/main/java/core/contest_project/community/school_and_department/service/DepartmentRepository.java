package core.contest_project.community.school_and_department.service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface DepartmentRepository {
    Slice<String> findAllBySchoolNameAndPrefix(String schoolName, String prefix, Pageable pageable);
}
