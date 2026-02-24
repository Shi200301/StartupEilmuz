package uz.eilmuz.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.eilmuz.dto.LessonDto;
import uz.eilmuz.model.Course;
import uz.eilmuz.model.Lesson;
import uz.eilmuz.repository.CourseRepository;
import uz.eilmuz.repository.LessonRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LessonService {

    private final LessonRepository lessonRepository;
    private final CourseRepository courseRepository;
    private final FileStorageService fileStorageService;

    public Lesson addLesson(LessonDto dto, Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found: " + courseId));
        String videoPath = null;
        if (dto.getVideoFile() != null && !dto.getVideoFile().isEmpty()) {
            videoPath = fileStorageService.storeFile(dto.getVideoFile(), "videos");
        }
        Lesson lesson = Lesson.builder()
                .course(course)
                .title(dto.getTitle())
                .description(dto.getDescription())
                .videoPath(videoPath)
                .orderNum(dto.getOrderNum() != null ? dto.getOrderNum() : 0)
                .build();
        return lessonRepository.save(lesson);
    }

    public long countCourseLessons(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found: " + courseId));
        return lessonRepository.countByCourse(course);
    }

    public Lesson getLessonById(Long id) {
        return lessonRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Lesson not found: " + id));
    }

    public List<Lesson> getCourseLessons(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found: " + courseId));
        return lessonRepository.findByCourseOrderByOrderNum(course);
    }
}
