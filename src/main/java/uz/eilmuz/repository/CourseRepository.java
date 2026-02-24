package uz.eilmuz.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.eilmuz.model.Course;
import uz.eilmuz.model.CourseStatus;
import uz.eilmuz.model.User;
import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findByStatus(CourseStatus status);
    List<Course> findByInstructor(User instructor);
    List<Course> findByStatusAndInstructor(CourseStatus status, User instructor);
    long countByInstructor(User instructor);
    long countByStatus(CourseStatus status);
}
