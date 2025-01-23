package shoppingmall.ankim;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import shoppingmall.ankim.domain.category.dto.CategoryResponse;
import shoppingmall.ankim.domain.category.service.query.CategoryQueryService;
import shoppingmall.ankim.domain.product.repository.query.helper.Condition;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final CategoryQueryService categoryQueryService;
    @GetMapping("/")
    public String home() {
        return "home";
    }

    @ResponseBody
    @GetMapping("/header")
    public Map<String, Object> getHeaderCategories() {
        Map<String, Object> response = new HashMap<>();
        response.put("middleCategories", categoryQueryService.retrieveMiddleCategories());
        response.put("handmadeCategories", categoryQueryService.fetchHandmadeCategories());
        response.put("subCategories", categoryQueryService.fetchAllSubCategories());
        return response;
    }

    @GetMapping("/dashboard")
    public String dashBaord() {
        return "base";
    }
}
