package upload.base;

@FunctionalInterface
public interface LineMapper<T> {
    T map(String[] parts);
}
