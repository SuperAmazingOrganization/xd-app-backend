package pl.vizja.xdbackend.supabase_storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@Service
public class PhotoService {

    @Value("${supabase.url:}")
    private String supabaseUrl;

    @Value("${supabase.key:}")
    private String supabaseKey;

    @Value("${supabase.bucket:}")
    private String bucket;

    @Value("${uploads.local-dir:./uploads}")
    private String localUploadsDir;

    @Value("${uploads.public-base:http://localhost:8080/uploads}")
    private String localPublicBase;

    private final RestTemplate restTemplate = new RestTemplate();

    public String uploadPhoto(MultipartFile file, String userId) throws IOException {
        if (supabaseUrl == null || supabaseUrl.isBlank()) {
            return saveLocally(file, userId);
        }
        return saveToSupabase(file, userId);
    }

    private String saveToSupabase(MultipartFile file, String userId) throws IOException {
        String fileName = userId + "/" + UUID.randomUUID() + "_" + file.getOriginalFilename();
        String uploadUrl = supabaseUrl + "/storage/v1/object/" + bucket + "/" + fileName;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + supabaseKey);
        headers.setContentType(MediaType.parseMediaType(file.getContentType()));

        HttpEntity<byte[]> request = new HttpEntity<>(file.getBytes(), headers);
        restTemplate.exchange(uploadUrl, HttpMethod.POST, request, String.class);

        return supabaseUrl + "/storage/v1/object/public/" + bucket + "/" + fileName;
    }

    private String saveLocally(MultipartFile file, String userId) throws IOException {
        Path userDir = Paths.get(localUploadsDir, userId);
        Files.createDirectories(userDir);

        String safeName = UUID.randomUUID() + "_" + sanitize(file.getOriginalFilename());
        Path target = userDir.resolve(safeName);
        file.transferTo(target);

        return localPublicBase + "/" + userId + "/" + safeName;
    }

    private static String sanitize(String name) {
        if (name == null) return "file";
        return name.replaceAll("[^A-Za-z0-9._-]", "_");
    }
}
