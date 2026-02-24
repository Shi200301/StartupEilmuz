package uz.eilmuz.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import uz.eilmuz.model.Course;
import uz.eilmuz.model.User;
import uz.eilmuz.service.CourseService;
import uz.eilmuz.service.EnrollmentService;
import uz.eilmuz.service.LessonService;
import uz.eilmuz.service.UserService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/student")
@PreAuthorize("hasRole('STUDENT')")
@RequiredArgsConstructor
public class StudentController {

    private final CourseService courseService;
    private final EnrollmentService enrollmentService;
    private final LessonService lessonService;
    private final UserService userService;

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User student = userService.findByEmail(userDetails.getUsername());
        var enrollments = enrollmentService.getStudentEnrollments(student);
        var approvedCourses = courseService.getApprovedCourses();
        var lessonCountMap = new HashMap<Long, Long>();
        for (var enrollment : enrollments) {
            Long courseId = enrollment.getCourse().getId();
            lessonCountMap.put(courseId, lessonService.countCourseLessons(courseId));
        }
        model.addAttribute("enrollments", enrollments);
        model.addAttribute("approvedCourses", approvedCourses);
        model.addAttribute("student", student);
        model.addAttribute("lessonCountMap", lessonCountMap);
        return "student/dashboard";
    }

    @GetMapping("/courses")
    public String browseCourses(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User student = userService.findByEmail(userDetails.getUsername());
        List<Course> courses = courseService.getApprovedCourses();
        Map<Long, Boolean> enrolledMap = new HashMap<>();
        for (Course course : courses) {
            enrolledMap.put(course.getId(), enrollmentService.isEnrolled(student, course.getId()));
        }
        model.addAttribute("courses", courses);
        model.addAttribute("enrolledMap", enrolledMap);
        return "student/courses";
    }

    @PostMapping("/courses/{id}/enroll")
    public String enroll(@PathVariable Long id,
                         @AuthenticationPrincipal UserDetails userDetails) {
        User student = userService.findByEmail(userDetails.getUsername());
        try {
            enrollmentService.enrollStudent(student, id);
        } catch (IllegalStateException e) {
            // already enrolled - ignore
        }
        return "redirect:/student/courses/" + id;
    }

    @GetMapping("/courses/{id}")
    public String courseDetail(@PathVariable Long id,
                               @AuthenticationPrincipal UserDetails userDetails,
                               Model model) {
        User student = userService.findByEmail(userDetails.getUsername());
        Course course = courseService.getCourseById(id);
        boolean enrolled = enrollmentService.isEnrolled(student, id);
        model.addAttribute("course", course);
        model.addAttribute("enrolled", enrolled);
        if (enrolled) {
            model.addAttribute("lessons", lessonService.getCourseLessons(id));
        }
        return "student/course-detail";
    }
}
