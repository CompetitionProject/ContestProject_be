package core.contest_project.file.entity;

import jakarta.persistence.*;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
public class Contest {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name="contest_id")
    private Long id;
}
