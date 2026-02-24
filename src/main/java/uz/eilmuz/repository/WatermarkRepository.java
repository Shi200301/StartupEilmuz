package uz.eilmuz.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.eilmuz.model.User;
import uz.eilmuz.model.Watermark;
import java.util.Optional;

public interface WatermarkRepository extends JpaRepository<Watermark, Long> {
    Optional<Watermark> findByStudent(User student);
}
