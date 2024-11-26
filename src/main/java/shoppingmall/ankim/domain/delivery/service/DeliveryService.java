package shoppingmall.ankim.domain.delivery.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import shoppingmall.ankim.domain.address.entity.member.MemberAddress;
import shoppingmall.ankim.domain.delivery.dto.DeliveryResponse;
import shoppingmall.ankim.domain.delivery.entity.Delivery;
import shoppingmall.ankim.domain.delivery.repository.DeliveryRepository;
import shoppingmall.ankim.global.config.track.TrackingNumberGenerator;

@Service
@RequiredArgsConstructor
public class DeliveryService {

    private final TrackingNumberGenerator trackingNumberGenerator;
    private final DeliveryRepository deliveryRepository;

    public DeliveryResponse createDelivery(MemberAddress memberAddress, String courier, String delReq) {
        Delivery delivery = Delivery.create(memberAddress, courier, delReq, trackingNumberGenerator);
        return DeliveryResponse.fromEntity(delivery);
    }


}
