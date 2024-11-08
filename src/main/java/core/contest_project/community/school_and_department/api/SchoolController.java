package core.contest_project.community.school_and_department.api;

import core.contest_project.community.school_and_department.service.SchoolService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class SchoolController {
    private final SchoolService schoolService;

    @GetMapping("/api/schools")
    public ResponseEntity<Slice<String>> getSchoolsByPrefix(@RequestParam(value="search", required = false) String prefix,
                                                                  @RequestParam(value="page", required = false) Integer page) {
        Slice<String> schoolsByPrefix = schoolService.getSchoolsByPrefix(prefix, page);
        return ResponseEntity.ok(schoolsByPrefix);
    }
}
