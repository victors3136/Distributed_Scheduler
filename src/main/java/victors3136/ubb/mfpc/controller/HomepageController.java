package victors3136.ubb.mfpc.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*")
public class HomepageController {

    @GetMapping("/")
    public ResponseEntity<String> home() {
        return ResponseEntity.ok("Hello, MFPC!");
    }
}