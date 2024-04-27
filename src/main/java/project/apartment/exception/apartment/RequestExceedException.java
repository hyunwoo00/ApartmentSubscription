package project.apartment.exception.apartment;

public class RequestExceedException extends RuntimeException{
    public RequestExceedException() {
    }

    public RequestExceedException(String message) {
        super(message);
    }

    public RequestExceedException(String message, Throwable cause) {
        super(message, cause);
    }
}
