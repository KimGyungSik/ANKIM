package shoppingmall.ankim.domain.leaveReason.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shoppingmall.ankim.domain.leaveReason.dto.LeaveReasonResponse;
import shoppingmall.ankim.domain.leaveReason.entity.LeaveReason;
import shoppingmall.ankim.domain.leaveReason.repository.LeaveReasonRepository;

import java.util.ArrayList;
import java.util.List;

@Transactional
@Service
@RequiredArgsConstructor
public class LeaveReasoneServiceImpl implements LeaveReasoneService {

    private final LeaveReasonRepository leaveReasonRepository;

    @Override
    public List<LeaveReasonResponse> getReason() {
        List<LeaveReason> leaveReasons = leaveReasonRepository.findAllByActiveYn("Y");

        List<LeaveReasonResponse> responses = new ArrayList<>();
        for (LeaveReason leaveReason : leaveReasons) {
            responses.add(LeaveReasonResponse.of(leaveReason));
        }

        return responses;
    }
}