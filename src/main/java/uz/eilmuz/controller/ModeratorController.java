package uz.eilmuz.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import uz.eilmuz.service.CourseService;
import uz.eilmuz.service.UserService;

@Controller
@RequestMapping("/moderator")
@PreAuthorize("hasRole('MODERATOR')")
@RequiredArgsConstructor
public class ModeratorController {

    private final CourseService courseService;
    private final UserService userService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("pendingCourses", courseService.getPendingCourses());
        return "moderator/dashboard";
    }

    @PostMapping("/courses/{id}/approve")
    public String approveCourse(@PathVariable Long id,
                                @AuthenticationPrincipal UserDetails userDetails) {
        var moderator = userService.findByEmail(userDetails.getUsername());
        courseService.approveCourse(id, moderator);
        return "redirect:/moderator/dashboard?approved=true";
    }

    @PostMapping("/courses/{id}/reject")
    public String rejectCourse(@PathVariable Long id,
                               @RequestParam(defaultValue = "") String note,
                               @AuthenticationPrincipal UserDetails userDetails) {
        var moderator = userService.findByEmail(userDetails.getUsername());
        courseService.rejectCourse(id, note, moderator);
        return "redirect:/moderator/dashboard?rejected=true";
    }
}
