package upload.base;

import upload.adapter.FileSystemTextFileReader;

/**
 * Composition root for upload layer default reader.
 * Keeps filesystem adapter out of core parsing logic.
 */
public final class DefaultTextFileReader {
    private static final TextFileReader INSTANCE = new FileSystemTextFileReader();

    private DefaultTextFileReader() {}

    public static TextFileReader get() {
        return INSTANCE;
    }
}


