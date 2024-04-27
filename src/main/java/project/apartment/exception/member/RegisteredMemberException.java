package project.apartment.exception.member;

public class RegisteredMemberException extends RuntimeException {
    public RegisteredMemberException() {
    }

    public RegisteredMemberException(String message) {
        super(message);
    }

    public RegisteredMemberException(String message, Throwable cause) {
        super(message, cause);
    }
}
