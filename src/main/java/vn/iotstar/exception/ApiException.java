package vn.iotstar.exception;
import lombok.Getter;
import org.springframework.http.HttpStatus;
@Getter
public class ApiException extends RuntimeException {
 private final HttpStatus status;
 public ApiException(HttpStatus status, String message) { super(message); this.status = status; }
 public static ApiException notFound(String name) { return new ApiException(HttpStatus.NOT_FOUND, name + " không tồn tại"); }
 public static ApiException conflict(String message) { return new ApiException(HttpStatus.CONFLICT, message); }
}
