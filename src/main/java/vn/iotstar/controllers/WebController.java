package vn.iotstar.controllers;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {
 @GetMapping("/") public String home() { return "home"; }
 @GetMapping("/categories") public String categories() { return "category"; }
 @GetMapping("/products") public String products() { return "product"; }
}
