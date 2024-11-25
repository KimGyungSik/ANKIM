package shoppingmall.ankim.domain.item.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shoppingmall.ankim.domain.item.dto.ItemPreviewResponse;
import shoppingmall.ankim.domain.item.service.ItemService;
import shoppingmall.ankim.domain.option.dto.OptionGroupCreateRequest;
import shoppingmall.ankim.domain.option.service.request.OptionGroupCreateServiceRequest;
import shoppingmall.ankim.global.response.ApiResponse;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/items")
public class ItemController {
    private final ItemService itemService;

    // 옵션 생성하기 버튼 누를 시 품목을 미리 보여주는 API
    @PostMapping("/preview")
    public ApiResponse<List<ItemPreviewResponse>> previewOptionCombinations(
            @RequestBody @Valid List<OptionGroupCreateRequest> optionGroupRequests
    ) {
        // OptionGroupCreateRequest 리스트를 OptionGroupCreateServiceRequest 리스트로 변환
        List<OptionGroupCreateServiceRequest> serviceRequests = optionGroupRequests.stream()
                .map(OptionGroupCreateRequest::toServiceRequest)
                .toList();

        return ApiResponse.ok(itemService.generateOptionCombinations(serviceRequests));
    }

    // 미리보기에서 입력받은 데이터를 기반으로 품목을 저장하는 API
}
