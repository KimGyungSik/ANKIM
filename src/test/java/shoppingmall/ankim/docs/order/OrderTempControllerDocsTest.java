package shoppingmall.ankim.docs.order;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithMockUser;
import shoppingmall.ankim.docs.RestDocsSupport;
import shoppingmall.ankim.domain.address.entity.member.MemberAddress;
import shoppingmall.ankim.domain.address.service.request.MemberAddressRegisterServiceRequest;
import shoppingmall.ankim.domain.cart.service.CartService;
import shoppingmall.ankim.domain.category.entity.Category;
import shoppingmall.ankim.domain.item.entity.Item;
import shoppingmall.ankim.domain.member.entity.Member;
import shoppingmall.ankim.domain.option.entity.OptionGroup;
import shoppingmall.ankim.domain.option.entity.OptionValue;
import shoppingmall.ankim.domain.order.controller.OrderTempController;
import shoppingmall.ankim.domain.order.dto.OrderResponse;
import shoppingmall.ankim.domain.order.dto.OrderTempResponse;
import shoppingmall.ankim.domain.order.entity.Order;
import shoppingmall.ankim.domain.order.service.OrderService;
import shoppingmall.ankim.domain.orderItem.entity.OrderItem;
import shoppingmall.ankim.domain.product.entity.Product;
import shoppingmall.ankim.domain.product.entity.ProductSellingStatus;
import shoppingmall.ankim.domain.security.helper.SecurityContextHelper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class OrderTempControllerDocsTest extends RestDocsSupport {

    private final OrderService orderService = mock(OrderService.class);
    private final CartService cartService = mock(CartService.class);
    private final SecurityContextHelper securityContextHelper = mock(SecurityContextHelper.class);

    private static final String ACCESS_TOKEN = "example-access-token";
    private static final String REFRESH_TOKEN = "example-refresh-token";

    @Override
    protected Object initController() {
        return new OrderTempController(orderService, cartService, securityContextHelper);
    }

    @DisplayName("임시 주문 생성 API")
    @WithMockUser(username = "test@example.com", roles = {"USER"})
    @Test
    public void termsAgree() throws Exception {
        // given
        String loginId = "test@example.com";

        List<Long> request = List.of(1L, 5L);

        Category category = Category.builder()
                        .name("상의")
                        .subCategories(List.of(Category.builder().name("코트").build()))
                        .build();

        Product product = Product.builder()
                        .category(category)
                        .name("캐시미어 코트")
                        .desc("부드럽고 고급스러운 캐시미어 코트")
                        .discRate(10)
                        .origPrice(120000)
                        .qty(100)
                        .sellingStatus(ProductSellingStatus.SELLING)
                        .build();

        // 옵션 그룹 생성 및 저장
        OptionGroup colorGroup = OptionGroup.builder()
                        .name("컬러")
                        .product(product)
                        .build();

        OptionGroup sizeGroup = OptionGroup.builder()
                        .name("사이즈")
                        .product(product)
                        .build();

        OptionValue black = OptionValue.builder()
                .no(1L)
                .name("블랙")
                .colorCode("#000000")
                .optionGroup(colorGroup)
                .build();
        OptionValue gray = OptionValue.builder()
                .no(2L)
                .name("그레이")
                .colorCode("#808080")
                .optionGroup(colorGroup)
                .build();
        OptionValue medium = OptionValue.builder()
                .no(3L)
                .name("M")
                .optionGroup(sizeGroup)
                .build();
        OptionValue large = OptionValue.builder()
                .no(4L)
                .name("L")
                .optionGroup(sizeGroup)
                .build();

        // 테스트용 OrderItem 생성
        List<OrderItem> orderItems = List.of(
                OrderItem.builder()
                        .item(Item.builder()
                                .no(1L)
                                .name("색상: 블랙, 사이즈: M")
                                .optionValues(List.of(black, medium))
                                .code("P001-BLK-M")
                                .addPrice(2000)
                                .qty(100)
                                .safQty(10)
                                .maxQty(5)
                                .minQty(1)
                                .product(product)
                                .build())
                        .qty(2)
                        .price(200)
                        .shipFee(10)
                        .discPrice(20)
                        .build(),
                OrderItem.builder()
                        .item(Item.builder()
                                .no(2L)
                                .name("색상: 블랙, 사이즈: L")
                                .optionValues(List.of(black, large))
                                .code("P001-BLK-L")
                                .addPrice(3000)
                                .qty(100)
                                .safQty(5)
                                .maxQty(3)
                                .minQty(1)
                                .product(product)
                                .build())
                        .qty(1)
                        .price(200)
                        .shipFee(15)
                        .discPrice(25)
                        .build()
        );

        // 테스트용 Member 생성
        Member member = Member.builder()
                .no(1L)
                .loginId("test@example.com")
                .build();

        // 테스트용 MemberAddress 생성
        MemberAddress existingAddress = MemberAddress.builder()
                .member(member)
                .baseAddress(MemberAddressRegisterServiceRequest.builder()
                        .zipCode(12345)
                        .addressMain("서울특별시 강남구")
                        .addressDetail("101호")
                        .build()
                        .toBaseAddress())
                .phoneNumber(member.getPhoneNum())
                .defaultAddressYn("Y")
                .build();

        MemberAddress anotherAddress = MemberAddress.builder()
                .member(member)
                .baseAddress(MemberAddressRegisterServiceRequest.builder()
                        .zipCode(98765)
                        .addressMain("제주특별시 서귀포구")
                        .addressDetail("00아파트 101호")
                        .build()
                        .toBaseAddress())
                .phoneNumber(member.getPhoneNum())
                .emergencyPhoneNumber("010-8282-8282")
                .defaultAddressYn("N")
                .build();

        List<MemberAddress> addresses = List.of(existingAddress, anotherAddress);

        // 테스트용 Order 생성
        Order order = Order.tempCreate(orderItems, member, LocalDateTime.now());
        order.setOrdNo(String.valueOf(UUID.randomUUID()));
        order.setOrdCode("ORD20241203-5261724");

        // 테스트용 OrderResponse 생성
        OrderTempResponse response = OrderTempResponse.tempOf(order).withAddresses(addresses);

        given(securityContextHelper.getLoginId()).willReturn(loginId);
        given(orderService.createOrderTemp(eq(loginId), anyList())).willReturn(response);


        // when & then
        mockMvc.perform(post("/api/temp-order")
                        .header("access", ACCESS_TOKEN)
                        .cookie(new Cookie("refresh", REFRESH_TOKEN))
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("member-temp-order",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("[]").type(JsonFieldType.ARRAY)
                                        .description("장바구니 항목 번호 리스트")
                        ),
                        responseFields(
                                fieldWithPath("code").description("응답 코드 (예: 200은 성공)").type(JsonFieldType.NUMBER),
                                fieldWithPath("status").description("응답 상태 (예: 'OK')").type(JsonFieldType.STRING),
                                fieldWithPath("message").description("응답 메시지").type(JsonFieldType.STRING),
                                fieldWithPath("fieldErrors").description("필드 오류 목록").optional().type(JsonFieldType.ARRAY),
                                fieldWithPath("data.orderNo").description("주문 번호").type(JsonFieldType.STRING),
                                fieldWithPath("data.orderCode").description("주문 코드").type(JsonFieldType.STRING),
                                fieldWithPath("data.items[].itemId").description("아이템 번호").type(JsonFieldType.NUMBER),
                                fieldWithPath("data.items[].code").description("아이템 코드").type(JsonFieldType.STRING),
                                fieldWithPath("data.items[].name").description("아이템 이름").type(JsonFieldType.STRING),
                                fieldWithPath("data.items[].addPrice").description("추가 금액").type(JsonFieldType.NUMBER),
                                fieldWithPath("data.items[].qty").description("수량").type(JsonFieldType.NUMBER),
                                fieldWithPath("data.items[].safQty").description("안전 재고 수량").type(JsonFieldType.NUMBER),
                                fieldWithPath("data.items[].sellingStatus").description("판매 상태").optional().type(JsonFieldType.STRING),
                                fieldWithPath("data.items[].maxQty").description("최대 주문 수량").type(JsonFieldType.NUMBER),
                                fieldWithPath("data.items[].minQty").description("최소 주문 수량").type(JsonFieldType.NUMBER),
                                fieldWithPath("data.items[].optionValues[].optionValueNo").description("옵션 값 번호").type(JsonFieldType.NUMBER),
                                fieldWithPath("data.items[].optionValues[].valueName").description("옵션 값 이름").type(JsonFieldType.STRING),
                                fieldWithPath("data.items[].optionValues[].colorCode").description("옵션 색상 코드").optional().type(JsonFieldType.STRING),
                                fieldWithPath("data.items[].optionValues[].optionGroupNo").description("옵션 그룹 번호").optional().type(JsonFieldType.NUMBER),
                                fieldWithPath("data.items[].optionValues[].itemId").description("아이템 ID").optional().type(JsonFieldType.NUMBER),
                                fieldWithPath("data.delivery").description("배송 정보 입력 전 이므로 null값이 들어감").optional().type(JsonFieldType.OBJECT),
                                fieldWithPath("data.totalQty").description("총 주문 수량").type(JsonFieldType.NUMBER),
                                fieldWithPath("data.totalPrice").description("총 상품 금액").type(JsonFieldType.NUMBER),
                                fieldWithPath("data.totalShipFee").description("총 배송비").type(JsonFieldType.NUMBER),
                                fieldWithPath("data.totalDiscPrice").description("총 할인 금액").type(JsonFieldType.NUMBER),
                                fieldWithPath("data.payAmt").description("최종 결제 금액").type(JsonFieldType.NUMBER),
                                fieldWithPath("data.regDate").description("주문 등록일").type(JsonFieldType.ARRAY),
                                fieldWithPath("data.modDate").description("주문 상태 변경일").type(JsonFieldType.ARRAY),
                                fieldWithPath("data.orderStatus").description("주문 상태").type(JsonFieldType.STRING),
                                // 추가된 주소 정보 문서화
                                fieldWithPath("data.addresses").description("주소 목록").type(JsonFieldType.ARRAY),
                                fieldWithPath("data.addresses[].addressName").description("배송지명").optional().type(JsonFieldType.STRING),
                                fieldWithPath("data.addresses[].receiver").description("수령인").optional().type(JsonFieldType.STRING),
                                fieldWithPath("data.addresses[].zipCode").description("우편번호").type(JsonFieldType.NUMBER),
                                fieldWithPath("data.addresses[].addressMain").description("기본 주소").type(JsonFieldType.STRING),
                                fieldWithPath("data.addresses[].addressDetail").description("상세 주소").type(JsonFieldType.STRING),
                                fieldWithPath("data.addresses[].phoneNumber").description("기본 연락처").optional().type(JsonFieldType.STRING),
                                fieldWithPath("data.addresses[].emergencyPhoneNumber").description("비상 연락처").optional().type(JsonFieldType.STRING),
                                fieldWithPath("jwtError").description("JWT 인증 오류 여부").type(JsonFieldType.BOOLEAN).optional()
                        )
                ));
    }
}
