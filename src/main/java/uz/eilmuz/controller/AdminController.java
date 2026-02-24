package uz.eilmuz.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import uz.eilmuz.model.CourseStatus;
import uz.eilmuz.model.Role;
import uz.eilmuz.model.User;
import uz.eilmuz.repository.CourseRepository;
import uz.eilmuz.repository.EnrollmentRepository;
import uz.eilmuz.repository.ModeratorActionRepository;
import uz.eilmuz.service.CourseService;
import uz.eilmuz.service.UserService;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final CourseService courseService;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final ModeratorActionRepository moderatorActionRepository;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        long totalInstructors = userService.countByRole(Role.INSTRUCTOR);
        long totalStudents = userService.countByRole(Role.STUDENT);
        long totalApprovedCourses = courseService.countByStatus(CourseStatus.APPROVED);
        long totalPendingCourses = courseService.countByStatus(CourseStatus.PENDING);

        List<User> instructors = userService.findByRole(Role.INSTRUCTOR);
        Map<User, Long> instructorCourseCounts = new LinkedHashMap<>();
        for (User instructor : instructors) {
            instructorCourseCounts.put(instructor, courseRepository.countByInstructor(instructor));
        }

        List<User> students = userService.findByRole(Role.STUDENT);
        Map<User, Long> studentEnrollmentCounts = new LinkedHashMap<>();
        for (User student : students) {
            studentEnrollmentCounts.put(student, enrollmentRepository.countByStudent(student));
        }

        var recentActions = moderatorActionRepository.findTop10ByOrderByActionAtDesc();

        model.addAttribute("totalInstructors", totalInstructors);
        model.addAttribute("totalStudents", totalStudents);
        model.addAttribute("totalApprovedCourses", totalApprovedCourses);
        model.addAttribute("totalPendingCourses", totalPendingCourses);
        model.addAttribute("instructorCourseCounts", instructorCourseCounts);
        model.addAttribute("studentEnrollmentCounts", studentEnrollmentCounts);
        model.addAttribute("recentActions", recentActions);
        return "admin/dashboard";
    }
}
