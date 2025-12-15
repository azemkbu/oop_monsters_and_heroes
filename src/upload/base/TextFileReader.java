package upload.base;

import java.util.List;

/**
 * Backend I/O port: abstract file-reading capability for loaders.
 * The rest of the upload layer depends on this interface, not the filesystem.
 */
public interface TextFileReader {
    /**
     * Reads the raw data lines (excluding header/empty lines) from a file path.
     */
    List<String> readDataLines(String filePath);
}


