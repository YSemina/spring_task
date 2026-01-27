package y.semina.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
public class PublicController {

    @GetMapping
    public ResponseEntity<String> publicMessage() {
        return ResponseEntity.ok("Вы попали на общедоступную станицу");
    }

}
