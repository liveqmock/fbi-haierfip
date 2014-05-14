package fip.common.exception;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 2010-6-7
 * Time: 15:49:42
 * To change this template use File | Settings | File Templates.
 */
public class SystemFatalException extends RuntimeException {
    public SystemFatalException(String message) {
        super(message);
    }

    public SystemFatalException(String message, Throwable cause) {
        super(message, cause);
    }
}