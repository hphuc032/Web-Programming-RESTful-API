package vn.iotstar.services.impl;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import vn.iotstar.configs.StorageProperties;
import vn.iotstar.exception.ApiException;
import vn.iotstar.services.StorageService;
import java.io.*;
import java.nio.file.*;
import java.util.*;

@Service @RequiredArgsConstructor
public class FileSystemStorageService implements StorageService {
 private final StorageProperties properties;
 private Path root() { return Paths.get(properties.getLocation()).toAbsolutePath().normalize(); }
 @Override public void init() {
  try { Files.createDirectories(root()); }
  catch (IOException e) { throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Không thể tạo thư mục upload"); }
 }
 @Override public String store(MultipartFile file, String folder) {
  if (file == null || file.isEmpty()) return null;
  if (file.getContentType() == null || !file.getContentType().toLowerCase(Locale.ROOT).startsWith("image/"))
   throw new ApiException(HttpStatus.BAD_REQUEST, "Chỉ chấp nhận file ảnh");
  String original = Optional.ofNullable(file.getOriginalFilename()).orElse("");
  String safeName = Paths.get(original).getFileName().toString();
  int dot = safeName.lastIndexOf('.');
  String extension = dot >= 0 ? safeName.substring(dot).toLowerCase(Locale.ROOT) : "";
  if (!extension.matches("\\.(jpg|jpeg|png|gif|webp)"))
   throw new ApiException(HttpStatus.BAD_REQUEST, "Ảnh phải có định dạng JPG, PNG, GIF hoặc WEBP");
  String normalizedFolder = folder.replaceAll("[^a-zA-Z0-9_-]", "");
  String relative = normalizedFolder + "/" + UUID.randomUUID() + extension;
  Path target = root().resolve(relative).normalize();
  if (!target.startsWith(root())) throw new ApiException(HttpStatus.BAD_REQUEST, "Tên file không hợp lệ");
  try {
   Files.createDirectories(target.getParent());
   try (InputStream input = file.getInputStream()) { Files.copy(input, target, StandardCopyOption.REPLACE_EXISTING); }
   return relative.replace('\\', '/');
  } catch (IOException e) { throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Không thể lưu ảnh"); }
 }
 @Override public Path load(String filename) {
  Path file = root().resolve(filename).normalize();
  if (!file.startsWith(root())) throw new ApiException(HttpStatus.BAD_REQUEST, "Đường dẫn ảnh không hợp lệ");
  return file;
 }
 @Override public Resource loadAsResource(String filename) {
  try {
   Resource resource = new UrlResource(load(filename).toUri());
   if (resource.exists() && resource.isReadable()) return resource;
  } catch (Exception ignored) { }
  throw ApiException.notFound("Ảnh");
 }
 @Override public void delete(String filename) {
  if (filename == null || filename.isBlank()) return;
  try { Files.deleteIfExists(load(filename)); }
  catch (IOException ignored) { }
 }
}
