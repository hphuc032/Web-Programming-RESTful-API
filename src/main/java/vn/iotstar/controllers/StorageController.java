package vn.iotstar.controllers;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import vn.iotstar.services.StorageService;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;

@RestController @RequiredArgsConstructor
public class StorageController {
 private final StorageService storage;
 @GetMapping("/uploads/{folder}/{filename:.+}")
 public ResponseEntity<Resource> serve(@PathVariable String folder, @PathVariable String filename) throws IOException {
  Resource resource = storage.loadAsResource(folder + "/" + filename);
  String detected = Files.probeContentType(resource.getFile().toPath());
  MediaType type;
  try { type = detected == null ? MediaType.APPLICATION_OCTET_STREAM : MediaType.parseMediaType(detected); }
  catch (InvalidMediaTypeException e) { type = MediaType.APPLICATION_OCTET_STREAM; }
  return ResponseEntity.ok().cacheControl(CacheControl.maxAge(Duration.ofHours(1))).contentType(type).body(resource);
 }
}
