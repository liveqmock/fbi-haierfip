package fip.common.exception;

/**
 * π´”√ø‚“Ï≥£
 * User: zhanrui
 * Date: 2010-6-7
 * Time: 15:52:12
 * To change this template use File | Settings | File Templates.
 */
public class PartsErrorException extends Exception {

    private int errorCode;

    public PartsErrorException(int errorCode) {
        setErrorCode(errorCode);
    }

    public PartsErrorException(int errorCode, Throwable cause) {
        super(cause);
        setErrorCode(errorCode);
    }

    public int getErrorCode() {

        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getMessage() {
        String str = "errorCode=" + getErrorCode()
                + " (" + super.getMessage() + ")";
        return str;
    }

}
