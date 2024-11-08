package core.contest_project.community.school_and_department.service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface SchoolRepository {

    Slice<String> findAllByPrefix(String prefix, Pageable pageable);
}
