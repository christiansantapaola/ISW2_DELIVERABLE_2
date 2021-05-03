package GitSubSystem.Exception;


/**
 * exception given for error git related in GitHandler.
 */
public class GitHandlerException extends Exception {
    public GitHandlerException(String message, Throwable cause) {
        super(message, cause);
    }
}
