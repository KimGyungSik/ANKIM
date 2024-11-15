package shoppingmall.ankim;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shoppingmall.ankim.global.response.ApiResponse;

@RestController()
@RequiredArgsConstructor
@RequestMapping("/api")
public class HelloController {

    @GetMapping("/hello")
    public ApiResponse<String> hello() {
        return ApiResponse.ok("hello");
    }

}
