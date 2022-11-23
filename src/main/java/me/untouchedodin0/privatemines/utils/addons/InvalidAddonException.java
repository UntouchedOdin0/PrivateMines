package me.untouchedodin0.privatemines.utils.addons;

/**
 * Thrown when attempting to load an invalid Addon file
 */
public class InvalidAddonException extends Exception {
    private static final long serialVersionUID = -8242141640709409544L;

    /**
     * Constructs a new InvalidAddonException based on the given Exception
     *
     * @param cause Exception that triggered this Exception
     */
    public InvalidAddonException(final Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new InvalidAddonException
     */
    public InvalidAddonException() {

    }

    /**
     * Constructs a new InvalidAddonException with the specified detail
     * message and cause.
     *
     * @param message the detail message (which is saved for later retrieval
     *     by the getMessage() method).
     * @param cause the cause (which is saved for later retrieval by the
     *     getCause() method). (A null value is permitted, and indicates that
     *     the cause is nonexistent or unknown.)
     */
    public InvalidAddonException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new InvalidAddonException with the specified detail
     * message
     *
     * @param message TThe detail message is saved for later retrieval by the
     *     getMessage() method.
     */
    public InvalidAddonException(final String message) {
        super(message);
    }
}