package uz.eilmuz.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.eilmuz.model.Course;
import uz.eilmuz.model.Enrollment;
import uz.eilmuz.model.User;
import uz.eilmuz.repository.CourseRepository;
import uz.eilmuz.repository.EnrollmentRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;

    public Enrollment enrollStudent(User student, Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found: " + courseId));
        if (enrollmentRepository.existsByStudentAndCourse(student, course)) {
            throw new IllegalStateException("Already enrolled");
        }
        Enrollment enrollment = Enrollment.builder()
                .student(student)
                .course(course)
                .build();
        return enrollmentRepository.save(enrollment);
    }

    public List<Enrollment> getStudentEnrollments(User student) {
        return enrollmentRepository.findByStudent(student);
    }

    public boolean isEnrolled(User student, Long courseId) {
        Course course = courseRepository.findById(courseId).orElse(null);
        if (course == null) return false;
        return enrollmentRepository.existsByStudentAndCourse(student, course);
    }

    public List<Enrollment> getEnrolledStudents(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found: " + courseId));
        return enrollmentRepository.findByCourse(course);
    }

    public long countByStudent(User student) {
        return enrollmentRepository.countByStudent(student);
    }
}
