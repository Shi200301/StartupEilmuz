package uz.eilmuz.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.eilmuz.model.User;
import uz.eilmuz.model.Watermark;
import uz.eilmuz.repository.WatermarkRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WatermarkService {

    private final WatermarkRepository watermarkRepository;

    public Watermark getOrCreateWatermark(User student) {
        return watermarkRepository.findByStudent(student)
                .orElseGet(() -> createWatermark(student));
    }

    private Watermark createWatermark(User student) {
        String displayText = buildDisplayText(student);
        Watermark wm = Watermark.builder()
                .student(student)
                .uniqueToken(UUID.randomUUID().toString())
                .displayText(displayText)
                .build();
        return watermarkRepository.save(wm);
    }

    private String buildDisplayText(User student) {
        String firstName = student.getFirstName() != null ? student.getFirstName() : "";
        String lastInitial = (student.getLastName() != null && !student.getLastName().isEmpty())
                ? student.getLastName().charAt(0) + "." : "";
        String email = student.getEmail();
        String maskedEmail = maskEmail(email);
        return firstName + " " + lastInitial + " " + maskedEmail;
    }

    private String maskEmail(String email) {
        if (email == null || !email.contains("@")) return email;
        String[] parts = email.split("@");
        String localPart = parts[0];
        String domain = parts[1];
        if (localPart.length() <= 3) {
            return localPart + "***@" + domain;
        }
        return localPart.substring(0, 3) + "***@" + domain;
    }
}
