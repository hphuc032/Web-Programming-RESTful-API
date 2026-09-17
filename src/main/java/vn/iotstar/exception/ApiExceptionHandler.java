package vn.iotstar.exception;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.*;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import vn.iotstar.dto.ApiResponse;
import java.util.LinkedHashMap;

@Slf4j @RestControllerAdvice
public class ApiExceptionHandler {
 @ExceptionHandler(ApiException.class)
 ResponseEntity<?> business(ApiException e) { return ResponseEntity.status(e.getStatus()).body(ApiResponse.error(e.getMessage(), null)); }
 @ExceptionHandler(BindException.class)
 ResponseEntity<?> validation(BindException e) {
  var errors = new LinkedHashMap<String,String>();
  e.getBindingResult().getFieldErrors().forEach(x -> errors.putIfAbsent(x.getField(), x.isBindingFailure() ? "Giá trị không hợp lệ" : x.getDefaultMessage()));
  return ResponseEntity.badRequest().body(ApiResponse.error("Dữ liệu không hợp lệ", errors));
 }
 @ExceptionHandler({ConstraintViolationException.class, MethodArgumentTypeMismatchException.class, MissingServletRequestParameterException.class})
 ResponseEntity<?> badRequest(Exception e) { return ResponseEntity.badRequest().body(ApiResponse.error("Tham số bị thiếu hoặc không hợp lệ", null)); }
 @ExceptionHandler(MaxUploadSizeExceededException.class)
 ResponseEntity<?> tooLarge(Exception e) { return ResponseEntity.status(413).body(ApiResponse.error("Ảnh tối đa 5 MB; tổng request tối đa 6 MB", null)); }
 @ExceptionHandler(MultipartException.class)
 ResponseEntity<?> multipart(Exception e) { return ResponseEntity.badRequest().body(ApiResponse.error("Dữ liệu upload không hợp lệ", null)); }
 @ExceptionHandler(DataIntegrityViolationException.class)
 ResponseEntity<?> conflict(Exception e) { return ResponseEntity.status(409).body(ApiResponse.error("Tên đã tồn tại hoặc dữ liệu đang được tham chiếu", null)); }
 @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
 ResponseEntity<?> method(Exception e) { return ResponseEntity.status(405).body(ApiResponse.error("HTTP method không được hỗ trợ", null)); }
 @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
 ResponseEntity<?> media(Exception e) { return ResponseEntity.status(415).body(ApiResponse.error("Sử dụng multipart/form-data cho thêm và sửa", null)); }
 @ExceptionHandler(Exception.class)
 ResponseEntity<?> unexpected(Exception e) {
  log.error("Unexpected request failure", e);
  return ResponseEntity.internalServerError().body(ApiResponse.error("Lỗi hệ thống. Vui lòng thử lại sau.", null));
 }
}
