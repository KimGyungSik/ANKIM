package shoppingmall.ankim.docs.security;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithMockUser;
import shoppingmall.ankim.docs.RestDocsSupport;
import shoppingmall.ankim.domain.category.entity.Category;
import shoppingmall.ankim.domain.item.entity.Item;
import shoppingmall.ankim.domain.member.entity.Member;
import shoppingmall.ankim.domain.option.entity.OptionGroup;
import shoppingmall.ankim.domain.option.entity.OptionValue;
import shoppingmall.ankim.domain.order.controller.OrderTempController;
import shoppingmall.ankim.domain.order.dto.OrderResponse;
import shoppingmall.ankim.domain.order.entity.Order;
import shoppingmall.ankim.domain.order.service.OrderService;
import shoppingmall.ankim.domain.orderItem.entity.OrderItem;
import shoppingmall.ankim.domain.product.entity.Product;
import shoppingmall.ankim.domain.product.entity.ProductSellingStatus;
import shoppingmall.ankim.domain.security.controller.ReissueController;
import shoppingmall.ankim.domain.security.helper.SecurityContextHelper;
import shoppingmall.ankim.domain.security.service.ReissueService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ReissueControllerDocsTest extends RestDocsSupport {

    private final ReissueService reissueService = mock(ReissueService.class);

    private static final String OLD_ACCESS_TOKEN = "old-access-token";
    private static final String OLD_REFRESH_TOKEN = "old-refresh-token";

    @Override
    protected Object initController() {
        return new ReissueController(reissueService);
    }

    @DisplayName("임시 주문 생성 API")
    @WithMockUser(username = "test@example.com", roles = {"USER"})
    @Test
    public void termsAgree() throws Exception {
        // given
        Map<String, String> tokenResponse = Map.of(
                "access", "new-access-token",
                "refreshRedis", "new-refresh-token"
        );

        doNothing().when(reissueService).isAccessTokenExist(OLD_ACCESS_TOKEN); // Redis에 저장된 access token 확인
        given(reissueService.validateRefreshToken(OLD_ACCESS_TOKEN)).willReturn(OLD_REFRESH_TOKEN);
        given(reissueService.reissueToken(OLD_ACCESS_TOKEN, OLD_REFRESH_TOKEN)).willReturn(tokenResponse);

        // when & then
        mockMvc.perform(post("/reissue")
                        .header("access", OLD_ACCESS_TOKEN)
                        .cookie(new Cookie("refresh", OLD_REFRESH_TOKEN))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("reissue-token",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("access").description("기존 Access Token")
                        ),
                        requestCookies(
                                cookieWithName("refresh").description("기존 Refresh Token")
                        ),
                        responseFields(
                                fieldWithPath("code").description("응답 코드").type(JsonFieldType.NUMBER),
                                fieldWithPath("status").description("응답 상태").type(JsonFieldType.STRING),
                                fieldWithPath("message").description("응답 메시지").type(JsonFieldType.STRING),
                                fieldWithPath("fieldErrors").description("필드 오류 목록").optional().type(JsonFieldType.ARRAY),
                                fieldWithPath("data").description("응답 데이터").type(JsonFieldType.STRING),
                                fieldWithPath("jwtError").description("JWT 인증 오류 여부").type(JsonFieldType.BOOLEAN).optional()
                        )
                ));
    }
}
