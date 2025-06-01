package alchemy.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "<html><head><title>AlchemyServer Home</title></head>" +
               "<body style='text-align: center; font-family: Arial, sans-serif;'>" +
               "<h1>Welcome to AlchemyServer!</h1>" +
               "<p>Your API is working and accessible.</p>" +
               "</body></html>";
    }
}
