package shoppingmall.ankim;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shoppingmall.ankim.global.response.ApiResponse;

@RestController()
@RequiredArgsConstructor
public class HelloController {

    @GetMapping("/")
    public String hello() {
        return "hello";
    }

}
