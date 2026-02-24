package uz.eilmuz.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {

    @GetMapping("/dashboard")
    public String dashboard(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            return "redirect:/auth/login";
        }
        if (auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            return "redirect:/admin/dashboard";
        } else if (auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_INSTRUCTOR"))) {
            return "redirect:/instructor/dashboard";
        } else if (auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_MODERATOR"))) {
            return "redirect:/moderator/dashboard";
        } else if (auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_STUDENT"))) {
            return "redirect:/student/dashboard";
        }
        return "redirect:/auth/login";
    }

    @GetMapping("/")
    public String home(Authentication auth) {
        if (auth != null && auth.isAuthenticated()) {
            return "redirect:/dashboard";
        }
        return "redirect:/auth/login";
    }
}
