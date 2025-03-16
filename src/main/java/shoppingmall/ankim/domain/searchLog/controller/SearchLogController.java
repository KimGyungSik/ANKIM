package shoppingmall.ankim.domain.searchLog.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import shoppingmall.ankim.domain.searchLog.service.SearchLogService;
import shoppingmall.ankim.global.response.ApiResponse;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class SearchLogController {
    private final SearchLogService searchLogService;

    @GetMapping("/searchLog")
    public ApiResponse<List<String>> findTop10Keywords() {
        return ApiResponse.ok(searchLogService.getPopularKeywords(10));
    }
}
