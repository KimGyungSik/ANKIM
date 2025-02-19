package shoppingmall.ankim.domain.payment.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import shoppingmall.ankim.domain.address.controller.request.MemberAddressCreateRequest;
import shoppingmall.ankim.domain.cart.repository.CartRepository;
import shoppingmall.ankim.domain.delivery.dto.DeliveryCreateRequest;
import shoppingmall.ankim.domain.delivery.service.DeliveryService;
import shoppingmall.ankim.domain.image.service.S3Service;
import shoppingmall.ankim.domain.item.service.ItemService;
import shoppingmall.ankim.domain.order.repository.OrderRepository;
import shoppingmall.ankim.domain.payment.controller.port.PaymentService;
import shoppingmall.ankim.domain.payment.controller.request.PaymentCreateRequest;
import shoppingmall.ankim.domain.payment.dto.PaymentCancelResponse;
import shoppingmall.ankim.domain.payment.dto.PaymentFailResponse;
import shoppingmall.ankim.domain.payment.dto.PaymentResponse;
import shoppingmall.ankim.domain.payment.dto.PaymentSuccessResponse;
import shoppingmall.ankim.domain.payment.entity.PayType;
import shoppingmall.ankim.domain.payment.repository.PaymentRepository;
import shoppingmall.ankim.domain.payment.service.PaymentFacadeWithNamedLock;
import shoppingmall.ankim.global.config.JpaAuditingConfig;
import shoppingmall.ankim.global.config.QuerydslConfig;
import shoppingmall.ankim.global.config.RestTemplateConfig;
import shoppingmall.ankim.global.config.TossPaymentConfig;
import shoppingmall.ankim.global.config.lock.LockHandler;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@WebMvcTest(controllers = PaymentController.class)
@TestPropertySource(properties = "spring.sql.init.mode=never")
@AutoConfigureMockMvc(addFilters = false) // CSRF 비활성화
@ImportAutoConfiguration(exclude =  {QuerydslConfig.class, JpaAuditingConfig.class, RestTemplateConfig.class, TossPaymentConfig.class})
class PaymentControllerTest {

    @MockBean
    private S3Service s3Service;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PaymentFacadeWithNamedLock paymentFacadeWithNamedLock;

    @MockBean
    private LockHandler lockHandler;

    @MockBean
    private ItemService itemService;

    @MockBean
    private DeliveryService deliveryService;

    @MockBean
    private PaymentService paymentService;

    @MockBean
    private OrderRepository orderRepository;

    @MockBean
    private PaymentRepository paymentRepository;

    @MockBean
    private CartRepository cartRepository;

    @Test
    @DisplayName("결제 요청 성공 시 PaymentResponse를 반환한다")
    void requestTossPayment_shouldReturnPaymentResponse() throws Exception {
        // given
        PaymentCreateRequest request = PaymentCreateRequest.builder()
                .payType(PayType.CARD)
                .amount(50000)
                .orderName("ORD12345678")
                .build();

        DeliveryCreateRequest deliveryRequest = DeliveryCreateRequest.builder()
                .addressId(null)
                .courier("FastCourier")
                .delReq("문 앞에 놓아주세요")
                .build();

        MemberAddressCreateRequest addressRequest = MemberAddressCreateRequest.builder()
                .addressMain("서울특별시 강남구 테헤란로 123")
                .addressName("집")
                .addressDetail("1층")
                .zipCode(12345)
                .phoneNumber("010-1234-5678")
                .emergencyPhoneNumber("010-5678-1234")
                .defaultAddressYn("Y")
                .build();

        PaymentResponse mockResponse = PaymentResponse.builder()
                .orderName("ORD12345678")
                .amount(50000)
                .payType("카드")
                .successUrl("https://example.com/success")
                .failUrl("https://example.com/fail")
                .build();

        given(paymentFacadeWithNamedLock.createPaymentWithNamedLock(request.toServiceRequest(),
                deliveryRequest.toServiceRequest(),
                addressRequest.toServiceRequest())).willReturn(mockResponse);

        // when & then
        mockMvc.perform(post("/api/v1/payments/toss")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                            "paymentRequest": {
                                "payType": "CARD",
                                "amount": 50000,
                                "orderName": "ORD12345678"
                            },
                            "deliveryRequest": {
                                "addressId": null,
                                "courier": "FastCourier",
                                "delReq": "문 앞에 놓아주세요"
                            },
                            "addressRequest": {
                                "addressMain": "서울특별시 강남구 테헤란로 123",
                                "addressName": "집",
                                "addressDetail": "1층",
                                "zipCode": 12345,
                                "phoneNumber": "010-1234-5678",
                                "emergencyPhoneNumber": "010-5678-1234",
                                "defaultAddressYn": "Y"
                            }
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data").isNotEmpty());
    }

    @Test
    @DisplayName("결제 성공 시 PaymentSuccessResponse를 반환한다")
    void tossPaymentSuccess_shouldReturnPaymentSuccessResponse() throws Exception {
        // given
        PaymentSuccessResponse mockResponse = PaymentSuccessResponse.builder()
                .orderId("ORD12345678")
                .amount(String.valueOf(50000))
                .status("SUCCESS")
                .build();

        given(paymentFacadeWithNamedLock.toSuccessRequest("paymentKey123", "ORD12345678", 50000)).willReturn(mockResponse);

        // when & then
        mockMvc.perform(post("/api/v1/payments/toss/success")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                            "paymentKey": "paymentKey123",
                            "orderId": "ORD12345678",
                            "amount": "50000"
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK")) // 응답 HTTP 상태 코드 검증
                .andExpect(jsonPath("$.data").isNotEmpty());
    }

    @Test
    @DisplayName("결제 실패 시 PaymentFailResponse를 반환한다")
    void tossPaymentFail_shouldReturnPaymentFailResponse() throws Exception {
        // given
        PaymentFailResponse mockResponse = PaymentFailResponse.builder()
                .errorCode("ERR001")
                .errorMessage("결제 실패")
                .orderId("ORD12345678")
                .build();

        given(paymentFacadeWithNamedLock.toFailRequest("ERR001", "결제 실패", "ORD12345678")).willReturn(mockResponse);

        // when & then
        mockMvc.perform((RequestBuilder) get("/api/v1/payments/toss/fail")
                        .queryParam("code", "ERR001")
                        .queryParam("message", "결제 실패")
                        .queryParam("orderId", "ORD12345678"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"))// 응답 HTTP 상태 코드 검증
                .andExpect(jsonPath("$.data").isNotEmpty());
    }

    @Test
    @DisplayName("결제 취소 시 PaymentCancelResponse를 반환한다")
    void tossPaymentCancelPoint_shouldReturnPaymentCancelResponse() throws Exception {
        // given
        Map<String, String> request = new HashMap<>();
        request.put("paymentKey","paymentKey123");
        request.put("cancelReason","단순 변심");

        PaymentCancelResponse mockResponse = PaymentCancelResponse.builder()
                .details(request)
                .build();

        given(paymentFacadeWithNamedLock.toCancelRequest("paymentKey123", "단순 변심")).willReturn(mockResponse);

        // when & then
        mockMvc.perform(post("/api/v1/payments/toss/cancel")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                            "paymentKey": "paymentKey123",
                            "cancelReason" : "단순 변심"
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK")) // 응답 HTTP 상태 코드 검증
                .andExpect(jsonPath("$.data").isNotEmpty());
    }

}