package shoppingmall.ankim.domain.viewRolling.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import shoppingmall.ankim.domain.product.dto.ProductListResponse;
import shoppingmall.ankim.domain.viewRolling.entity.RollingPeriod;
import shoppingmall.ankim.domain.viewRolling.repository.ViewRollingRepository;
import shoppingmall.ankim.global.response.ApiResponse;

@RestController
@RequiredArgsConstructor
public class ViewRollingController {
    private final ViewRollingRepository viewRollingRepository;

    /**
     * 기간별 조회순 상품 50개 조회 API
     * @param categoryNo
     * @param period
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/categories/{categoryNo}/top-views/{period}")
    public ApiResponse<Page<ProductListResponse>> getTopViewedProducts(
            @PathVariable Long categoryNo,
            @PathVariable RollingPeriod period,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {

        return ApiResponse.ok(viewRollingRepository.getViewRollingProducts(categoryNo, period, PageRequest.of(page, size)));
    }
}
