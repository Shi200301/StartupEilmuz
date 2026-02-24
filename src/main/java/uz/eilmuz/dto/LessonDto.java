package uz.eilmuz.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class LessonDto {
    @NotBlank
    private String title;

    private String description;

    private MultipartFile videoFile;

    private Integer orderNum;
}
