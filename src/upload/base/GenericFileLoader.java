package upload.base;

import java.util.ArrayList;
import java.util.List;

public final class GenericFileLoader {

    private GenericFileLoader() {
    }

    public static <T> List<T> load(String filePath, LineMapper<T> mapper) {
        List<T> result = new ArrayList<>();
        for (String line : TextFileUtils.readDataLines(filePath)) {
            String[] parts = line.split("\\s+");
            if (parts.length == 0) {
                continue;
            }
            result.add(mapper.map(parts));
        }
        return result;
    }
}
