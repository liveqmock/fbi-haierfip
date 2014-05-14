package fip.common.exception;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 2010-6-7
 * Time: 15:48:17
 * To change this template use File | Settings | File Templates.
 */

public class SystemErrorException extends RuntimeException {
    public SystemErrorException(String message) {
        super(message);
    }

    public SystemErrorException(String message, Throwable cause) {
        super(message, cause);
    }
}