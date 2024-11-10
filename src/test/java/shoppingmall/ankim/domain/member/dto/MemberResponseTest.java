package shoppingmall.ankim.domain.member.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MemberResponseTest {

    @Test
    @DisplayName("개인정보 보호를 위해 이름을 마스킹 처리하며 세글자 이상인 경우 첫글자와 마지막글자를 제외하고 *로 처리한다.")
    public void maskNameLength3() throws Exception {
        // given
        String name = "홍길동";
        String expectName = "홍*동";

        String firstChar = name.substring(0, 1);
        String lastChar = name.substring(name.length() - 1);
        String middle = "*".repeat(name.length() - 2);

        String maskName = firstChar + middle + lastChar;

        assertEquals(expectName, maskName);
    }

    @Test
    @DisplayName("개인정보 보호를 위해 이름을 마스킹 처리하며 두글자인 경우 첫글자를 제외하고 *로 처리한다.")
    public void maskNameLenght2() throws Exception {
        // given
        String name = "둘리";
        String expectName = "둘*";

        String maskName = name.charAt(0) + "*";

        assertEquals(expectName, maskName);
    }

}