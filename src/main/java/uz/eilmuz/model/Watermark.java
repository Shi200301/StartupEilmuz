package uz.eilmuz.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "watermarks")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Watermark {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @Column(unique = true, nullable = false)
    private String uniqueToken;

    @Column(nullable = false)
    private String displayText;

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
