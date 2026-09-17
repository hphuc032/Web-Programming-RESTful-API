package vn.iotstar.dto;
public record ApiResponse<T>(Boolean status, String message, T body) {
 public static <T> ApiResponse<T> ok(String message, T body) { return new ApiResponse<>(true, message, body); }
 public static ApiResponse<Object> error(String message, Object body) { return new ApiResponse<>(false, message, body); }
}
