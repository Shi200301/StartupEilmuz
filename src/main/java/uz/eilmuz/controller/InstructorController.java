package uz.eilmuz.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import uz.eilmuz.dto.CourseDto;
import uz.eilmuz.dto.LessonDto;
import uz.eilmuz.model.Course;
import uz.eilmuz.model.User;
import uz.eilmuz.service.CourseService;
import uz.eilmuz.service.LessonService;
import uz.eilmuz.service.UserService;

@Controller
@RequestMapping("/instructor")
@PreAuthorize("hasRole('INSTRUCTOR')")
@RequiredArgsConstructor
public class InstructorController {

    private final CourseService courseService;
    private final LessonService lessonService;
    private final UserService userService;

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User instructor = userService.findByEmail(userDetails.getUsername());
        model.addAttribute("courses", courseService.getInstructorCourses(instructor));
        return "instructor/dashboard";
    }

    @GetMapping("/courses/new")
    public String newCourseForm(Model model) {
        model.addAttribute("courseDto", new CourseDto());
        return "instructor/course-form";
    }

    @PostMapping("/courses")
    public String createCourse(@Valid @ModelAttribute("courseDto") CourseDto dto,
                               BindingResult result,
                               @AuthenticationPrincipal UserDetails userDetails,
                               Model model) {
        if (result.hasErrors()) {
            return "instructor/course-form";
        }
        User instructor = userService.findByEmail(userDetails.getUsername());
        courseService.createCourse(dto, instructor);
        return "redirect:/instructor/dashboard?created=true";
    }

    @GetMapping("/courses/{id}/lessons/new")
    public String newLessonForm(@PathVariable Long id, Model model) {
        Course course = courseService.getCourseById(id);
        model.addAttribute("course", course);
        model.addAttribute("lessonDto", new LessonDto());
        return "instructor/lesson-form";
    }

    @PostMapping("/courses/{id}/lessons")
    public String addLesson(@PathVariable Long id,
                            @Valid @ModelAttribute("lessonDto") LessonDto dto,
                            BindingResult result,
                            Model model) {
        if (result.hasErrors()) {
            model.addAttribute("course", courseService.getCourseById(id));
            return "instructor/lesson-form";
        }
        lessonService.addLesson(dto, id);
        return "redirect:/instructor/dashboard?lessonAdded=true";
    }
}
