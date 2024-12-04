package shoppingmall.ankim.domain.address.service;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import shoppingmall.ankim.domain.address.entity.member.MemberAddress;
import shoppingmall.ankim.domain.address.repository.MemberAddressRepository;
import shoppingmall.ankim.domain.address.service.request.MemberAddressRegisterServiceRequest;
import shoppingmall.ankim.domain.member.entity.Member;
import shoppingmall.ankim.domain.member.repository.MemberRepository;
import shoppingmall.ankim.domain.security.service.JwtTokenProvider;
import shoppingmall.ankim.factory.MemberJwtFactory;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@TestPropertySource(properties = "spring.sql.init.mode=never")
@Transactional
class MemberAddressServiceTest {
    @Autowired
    EntityManager em;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MemberAddressRepository memberAddressRepository;

    @Autowired
    private MemberAddressService memberAddressService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("기존 기본 배송지가 없는 경우 새로운 기본 배송지로 저장한다.")
    void saveOrUpdateAddress_whenNoDefaultAddress() {
        // given
        String loginId = "test@ankim.com";
        Member member = MemberJwtFactory.createMember(em, loginId);
        String accessToken = MemberJwtFactory.createToken(member, jwtTokenProvider);

        MemberAddressRegisterServiceRequest request = MemberAddressRegisterServiceRequest.builder()
                .zipCode(12345)
                .addressMain("서울특별시 강남구")
                .addressDetail("101호")
                .build();

        MemberAddress expectedAddress = request.toEntity(member);

        // when
        memberAddressService.saveOrUpdateAddress(accessToken, request);

        // then
        Optional<MemberAddress> findAddress = memberAddressRepository.findDefaultAddressByMemberNo(member.getNo());

        assertThat(expectedAddress.getBaseAddress().getZipCode()).isEqualTo(findAddress.get().getBaseAddress().getZipCode());
        assertThat(expectedAddress.getBaseAddress().getAddressMain()).isEqualTo(findAddress.get().getBaseAddress().getAddressMain());
        assertThat(expectedAddress.getBaseAddress().getAddressDetail()).isEqualTo(findAddress.get().getBaseAddress().getAddressDetail());
    }

    @Test
    @DisplayName("기존 기본 배송지가 존재할 경우 새로운 기본 배송지로 업데이트한다.")
    void saveOrUpdateAddress_whenDefaultAddressExists() {
        // given
        String loginId = "test@ankim.com";
        Member member = MemberJwtFactory.createMember(em, loginId);
        String accessToken = MemberJwtFactory.createToken(member, jwtTokenProvider);

        // 기존 기본 배송지 설정
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
        memberAddressRepository.save(existingAddress);

        em.flush();
        em.clear();

        // 새로운 기본 배송지 요청
        MemberAddressRegisterServiceRequest request = MemberAddressRegisterServiceRequest.builder()
                .zipCode(54321)
                .addressMain("부산광역시 해운대구")
                .addressDetail("202호")
                .build();

        // when
        memberAddressService.saveOrUpdateAddress(accessToken, request);

        // then
        Optional<MemberAddress> updatedAddress = memberAddressRepository.findDefaultAddressByMemberNo(member.getNo());
        assertThat(updatedAddress).isPresent();
        assertThat(updatedAddress.get().getBaseAddress().getZipCode()).isEqualTo(request.getZipCode());
        assertThat(updatedAddress.get().getBaseAddress().getAddressMain()).isEqualTo(request.getAddressMain());
        assertThat(updatedAddress.get().getBaseAddress().getAddressDetail()).isEqualTo(request.getAddressDetail());
    }
}