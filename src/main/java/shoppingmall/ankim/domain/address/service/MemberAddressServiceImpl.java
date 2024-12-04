package shoppingmall.ankim.domain.address.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shoppingmall.ankim.domain.address.entity.member.MemberAddress;
import shoppingmall.ankim.domain.address.exception.AddressRegisterException;
import shoppingmall.ankim.domain.address.repository.MemberAddressRepository;
import shoppingmall.ankim.domain.address.service.request.MemberAddressRegisterServiceRequest;
import shoppingmall.ankim.domain.member.entity.Member;
import shoppingmall.ankim.domain.member.exception.InvalidMemberException;
import shoppingmall.ankim.domain.member.repository.MemberRepository;
import shoppingmall.ankim.domain.security.exception.JwtValidException;
import shoppingmall.ankim.domain.security.service.JwtTokenProvider;

import java.util.Optional;

import static shoppingmall.ankim.global.exception.ErrorCode.*;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MemberAddressServiceImpl implements MemberAddressService {

    private final MemberRepository memberRepository;
    private final MemberAddressRepository memberAddressRepository;
    private final JwtTokenProvider jwtTokenProvider;


    @Override
    public String saveOrUpdateAddress(String accessToken, MemberAddressRegisterServiceRequest request) {
        Member member = getMember(accessToken);

        // member의 no를 가지고 기본주소 존재 여부 조회
        Optional<MemberAddress> existingDefaultAddress = memberAddressRepository.findDefaultAddressByMemberNo(member.getNo());
        String message = "주소를 저장했습니다.";
        try {
            if (existingDefaultAddress.isPresent()) {
                MemberAddress address = existingDefaultAddress.get();
                // 업데이트 처리
                address.registerAddress(request.toBaseAddress());
                message = "수정 완료 했습니다.";
            } else {
                // 새로운 기본 주소 생성
                MemberAddress address = request.toEntity(member);
                memberAddressRepository.save(address);
            }
        } catch (Exception e) {
            throw new AddressRegisterException(ADDRESS_REGISTER_ERROR);
        }
        return message;
    }

    private Member getMember(String accessToken) {
        // 토큰 유효성 검사(만료 검사도 들어있음)
        if (!jwtTokenProvider.isTokenValidate(accessToken)) {
            throw new JwtValidException(TOKEN_VALIDATION_ERROR);
        }
        // member의 loginId 추출
        String loginId = jwtTokenProvider.getUsernameFromToken(accessToken);
        // loginId를 가지고 member엔티티의 no 조회
        Member member = memberRepository.findByLoginId(loginId);
        if (member == null) {
            throw new InvalidMemberException(INVALID_MEMBER);
        }
        return member;
    }

}
