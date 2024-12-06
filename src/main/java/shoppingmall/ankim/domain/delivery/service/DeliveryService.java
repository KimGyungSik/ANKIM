package shoppingmall.ankim.domain.delivery.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shoppingmall.ankim.domain.address.service.request.MemberAddressCreateServiceRequest;
import shoppingmall.ankim.domain.address.entity.BaseAddress;
import shoppingmall.ankim.domain.address.entity.member.MemberAddress;
import shoppingmall.ankim.domain.address.exception.AddressNotFoundException;
import shoppingmall.ankim.domain.address.repository.MemberAddressRepository;
import shoppingmall.ankim.domain.delivery.entity.Delivery;
import shoppingmall.ankim.domain.delivery.repository.DeliveryRepository;
import shoppingmall.ankim.domain.delivery.service.request.DeliveryCreateServiceRequest;
import shoppingmall.ankim.domain.member.entity.Member;
import shoppingmall.ankim.domain.member.exception.InvalidMemberException;
import shoppingmall.ankim.domain.member.repository.MemberRepository;
import shoppingmall.ankim.global.config.track.TrackingNumberGenerator;

import static shoppingmall.ankim.global.exception.ErrorCode.*;

@Service
@Transactional
@RequiredArgsConstructor
public class DeliveryService {

    private final TrackingNumberGenerator trackingNumberGenerator;
    private final DeliveryRepository deliveryRepository;
    private final MemberRepository memberRepository;
    private final MemberAddressRepository memberAddressRepository;

    public Delivery createDelivery(DeliveryCreateServiceRequest request, MemberAddressCreateServiceRequest addressRequest, String loginId) {
        // 회원 조회
        Member member = getMember(loginId);

        // 주소 조회 또는 생성 로직
        // 기존 주소가 있으면 기존 주소로, 없으면 신규 생성
        MemberAddress memberAddress = request.getAddressId() != null
                ? memberAddressRepository.findById(request.getAddressId())
                .orElseThrow(() -> new AddressNotFoundException(ADDRESS_NOT_FOUND))
                : (addressRequest != null
                ? createMemberAddress(addressRequest, member)
                : memberAddressRepository.findDefaultAddressByMember(member)
                .orElseThrow(() -> new AddressNotFoundException(DEFAULT_ADDRESS_NOT_FOUND))); // 기본 주소가 없을 경우 예외

        // 배송 생성
        return Delivery.create(memberAddress, request.getCourier(), request.getDelReq(), trackingNumberGenerator);
    }
    private Member getMember(String loginId) {
        // loginId를 가지고 member엔티티의 no 조회
        Member member = memberRepository.findByLoginId(loginId);
        if(member == null) {
            throw new InvalidMemberException(INVALID_MEMBER);
        }
        return member;
    }

    private MemberAddress createMemberAddress(MemberAddressCreateServiceRequest addressRequest, Member member) {
        BaseAddress baseAddress = BaseAddress.builder()
                .zipCode(addressRequest.getZipCode())
                .addressMain(addressRequest.getAddressMain())
                .addressDetail(addressRequest.getAddressDetail())
                .build();

        MemberAddress memberAddress = MemberAddress.create(
                member,
                addressRequest.getAddressName(),
                baseAddress,
                addressRequest.getPhoneNumber(),
                addressRequest.getEmergencyPhoneNumber(),
                addressRequest.getDefaultAddressYn()
        );

        return memberAddressRepository.save(memberAddress);
    }


}
