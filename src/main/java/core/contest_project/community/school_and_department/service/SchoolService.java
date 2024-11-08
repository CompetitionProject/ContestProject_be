package core.contest_project.community.school_and_department.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SchoolService {
    private final SchoolRepository schoolRepository;

    public Slice<String> getSchoolsByPrefix(String prefix, Integer page) {
        if(page==null){page=0;}
        Pageable pageable = PageRequest.of(page, 5, Sort.by(Sort.Direction.ASC, "schoolName"));
        return schoolRepository.findAllByPrefix(prefix, pageable);
    }


}
