package core.contest_project.community.school_and_department.entity;

import jakarta.persistence.*;
import lombok.*;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Getter
@Entity
@NoArgsConstructor(access = PROTECTED)
@Builder
@AllArgsConstructor
public class School {
    @Id @GeneratedValue(strategy = IDENTITY)
    @Column(name="school_id")
    private Long id;
    private String schoolName;


}
