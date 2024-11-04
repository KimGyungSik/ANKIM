package shoppingmall.ankim.domain.terms.entity;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class TermsHistoryId implements Serializable {
    private Long memNo;
    private String code;

    public TermsHistoryId() {}

    public TermsHistoryId(Long memNo, String code) {
        this.memNo = memNo;
        this.code = code;
    }

    // hashCode()와 equals() 메서드 구현 필수
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TermsHistoryId that = (TermsHistoryId) o;
        return Objects.equals(memNo, that.memNo) && Objects.equals(code, that.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(memNo, code);
    }
}
