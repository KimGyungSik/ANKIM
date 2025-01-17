package shoppingmall.ankim.domain.product.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/product")
public class ProductController {
    @GetMapping("/admin/new")
    public String productForm(Model model) {
        return "/admin/product/registerForm";
    }
}
