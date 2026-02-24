package uz.eilmuz.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class CourseDto {
    @NotBlank
    private String title;

    private String description;

    private MultipartFile thumbnail;
}
