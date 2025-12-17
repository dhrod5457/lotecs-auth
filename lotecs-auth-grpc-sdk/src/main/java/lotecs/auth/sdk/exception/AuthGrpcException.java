package lotecs.auth.sdk.exception;

import io.grpc.Status;
import lombok.Getter;

@Getter
public class AuthGrpcException extends RuntimeException {

    private final Status.Code statusCode;
    private final String description;

    public AuthGrpcException(String message) {
        super(message);
        this.statusCode = Status.Code.INTERNAL;
        this.description = message;
    }

    public AuthGrpcException(String message, Throwable cause) {
        super(message, cause);
        this.statusCode = Status.Code.INTERNAL;
        this.description = message;
    }

    public AuthGrpcException(Status.Code statusCode, String description) {
        super(description);
        this.statusCode = statusCode;
        this.description = description;
    }

    public AuthGrpcException(Status.Code statusCode, String description, Throwable cause) {
        super(description, cause);
        this.statusCode = statusCode;
        this.description = description;
    }

    public static AuthGrpcException fromStatus(io.grpc.Status status) {
        return new AuthGrpcException(status.getCode(), status.getDescription());
    }

    public static AuthGrpcException fromStatusRuntimeException(io.grpc.StatusRuntimeException e) {
        return new AuthGrpcException(e.getStatus().getCode(), e.getStatus().getDescription(), e);
    }
}
