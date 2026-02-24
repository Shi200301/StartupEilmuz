package uz.eilmuz.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.eilmuz.model.Course;
import uz.eilmuz.model.Enrollment;
import uz.eilmuz.model.User;
import java.util.List;
import java.util.Optional;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    List<Enrollment> findByStudent(User student);
    Optional<Enrollment> findByStudentAndCourse(User student, Course course);
    List<Enrollment> findByCourse(Course course);
    boolean existsByStudentAndCourse(User student, Course course);
    long countByStudent(User student);
    long countByCourse(Course course);
}
