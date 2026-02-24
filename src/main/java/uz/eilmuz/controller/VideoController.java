package uz.eilmuz.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import uz.eilmuz.model.Lesson;
import uz.eilmuz.model.User;
import uz.eilmuz.model.Watermark;
import uz.eilmuz.service.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.util.Map;

@Controller
@RequestMapping("/video")
@RequiredArgsConstructor
public class VideoController {

    private static final long VIDEO_CHUNK_SIZE = 1024 * 1024; // 1MB

    private final LessonService lessonService;
    private final EnrollmentService enrollmentService;
    private final UserService userService;
    private final WatermarkService watermarkService;
    private final FileStorageService fileStorageService;

    @GetMapping("/watch/{lessonId}")
    public String watchVideo(@PathVariable Long lessonId,
                             @AuthenticationPrincipal UserDetails userDetails,
                             Model model) {
        User student = userService.findByEmail(userDetails.getUsername());
        Lesson lesson = lessonService.getLessonById(lessonId);
        if (!enrollmentService.isEnrolled(student, lesson.getCourse().getId())) {
            return "redirect:/student/courses/" + lesson.getCourse().getId() + "?notEnrolled=true";
        }
        model.addAttribute("lesson", lesson);
        model.addAttribute("course", lesson.getCourse());
        return "video/player";
    }

    @GetMapping("/stream/{lessonId}")
    public ResponseEntity<Resource> streamVideo(@PathVariable Long lessonId,
                                                @RequestHeader(value = "Range", required = false) String rangeHeader,
                                                @AuthenticationPrincipal UserDetails userDetails) throws IOException {
        User student = userService.findByEmail(userDetails.getUsername());
        Lesson lesson = lessonService.getLessonById(lessonId);
        if (!enrollmentService.isEnrolled(student, lesson.getCourse().getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        if (lesson.getVideoPath() == null) {
            return ResponseEntity.notFound().build();
        }
        Path videoPath = fileStorageService.getFilePath(lesson.getVideoPath());
        Resource resource = new FileSystemResource(videoPath);
        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }
        String contentType = determineContentType(lesson.getVideoPath());
        long fileLength = resource.contentLength();

        if (rangeHeader != null) {
            return buildRangeResponse(resource, rangeHeader, fileLength, contentType);
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline")
                .contentLength(fileLength)
                .body(resource);
    }

    private ResponseEntity<Resource> buildRangeResponse(Resource resource, String rangeHeader,
                                                         long fileLength, String contentType) throws IOException {
        String rangeValue = rangeHeader.replace("bytes=", "");
        String[] rangeParts = rangeValue.split("-");
        long start = Long.parseLong(rangeParts[0]);
        long end = rangeParts.length > 1 && !rangeParts[1].isEmpty()
                ? Long.parseLong(rangeParts[1])
                : Math.min(start + VIDEO_CHUNK_SIZE - 1, fileLength - 1);

        long contentLength = end - start + 1;
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Range", "bytes " + start + "-" + end + "/" + fileLength);
        headers.add(HttpHeaders.ACCEPT_RANGES, "bytes");
        headers.setContentType(MediaType.parseMediaType(contentType));
        headers.setContentLength(contentLength);

        @SuppressWarnings("resource")
        RandomAccessFile raf = new RandomAccessFile(resource.getFile(), "r");
        raf.seek(start);
        InputStream slicedStream = new InputStream() {
            long remaining = contentLength;

            @Override
            public int read() throws IOException {
                if (remaining <= 0) return -1;
                remaining--;
                return raf.read();
            }

            @Override
            public int read(byte[] b, int off, int len) throws IOException {
                if (remaining <= 0) return -1;
                int toRead = (int) Math.min(len, remaining);
                int bytesRead = raf.read(b, off, toRead);
                if (bytesRead > 0) remaining -= bytesRead;
                return bytesRead;
            }

            @Override
            public void close() throws IOException {
                raf.close();
            }
        };

        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                .headers(headers)
                .body(new InputStreamResource(slicedStream));
    }

    private String determineContentType(String path) {
        if (path.endsWith(".mp4")) return "video/mp4";
        if (path.endsWith(".webm")) return "video/webm";
        if (path.endsWith(".ogg")) return "video/ogg";
        if (path.endsWith(".avi")) return "video/x-msvideo";
        return "video/mp4";
    }

    @GetMapping("/watermark")
    @ResponseBody
    public ResponseEntity<Map<String, String>> getWatermark(@AuthenticationPrincipal UserDetails userDetails) {
        User student = userService.findByEmail(userDetails.getUsername());
        Watermark watermark = watermarkService.getOrCreateWatermark(student);
        return ResponseEntity.ok(Map.of(
                "text", watermark.getDisplayText(),
                "token", watermark.getUniqueToken()
        ));
    }
}
