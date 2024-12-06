package shoppingmall.ankim.domain.delivery.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import shoppingmall.ankim.domain.address.service.request.MemberAddressCreateServiceRequest;
import shoppingmall.ankim.domain.address.entity.BaseAddress;
import shoppingmall.ankim.domain.address.entity.member.MemberAddress;
import shoppingmall.ankim.domain.address.repository.MemberAddressRepository;
import shoppingmall.ankim.domain.delivery.entity.Delivery;
import shoppingmall.ankim.domain.delivery.service.request.DeliveryCreateServiceRequest;
import shoppingmall.ankim.domain.image.service.S3Service;
import shoppingmall.ankim.domain.member.entity.Member;
import shoppingmall.ankim.domain.member.repository.MemberRepository;
import shoppingmall.ankim.global.config.track.TrackingNumberGenerator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ActiveProfiles("test")
@SpringBootTest
@TestPropertySource(properties = "spring.sql.init.mode=never")
@Transactional
class DeliveryServiceTest {

    @MockBean
    S3Service s3Service;

    @Autowired
    private DeliveryService deliveryService;

    @MockBean
    private TrackingNumberGenerator trackingNumberGenerator;

    @MockBean
    private MemberAddressRepository memberAddressRepository;

    @MockBean
    private MemberRepository memberRepository;

    @DisplayName("회원 주소를 통해 배송을 등록할 수 있다.")
    @Test
    void createDelivery_withValidMemberAddress() {
        // given
        String loginId = "testMember"; // 로그인 ID
        Long memberId = 1L; // 회원 ID
        Long addressId = 1L; // 주소 ID

        Member mockMember = mock(Member.class);
        given(mockMember.getNo()).willReturn(memberId);
        given(mockMember.getName()).willReturn("홍길동");

        // Mock MemberAddress
        BaseAddress baseAddress = BaseAddress.builder()
                .zipCode(12345)
                .addressMain("서울특별시 강남구 테헤란로")
                .addressDetail("123-45")
                .build();

        MemberAddress memberAddress = MemberAddress.create(
                mockMember,
                "집",
                baseAddress,
                "010-1234-5678",
                "010-8765-4321",
                "Y"
        );

        // Mock repositories
        given(memberRepository.findByLoginId(loginId)).willReturn(mockMember);
        given(memberAddressRepository.findById(addressId)).willReturn(java.util.Optional.of(memberAddress));

        String courier = "FastCourier";
        String delReq = "문 앞에 놓아주세요.";
        String expectedTrackingNumber = "TEST123456789";

        given(trackingNumberGenerator.generate()).willReturn(expectedTrackingNumber);

        DeliveryCreateServiceRequest deliveryRequest = DeliveryCreateServiceRequest.builder()
                .addressId(addressId)
                .courier(courier)
                .delReq(delReq)
                .build();

        // when
        Delivery delivery = deliveryService.createDelivery(deliveryRequest, null, loginId);

        // then
        assertThat(delivery).isNotNull();
        assertThat(delivery.getTrckNo()).isEqualTo(expectedTrackingNumber);
        assertThat(delivery.getCourier()).isEqualTo(courier);
        assertThat(delivery.getReceiver()).isEqualTo("홍길동");
        assertThat(delivery.getReceiverPhone()).isEqualTo("010-1234-5678");
        assertThat(delivery.getAddress()).isEqualTo("서울특별시 강남구 테헤란로 123-45");
        assertThat(delivery.getZipcode()).isEqualTo(12345);
        assertThat(delivery.getDelReq()).isEqualTo(delReq);
    }

    @DisplayName("신규 주소를 등록하며 배송을 생성할 수 있다.")
    @Test
    void createDelivery_withNewAddress() {
        // given
        String loginId = "testMember"; // 로그인 ID
        Long memberId = 1L; // 회원 ID

        Member mockMember = mock(Member.class);
        given(mockMember.getNo()).willReturn(memberId);
        given(mockMember.getName()).willReturn("홍길동");

        given(memberRepository.findByLoginId(loginId)).willReturn(mockMember);

        MemberAddressCreateServiceRequest addressRequest = MemberAddressCreateServiceRequest.builder()
                .addressName("회사")
                .zipCode(67890)
                .addressMain("서울특별시 중구 을지로")
                .addressDetail("101-202")
                .phoneNumber("010-5678-1234")
                .emergencyPhoneNumber("010-8765-4321")
                .defaultAddressYn("Y")
                .build();

        String courier = "SlowCourier";
        String delReq = "집 앞에 놔주세요.";
        String expectedTrackingNumber = "TEST987654321";

        given(trackingNumberGenerator.generate()).willReturn(expectedTrackingNumber);

        MemberAddress mockSavedAddress = mock(MemberAddress.class);
        given(mockSavedAddress.getNo()).willReturn(1L);
        given(mockSavedAddress.getPhoneNumber()).willReturn(addressRequest.getPhoneNumber()); // 전화번호 설정
        given(mockSavedAddress.getEmergencyPhoneNumber()).willReturn(addressRequest.getEmergencyPhoneNumber()); // 비상전화 설정
        given(mockSavedAddress.getBaseAddress()).willReturn(BaseAddress.builder()
                .zipCode(addressRequest.getZipCode())
                .addressMain(addressRequest.getAddressMain())
                .addressDetail(addressRequest.getAddressDetail())
                .build());
        given(mockSavedAddress.getMember()).willReturn(mockMember);

        // Mock save 호출
        given(memberAddressRepository.save(org.mockito.ArgumentMatchers.any(MemberAddress.class))).willReturn(mockSavedAddress);

        DeliveryCreateServiceRequest deliveryRequest = DeliveryCreateServiceRequest.builder()
                .addressId(null)
                .courier(courier)
                .delReq(delReq)
                .build();

        // when
        Delivery delivery = deliveryService.createDelivery(deliveryRequest, addressRequest, loginId);

        // then
        assertThat(delivery).isNotNull();
        assertThat(delivery.getTrckNo()).isEqualTo(expectedTrackingNumber);
        assertThat(delivery.getCourier()).isEqualTo(courier);
        assertThat(delivery.getReceiver()).isEqualTo("홍길동");
        assertThat(delivery.getReceiverPhone()).isEqualTo("010-5678-1234");
        assertThat(delivery.getAddress()).isEqualTo("서울특별시 중구 을지로 101-202");
        assertThat(delivery.getZipcode()).isEqualTo(67890);
        assertThat(delivery.getDelReq()).isEqualTo(delReq);
    }
}