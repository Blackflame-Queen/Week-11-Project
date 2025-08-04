package projects.exception;

/**
 * Unchecked exception for DIY project app errors
 * Note: @SuppressWarnings("serial") skips the serialization ID warning since we don’t need it
 */
@SuppressWarnings("serial")
public class DbException extends RuntimeException {
private static final long serialVersionUID = 1L;
  /**
   * Builds exception with a message
   * @param message What went wrong
   */
  public DbException(String message) {
    super(message);
  }

  /**
   * Builds exception with a cause
   * @param cause Root error
   */
  public DbException(Throwable cause) {
    super(cause);
  }

  /**
   * Builds exception with message and cause
   * @param message What went wrong
   * @param cause Root error
   */
  public DbException(String message, Throwable cause) {
    super(message, cause);
  }
}