package core.contest_project.community.school_and_department.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class DepartmentService {
    private final DepartmentRepository departmentRepository;

    public Slice<String> getDepartmentsBySchoolNameAndPrefix(String schoolName, String prefix, Integer page) {
        if(page==null){page=0;}
        Pageable pageable=PageRequest.of(page, 5, Sort.by(Sort.Direction.ASC, "departmentName"));
        return departmentRepository.findAllBySchoolNameAndPrefix(schoolName, prefix, pageable);
    }
}
