package core.contest_project.community.school_and_department.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Getter
@Entity
@NoArgsConstructor(access = PROTECTED)
@Builder
@AllArgsConstructor
public class Department {

    @Id @GeneratedValue(strategy = IDENTITY)
    @Column(name="department_id")
    private Long id;
    private String departmentName;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name="school_id")
    private School school;
}
