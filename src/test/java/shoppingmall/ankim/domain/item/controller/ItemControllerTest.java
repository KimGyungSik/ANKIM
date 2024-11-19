package shoppingmall.ankim.domain.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import shoppingmall.ankim.domain.image.service.S3Service;
import shoppingmall.ankim.domain.item.dto.ItemPreviewResponse;
import shoppingmall.ankim.domain.item.service.ItemService;
import shoppingmall.ankim.domain.option.dto.OptionGroupCreateRequest;
import shoppingmall.ankim.domain.option.dto.OptionValueCreateRequest;
import shoppingmall.ankim.global.config.JpaAuditingConfig;
import shoppingmall.ankim.global.config.QuerydslConfig;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@WebMvcTest(controllers = ItemController.class)
@TestPropertySource(properties = "spring.sql.init.mode=never")
@ImportAutoConfiguration(exclude =  {QuerydslConfig.class, JpaAuditingConfig.class})
class ItemControllerTest {

    @MockBean
    private S3Service s3Service;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemService;

    @Autowired
    private ObjectMapper objectMapper;

    @DisplayName("옵션 생성 하기 버튼을 누르면 옵션별 품목을 미리볼 수 있다.")
    @Test
    void previewOptionCombinations() throws Exception {
        // given
        OptionValueCreateRequest blueOption = OptionValueCreateRequest.builder()
                .valueName("Blue")
                .colorCode("#0000FF")
                .build();
        OptionValueCreateRequest redOption = OptionValueCreateRequest.builder()
                .valueName("Red")
                .colorCode("#FF0000")
                .build();
        OptionGroupCreateRequest colorGroup = OptionGroupCreateRequest.builder()
                .groupName("색상")
                .optionValues(List.of(blueOption, redOption))
                .build();

        OptionValueCreateRequest smallOption = OptionValueCreateRequest.builder()
                .valueName("Small")
                .build();
        OptionValueCreateRequest largeOption = OptionValueCreateRequest.builder()
                .valueName("Large")
                .build();
        OptionGroupCreateRequest sizeGroup = OptionGroupCreateRequest.builder()
                .groupName("사이즈")
                .optionValues(List.of(smallOption, largeOption))
                .build();

        List<OptionGroupCreateRequest> optionGroupRequests = List.of(colorGroup, sizeGroup);
        List<ItemPreviewResponse> previewResponses = List.of(); // 예상된 미리보기 응답 (여기서는 빈 리스트로 설정)

        given(itemService.generateOptionCombinations(optionGroupRequests.stream().map(OptionGroupCreateRequest::toServiceRequest).toList()))
                .willReturn(previewResponses);

        String requestBody = objectMapper.writeValueAsString(optionGroupRequests);

        // when // then
        mockMvc.perform(
                        post("/api/items/preview")
                                .contentType("application/json")
                                .content(requestBody)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"))// 응답 HTTP 상태 코드 검증
                .andExpect(jsonPath("$.data").isArray());
    }

}