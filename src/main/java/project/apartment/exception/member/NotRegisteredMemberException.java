package project.apartment.exception.member;

public class NotRegisteredMemberException extends RuntimeException {
    public NotRegisteredMemberException() {
    }

    public NotRegisteredMemberException(String message) {
        super(message);
    }

    public NotRegisteredMemberException(String message, Throwable cause) {
        super(message, cause);
    }
}
