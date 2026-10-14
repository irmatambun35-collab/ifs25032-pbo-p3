package domain.exception;

/**
 * Exception yang dilempar saat terjadi kegagalan validasi aturan bisnis atau input domain.
 * Pesan exception sudah siap ditampilkan kepada pengguna.
 */
public class ValidationException extends DomainException {
    public ValidationException(String message) {
        super(message);
    }
}
