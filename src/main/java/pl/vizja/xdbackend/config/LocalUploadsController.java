package pl.vizja.xdbackend.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/uploads")
@Profile("h2file")
public class LocalUploadsController {

    private final Path baseDir;

    public LocalUploadsController(@Value("${uploads.local-dir:./uploads}") String localDir) {
        this.baseDir = Paths.get(localDir).toAbsolutePath().normalize();
    }

    @GetMapping("/{userId}/{filename:.+}")
    public ResponseEntity<Resource> serve(@PathVariable String userId, @PathVariable String filename) throws IOException {
        Path userDir = baseDir.resolve(userId).normalize();
        if (!userDir.startsWith(baseDir)) {
            return ResponseEntity.badRequest().build();
        }
        Path file = userDir.resolve(filename).normalize();
        if (!file.startsWith(userDir) || !Files.exists(file) || Files.isDirectory(file)) {
            return ResponseEntity.notFound().build();
        }

        Resource resource = new FileSystemResource(file);
        String contentType = Files.probeContentType(file);
        MediaType type = (contentType != null) ? MediaType.parseMediaType(contentType) : MediaType.APPLICATION_OCTET_STREAM;

        return ResponseEntity.ok()
                .contentType(type)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                .body(resource);
    }
}
