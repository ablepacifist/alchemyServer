package alchemy.api;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class HomeController {

    @GetMapping("/")
    public String home() {
        // right now, to just says that the api is working
        return "<html><head><title>AlchemyServer Home</title></head>" +
               "<body style='text-align: center; font-family: Arial, sans-serif;'>" +
               "<h1>Welcome to AlchemyServer!</h1>" +
               "<p>Your API is working and accessible. Alex is the best</p>" +
               "</body></html>";
    }
    
    @GetMapping("/health")
    public String health() {
        return "{\"status\":\"Alchemy Server is running\",\"service\":\"alchemy\"}";
    }
}