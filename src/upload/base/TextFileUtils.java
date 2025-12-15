package upload.base;

import java.util.List;

/**
 * Backward-compatible utility wrapper.
 * Prefer injecting {@link TextFileReader} directly for testability.
 */
public final class TextFileUtils {

    private TextFileUtils() {
    }

    public static List<String> readDataLines(String filePath) {
        throw new UnsupportedOperationException("Use TextFileReader adapter instead of TextFileUtils");
    }
}
