package ui.formatter;

/**
 * A single rendered line along with its intended output style.
 */
public final class RenderedLine {
    private final LineKind kind;
    private final String text;

    public RenderedLine(LineKind kind, String text) {
        if (kind == null) {
            throw new IllegalArgumentException("kind must not be null");
        }
        if (text == null) {
            throw new IllegalArgumentException("text must not be null");
        }
        this.kind = kind;
        this.text = text;
    }

    public LineKind getKind() {
        return kind;
    }

    public String getText() {
        return text;
    }
}


