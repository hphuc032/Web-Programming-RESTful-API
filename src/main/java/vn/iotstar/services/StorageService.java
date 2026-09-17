package vn.iotstar.services;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Path;

public interface StorageService {
 void init();
 String store(MultipartFile file, String folder);
 Path load(String filename);
 Resource loadAsResource(String filename);
 void delete(String filename);
}
