package shoppingmall.ankim.domain.termsHistory.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TermsHistoryUpdateResponse {

    private List<String> message;
    private String date;
    private String sender;

    public static TermsHistoryUpdateResponse of(List<String> message, LocalDateTime now) {
        return TermsHistoryUpdateResponse.builder()
                .message(message)
                .date(now.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")))
                .sender("ANKIM( (주)안킴 )")
                .build();
    }


}
