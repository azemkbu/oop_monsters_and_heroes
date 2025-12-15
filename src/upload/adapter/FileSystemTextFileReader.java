package upload.adapter;

import upload.base.TextFileReader;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Adapter implementation backed by the local filesystem.
 */
public final class FileSystemTextFileReader implements TextFileReader {
    @Override
    public List<String> readDataLines(String filePath) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader br = Files.newBufferedReader(Paths.get(filePath))) {
            // skip header line
            String line = br.readLine();
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read file: " + filePath, e);
        }
        return lines;
    }
}


