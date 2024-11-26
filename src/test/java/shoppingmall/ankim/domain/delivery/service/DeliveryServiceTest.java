//package shoppingmall.ankim.domain.delivery.service;
//
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.TestPropertySource;
//import org.springframework.test.util.ReflectionTestUtils;
//import org.springframework.transaction.annotation.Transactional;
//import shoppingmall.ankim.domain.address.entity.BaseAddress;
//import shoppingmall.ankim.domain.address.entity.member.MemberAddress;
//import shoppingmall.ankim.domain.delivery.dto.DeliveryResponse;
//import shoppingmall.ankim.domain.image.service.S3Service;
//import shoppingmall.ankim.domain.member.entity.Member;
//import shoppingmall.ankim.global.config.track.TrackingNumberGenerator;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.BDDMockito.given;
//import static org.mockito.Mockito.mock;
//
//@ActiveProfiles("test")
//@SpringBootTest
//@TestPropertySource(properties = "spring.sql.init.mode=never")
//@Transactional
//class DeliveryServiceTest {
//
//    @Autowired
//    private DeliveryService deliveryService;
//
//    @MockBean
//    S3Service s3Service;
//
//    @MockBean
//    private TrackingNumberGenerator trackingNumberGenerator;
//
//    @DisplayName("회원 주소를 통해 배송을 등록할 수 있다.")
//    @Test
//    void createDelivery_withValidMemberAddress() {
//        // given
//        Member mockMember = mock(Member.class);
//        given(mockMember.getName()).willReturn("홍길동");
//
//        BaseAddress baseAddress = BaseAddress.builder()
//                .zipCode(12345)
//                .addressMain("서울특별시 강남구 테헤란로")
//                .addressDetail("123-45")
//                .build();
//
//        MemberAddress memberAddress = MemberAddress.create(
//                mockMember,
//                "집",
//                baseAddress,
//                "010-1234-5678",
//                "010-8765-4321",
//                "Y"
//        );
//
//        String courier = "FastCourier";
//        String delReq = "문 앞에 놓아주세요.";
//        String expectedTrackingNumber = "TEST123456789";
//
//        given(trackingNumberGenerator.generate()).willReturn(expectedTrackingNumber);
//
//        // when
//        DeliveryResponse response = deliveryService.createDelivery(memberAddress, courier, delReq);
//
//        // then
//        assertThat(response).isNotNull();
//        assertThat(response.getTrackingNumber()).isEqualTo(expectedTrackingNumber);
//        assertThat(response.getCourier()).isEqualTo(courier);
//        assertThat(response.getReceiver()).isEqualTo("홍길동");
//        assertThat(response.getReceiverPhone()).isEqualTo("010-1234-5678");
//        assertThat(response.getAddress()).isEqualTo("서울특별시 강남구 테헤란로 123-45");
//        assertThat(response.getZipcode()).isEqualTo(12345);
//        assertThat(response.getDeliveryRequest()).isEqualTo(delReq);
//    }
//
//}