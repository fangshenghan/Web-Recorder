package cn.sharkmc.demo;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class FileService {

    private final ResourceLoader resourceLoader;

    public FileService(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public void copyResourceFile(String resourcePath, String targetPath) throws IOException {
        Resource resource = resourceLoader.getResource("classpath:" + resourcePath);
        try (InputStream inputStream = resource.getInputStream()) {
            Files.copy(inputStream, new File(targetPath).toPath());
        }
    }
}

