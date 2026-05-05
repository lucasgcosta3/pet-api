package com.lucascosta.petapi.commons;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;

@Component
public class FileUtils {

    public String readResourceFile(String filename) {
        try {
            var resource = new ClassPathResource(filename);
            return new String(Files.readAllBytes(resource.getFile().toPath()));
        } catch (IOException e) {
            throw new RuntimeException("Failed to read resource file: " + filename, e);
        }
    }
}
