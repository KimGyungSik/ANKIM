package shoppingmall.ankim.domain.address.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import shoppingmall.ankim.domain.address.entity.BaseAddress;
import shoppingmall.ankim.domain.address.entity.member.MemberAddress;
import shoppingmall.ankim.domain.member.entity.Member;
import shoppingmall.ankim.domain.member.entity.MemberStatus;
import shoppingmall.ankim.domain.member.repository.MemberRepository;
import shoppingmall.ankim.global.config.QuerydslConfig;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestPropertySource(properties = "spring.sql.init.mode=never")
@Import(QuerydslConfig.class)
class MemberAddressRepositoryTest {

    @Autowired
    private MemberAddressRepository memberAddressRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("회원의 기본 주소가 없는 경우 빈 값을 반환한다.")
    void findDefaultAddressByMemberNo_NoDefaultAddress() {
        // given
        Member member = Member.builder()
                .loginId("test@example.com")
                .password("password")
                .name("user1")
                .phoneNum("010-1234-5678")
                .birth(LocalDate.of(1990, 1, 1))
                .grade(50)
                .gender("M")
                .joinDate(LocalDateTime.now())
                .status(MemberStatus.ACTIVE)
                .build();
        memberRepository.save(member);

        // when
        Optional<MemberAddress> defaultAddress = memberAddressRepository.findDefaultAddressByMemberNo(member.getNo());

        // then
        assertThat(defaultAddress).isEmpty();
    }


    @Test
    @DisplayName("회원의 기본 주소를 저장하고 조회한다.")
    void findDefaultAddressByMemberNo() {
        // given
        Member member = Member.builder()
                .loginId("test@example.com")
                .password("password")
                .name("user1")
                .phoneNum("010-1234-5678")
                .birth(LocalDate.of(1990, 1, 1))
                .grade(50)
                .gender("M")
                .joinDate(LocalDateTime.now())
                .status(MemberStatus.ACTIVE)
                .build();
        memberRepository.save(member);

        BaseAddress baseAddress = BaseAddress.builder()
                .zipCode(12345)
                .addressMain("서울특별시 강남구")
                .addressDetail("10층 D강의실")
                .build();

        MemberAddress memberAddress = MemberAddress.builder()
                .member(member)
                .addressName("기본 배송지")
                .baseAddress(baseAddress)
                .phoneNumber("010-1234-5678")
                .defaultAddressYn("Y")
                .activeYn("Y")
                .build();

        memberAddressRepository.save(memberAddress);

        // when
        Optional<MemberAddress> defaultAddress = memberAddressRepository.findDefaultAddressByMemberNo(member.getNo());

        // then
        assertThat(defaultAddress).isPresent();
        assertThat(defaultAddress.get().getBaseAddress().getAddressMain()).isEqualTo(memberAddress.getBaseAddress().getAddressMain());
        assertThat(defaultAddress.get().getDefaultAddressYn()).isEqualTo("Y");
    }

    @Test
    @DisplayName("회원의 신규 주소를 저장하고 기본 배송지를 조회한다.")
    void saveNewAddressAndFindDefaultAddressByMemberNo() {
        // given
        Member member = Member.builder()
                .loginId("test@example.com")
                .password("password")
                .name("user1")
                .phoneNum("010-1234-5678")
                .birth(LocalDate.of(1990, 1, 1))
                .grade(50)
                .gender("M")
                .joinDate(LocalDateTime.now())
                .status(MemberStatus.ACTIVE)
                .build();
        memberRepository.save(member);

        BaseAddress baseAddress = BaseAddress.builder()
                .zipCode(12345)
                .addressMain("서울특별시 강남구")
                .addressDetail("10층 D강의실")
                .build();

        MemberAddress memberAddress = MemberAddress.builder()
                .member(member)
                .addressName("학원")
                .baseAddress(baseAddress)
                .phoneNumber("010-1234-5678")
                .defaultAddressYn("N")
                .activeYn("Y")
                .build();

        memberAddressRepository.save(memberAddress);

        // when
        Optional<MemberAddress> defaultAddress = memberAddressRepository.findDefaultAddressByMemberNo(member.getNo());

        // then
        assertThat(defaultAddress).isEmpty();
    }

}