package vn.iotstar.configs;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.*;

@Configuration
public class OpenApiConfig {
 @Bean OpenAPI projectOpenApi() {
  return new OpenAPI().info(new Info().title("LTWeb Product Category API").version("1.0")
   .description("REST API CRUD Category và Product cho Project 1"));
 }
}
