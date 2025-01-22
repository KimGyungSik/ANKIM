package shoppingmall.ankim;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import shoppingmall.ankim.domain.category.dto.CategoryResponse;
import shoppingmall.ankim.domain.category.service.query.CategoryQueryService;
import shoppingmall.ankim.domain.product.repository.query.helper.Condition;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final CategoryQueryService categoryQueryService;
    @GetMapping("/")
    public String home(Model model) {
        List<CategoryResponse> middleCategories = categoryQueryService.retrieveMiddleCategories();
        model.addAttribute("middleCategories", middleCategories);

        List<CategoryResponse> handmadeCategories = categoryQueryService.fetchHandmadeCategories();
        model.addAttribute("handmadeCategories", handmadeCategories);

        List<CategoryResponse> subCategories = categoryQueryService.fetchAllSubCategories();
        model.addAttribute("subCategories", subCategories);

        return "home";
    }

    @GetMapping("/dashboard")
    public String dashBaord() {
        return "base";
    }
}
