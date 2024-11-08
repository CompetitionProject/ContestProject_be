package core.contest_project.community.school_and_department.repository;

import core.contest_project.community.school_and_department.entity.Department;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DepartmentJpaRepository extends JpaRepository<Department, Long> {

    @Query("select department from Department department" +
            " join fetch department.school s" +
            " where s.schoolName=:schoolName and department.departmentName like :prefix%")
    Slice<Department> findAllBySchoolAndPrefix(@Param("schoolName")String schoolName, @Param("prefix")String prefix, Pageable pageable);

    @Query("select department from Department department" +
            " join fetch department.school s" +
            " where s.schoolName=:schoolName")
    Slice<Department> findAllBySchool(@Param("schoolName")String schoolName, Pageable pageable);

}
