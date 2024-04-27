package project.apartment.exception.region;

public class RegionExceedException extends RuntimeException {
    public RegionExceedException() {
    }

    public RegionExceedException(String message) {
        super(message);
    }

    public RegionExceedException(String message, Throwable cause) {
        super(message, cause);
    }
}
