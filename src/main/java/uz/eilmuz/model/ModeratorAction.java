package uz.eilmuz.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "moderator_actions")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ModeratorAction {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "moderator_id", nullable = false)
    private User moderator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(nullable = false)
    private String action; // APPROVED or REJECTED

    @Column(columnDefinition = "TEXT")
    private String note;

    private LocalDateTime actionAt;

    @PrePersist
    protected void onCreate() {
        actionAt = LocalDateTime.now();
    }
}
