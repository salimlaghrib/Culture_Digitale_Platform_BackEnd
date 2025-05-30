package salimlgh.culturedigitalplatform.service;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.stream.Stream;
@Service
public interface FileStorageService {
    String storePdf(MultipartFile file) throws IOException;
    Resource loadFile(String filename);
    void deleteFile(String filename);
    void init();
}