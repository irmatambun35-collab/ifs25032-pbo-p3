package domain.exception;

/**
 * Checked exception dasar untuk hierarki domain model aplikasi Kontak Teman.
 */
public class DomainException extends Exception {
    public DomainException(String message) {
        super(message);
    }

    public DomainException(String message, Throwable cause) {
        super(message, cause);
    }
}
