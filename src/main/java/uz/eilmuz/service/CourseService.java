package uz.eilmuz.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.eilmuz.dto.CourseDto;
import uz.eilmuz.model.*;
import uz.eilmuz.repository.CourseRepository;
import uz.eilmuz.repository.ModeratorActionRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final FileStorageService fileStorageService;
    private final ModeratorActionRepository moderatorActionRepository;

    public Course createCourse(CourseDto dto, User instructor) {
        String thumbnailPath = null;
        if (dto.getThumbnail() != null && !dto.getThumbnail().isEmpty()) {
            thumbnailPath = fileStorageService.storeFile(dto.getThumbnail(), "thumbnails");
        }
        Course course = Course.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .thumbnailPath(thumbnailPath)
                .instructor(instructor)
                .status(CourseStatus.PENDING)
                .build();
        return courseRepository.save(course);
    }

    public List<Course> getApprovedCourses() {
        return courseRepository.findByStatus(CourseStatus.APPROVED);
    }

    public List<Course> getPendingCourses() {
        return courseRepository.findByStatus(CourseStatus.PENDING);
    }

    public Course approveCourse(Long id, User moderator) {
        Course course = getCourseById(id);
        course.setStatus(CourseStatus.APPROVED);
        courseRepository.save(course);
        ModeratorAction action = ModeratorAction.builder()
                .moderator(moderator)
                .course(course)
                .action("APPROVED")
                .build();
        moderatorActionRepository.save(action);
        return course;
    }

    public Course rejectCourse(Long id, String note, User moderator) {
        Course course = getCourseById(id);
        course.setStatus(CourseStatus.REJECTED);
        course.setModeratorNote(note);
        courseRepository.save(course);
        ModeratorAction action = ModeratorAction.builder()
                .moderator(moderator)
                .course(course)
                .action("REJECTED")
                .note(note)
                .build();
        moderatorActionRepository.save(action);
        return course;
    }

    public List<Course> getInstructorCourses(User instructor) {
        return courseRepository.findByInstructor(instructor);
    }

    public Course getCourseById(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Course not found: " + id));
    }

    public long countByStatus(CourseStatus status) {
        return courseRepository.countByStatus(status);
    }
}
