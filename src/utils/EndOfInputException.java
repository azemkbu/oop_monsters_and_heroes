package utils;

/**
 * Thrown when the input stream is closed (EOF) and the game cannot read more user input.
 * This is used to exit gracefully without printing a full stack trace.
 */
public class EndOfInputException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public EndOfInputException(String message) {
        super(message);
    }
}


