package shoppingmall.ankim.docs.cart;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithMockUser;
import shoppingmall.ankim.docs.RestDocsSupport;
import shoppingmall.ankim.domain.cart.controller.CartApiController;
import shoppingmall.ankim.domain.cart.controller.request.AddToCartRequest;
import shoppingmall.ankim.domain.cart.dto.CartItemsResponse;
import shoppingmall.ankim.domain.cart.service.CartService;
import shoppingmall.ankim.domain.member.dto.MemberResponse;
import shoppingmall.ankim.domain.member.service.request.MemberRegisterServiceRequest;
import shoppingmall.ankim.domain.security.helper.SecurityContextHelper;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CartApiControllerDocsTest extends RestDocsSupport {

    private final CartService cartService = mock(CartService.class);
    private final SecurityContextHelper securityContextHelper = mock(SecurityContextHelper.class);

    private static final String ACCESS_TOKEN = "example-access-token";
    private static final String REFRESH_TOKEN = "example-refresh-token";

    @Override
    protected Object initController() {
        return new CartApiController(cartService, securityContextHelper);
    }

    @DisplayName("장바구니 상품 추가 API")
    @WithMockUser(username = "test@example.com", roles = {"USER"})
    @Test
    public void addToCart() throws Exception {
        // given
        AddToCartRequest request = AddToCartRequest.builder()
                .productNo(1L)
                .optionValueNoList(List.of(1L, 3L))
                .qty(3)
                .build();
        String loginId = "test@example.com";

        given(securityContextHelper.getLoginId()).willReturn(loginId);

        // when & then
        mockMvc.perform(post("/api/cart/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("access", ACCESS_TOKEN)
                        .cookie(new Cookie("refresh", REFRESH_TOKEN))
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(document("cart-add-item",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("productNo").type(JsonFieldType.NUMBER)
                                        .description("상품 번호"),
                                fieldWithPath("optionValueNoList").type(JsonFieldType.ARRAY)
                                        .description("옵션 번호"),
                                fieldWithPath("qty").type(JsonFieldType.NUMBER)
                                        .description("상품 수량")
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

    @DisplayName("장바구니 상품 조회 API")
    @WithMockUser(username = "test@example.com", roles = {"USER"})
    @Test
    public void getCartItems() throws Exception {
        // given
        List<CartItemsResponse> response = List.of(
                CartItemsResponse.builder()
                        .cartNo(1L)
                        .cartItemNo(1L)
                        .itemNo(2L)
                        .productName("카라 셔츠")
                        .itemName("색상: BLACK, 사이즈: L")
                        .thumbNailImgUrl("http://example.com/images/thumbnail_0.jpg")
                        .qty(3)
                        .sellingStatus(null)
                        .origPrice(10000)
                        .discRate(0)
                        .sellPrice(10000)
                        .addPrice(0)
                        .totalPrice(10000)
                        .freeShip("N")
                        .shipFee(2000)
                        .maxQty(5)
                        .minQty(1)
                        .itemQty(50)
                        .build(),
                CartItemsResponse.builder()
                        .cartNo(1L)
                        .cartItemNo(2L)
                        .itemNo(3L)
                        .productName("일자핏 슬랙스")
                        .itemName("색상: IVORY, 사이즈: M")
                        .thumbNailImgUrl("http://example.com/images/thumbnail_1.jpg")
                        .qty(2)
                        .sellingStatus(null)
                        .origPrice(30000)
                        .discRate(10)
                        .sellPrice(27000)
                        .addPrice(0)
                        .totalPrice(27000)
                        .freeShip("Y")
                        .shipFee(0)
                        .maxQty(10)
                        .minQty(1)
                        .itemQty(110)
                        .build()
        );
        String loginId = "test@example.com";

        given(securityContextHelper.getLoginId()).willReturn(loginId);

        given(cartService.getCartItems(anyString())).willReturn(response);

        // when & then
        mockMvc.perform(get("/api/cart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("access", ACCESS_TOKEN)
                        .cookie(new Cookie("refresh", REFRESH_TOKEN))
                        .content(objectMapper.writeValueAsString(response))
                )
                .andExpect(status().isOk())
                .andDo(document("cart-get-items",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("code").description("응답 코드").type(JsonFieldType.NUMBER),
                                fieldWithPath("status").description("응답 상태").type(JsonFieldType.STRING),
                                fieldWithPath("message").description("응답 메시지").type(JsonFieldType.STRING),
                                fieldWithPath("fieldErrors").description("필드 오류 목록").optional().type(JsonFieldType.ARRAY),
                                fieldWithPath("data.cartItems").description("장바구니 품목 목록").type(JsonFieldType.ARRAY).optional(),
                                fieldWithPath("data.freeShippingThreshold").description("무료배송 기준 금액").type(JsonFieldType.NUMBER),
                                fieldWithPath("data.cartItems[].cartNo").description("장바구니 번호").type(JsonFieldType.NUMBER),
                                fieldWithPath("data.cartItems[].cartItemNo").description("장바구니 품목 번호").type(JsonFieldType.NUMBER),
                                fieldWithPath("data.cartItems[].itemNo").description("품목 번호").type(JsonFieldType.NUMBER),
                                fieldWithPath("data.cartItems[].productName").description("상품명").type(JsonFieldType.STRING),
                                fieldWithPath("data.cartItems[].itemName").description("품목명").type(JsonFieldType.STRING),
                                fieldWithPath("data.cartItems[].thumbNailImgUrl").description("썸네일 이미지 주소").type(JsonFieldType.STRING),
                                fieldWithPath("data.cartItems[].qty").description("주문 수량").type(JsonFieldType.NUMBER),
                                fieldWithPath("data.cartItems[].itemQty").description("품목 재고량").type(JsonFieldType.NUMBER),
                                fieldWithPath("data.cartItems[].sellingStatus").description("판매 상태").optional().type(JsonFieldType.STRING),
                                fieldWithPath("data.cartItems[].origPrice").description("정상가격").type(JsonFieldType.NUMBER),
                                fieldWithPath("data.cartItems[].discRate").description("할인율").type(JsonFieldType.NUMBER),
                                fieldWithPath("data.cartItems[].sellPrice").description("판매가격").type(JsonFieldType.NUMBER),
                                fieldWithPath("data.cartItems[].addPrice").description("추가금액 (품목에 대한 추가금)").type(JsonFieldType.NUMBER),
                                fieldWithPath("data.cartItems[].totalPrice").description("판매가 (할인율 적용된 가격) + 추가금액").type(JsonFieldType.NUMBER),
                                fieldWithPath("data.cartItems[].freeShip").description("무료배송 여부").type(JsonFieldType.STRING),
                                fieldWithPath("data.cartItems[].shipFee").description("배송비").type(JsonFieldType.NUMBER),
                                fieldWithPath("data.cartItems[].maxQty").description("최대 구매 수량").type(JsonFieldType.NUMBER),
                                fieldWithPath("data.cartItems[].minQty").description("최소 구매 수량").type(JsonFieldType.NUMBER),
                                fieldWithPath("jwtError").description("JWT 인증 오류 여부").type(JsonFieldType.BOOLEAN).optional()
                        )
                ));
    }

    @DisplayName("장바구니 상품 선택 삭제 API")
    @WithMockUser(username = "test@example.com", roles = {"USER"})
    @Test
    public void deleteSelectedItems() throws Exception {
        // given
        List<Long> request = List.of(1L, 3L, 4L, 5L);
        String loginId = "test@example.com";

        given(securityContextHelper.getLoginId()).willReturn(loginId);

        // when & then
        mockMvc.perform(delete("/api/cart/items/selected")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("access", ACCESS_TOKEN)
                        .cookie(new Cookie("refresh", REFRESH_TOKEN))
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(document("cart-delete-selected-items",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("[]").type(JsonFieldType.ARRAY)
                                        .description("장바구니 품목 번호")
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

    @DisplayName("장바구니 상품 품절 삭제 API")
    @WithMockUser(username = "test@example.com", roles = {"USER"})
    @Test
    public void deleteSoldOutItems() throws Exception {
        // given
        String loginId = "test@example.com";

        given(securityContextHelper.getLoginId()).willReturn(loginId);

        // when & then
        mockMvc.perform(delete("/api/cart/items/sold-out")
                        .header("access", ACCESS_TOKEN)
                        .cookie(new Cookie("refresh", REFRESH_TOKEN))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("cart-delete-sold-out-items",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
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

    @DisplayName("장바구니 상품 수량 변경 API")
    @WithMockUser(username = "test@example.com", roles = {"USER"})
    @Test
    public void updateCartItemQuantity() throws Exception {
        // given
        Long cartItemNo = 1L;
        Integer qty = 4;
        String loginId = "test@example.com";

        given(securityContextHelper.getLoginId()).willReturn(loginId);

        // when & then
        mockMvc.perform(patch("/api/cart/items/{cartItemNo}?qty={qty}", cartItemNo, qty)
                        .header("access", ACCESS_TOKEN)
                        .cookie(new Cookie("refresh", REFRESH_TOKEN))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print()) // 요청과 응답을 출력
                .andExpect(status().isOk())
                .andDo(document("cart-update-item-qty",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("cartItemNo").description("수정할 장바구니 품목 번호")
                        ),
                        queryParameters(
                                parameterWithName("qty").description("수정할 장바구니 품목 수량")
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

    @DisplayName("장바구니 품목 개수 조회 API")
    @WithMockUser(username = "test@example.com", roles = {"USER"})
    @Test
    public void getCartItemsCount() throws Exception {
        // given
        String loginId = "test@example.com";
        int itemCount = 5;

        given(securityContextHelper.getLoginId()).willReturn(loginId);
        given(cartService.getCartItemCount(loginId)).willReturn(itemCount);

        // when & then
        mockMvc.perform(get("/api/cart/items/count")
                        .header("access", ACCESS_TOKEN)
                        .cookie(new Cookie("refresh", REFRESH_TOKEN))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemCount)))
                .andExpect(status().isOk())
                .andDo(document("cart-get-items-count",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("code").description("응답 코드").type(JsonFieldType.NUMBER),
                                fieldWithPath("status").description("응답 상태").type(JsonFieldType.STRING),
                                fieldWithPath("message").description("응답 메시지").type(JsonFieldType.STRING),
                                fieldWithPath("fieldErrors").description("필드 오류 목록").optional().type(JsonFieldType.ARRAY),
                                fieldWithPath("data.cartItemsCount").description("장바구니 품목 수").type(JsonFieldType.NUMBER),
                                fieldWithPath("jwtError").description("JWT 인증 오류 여부").type(JsonFieldType.BOOLEAN).optional()
                        )
                ));
    }

}
