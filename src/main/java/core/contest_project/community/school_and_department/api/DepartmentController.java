package core.contest_project.community.school_and_department.api;

import core.contest_project.community.school_and_department.service.DepartmentService;
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
public class DepartmentController {
    private final DepartmentService departmentService;

    @GetMapping("/api/departments")
    public ResponseEntity<Slice<String>> getSchoolsByPrefix(@RequestParam("schoolName") String schoolName,
                                                                  @RequestParam(value="search", required = false) String prefix,
                                                                  @RequestParam(value="page", required = false) Integer page) {
        Slice<String> departments = departmentService.getDepartmentsBySchoolNameAndPrefix(schoolName, prefix, page);
        return ResponseEntity.ok(departments);
    }
}
