package project.apartment.exception.apartment;

public class RegisteredAPTException extends RuntimeException {
    public RegisteredAPTException() {
    }

    public RegisteredAPTException(String message) {
        super(message);
    }

    public RegisteredAPTException(String message, Throwable cause) {
        super(message, cause);
    }
}
