package kafka.exception;

/**
 * @Author shuoxuan.fang
 * @Date 2024/3/18
 **/
public class GlspMqRuntimeException extends RuntimeException {
    public GlspMqRuntimeException() {
    }

    public GlspMqRuntimeException(String message) {
        super(message);
    }

    public GlspMqRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public GlspMqRuntimeException(Throwable cause) {
        super(cause);
    }

    public GlspMqRuntimeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
