package shoppingmall.ankim.global.config.uuid;

import java.math.BigInteger;
import java.util.UUID;

public class Base62UUID {

    private static final String BASE62_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int BASE = 62;

    // UUID를 Base62로 변환
    public static String toBase62(UUID uuid) {
        String uuidHex = uuid.toString().replace("-", ""); // 하이픈 제거
        BigInteger uuidBigInt = new BigInteger(uuidHex, 16); // 16진수 -> 10진수 변환(UUID는 문자처럼 보이지만 실제로는 16진수 숫자를 문자로 표현한 것이라 가능)

        StringBuilder base62 = new StringBuilder();
        while (uuidBigInt.compareTo(BigInteger.ZERO) > 0) {
            BigInteger[] divRem = uuidBigInt.divideAndRemainder(BigInteger.valueOf(BASE)); // BigInteger를 BASE(62)로 나눈 몫([0])과 나머지([1])를 구함
            base62.insert(0, BASE62_CHARS.charAt(divRem[1].intValue())); // 62로 나눈 나머지(0 ~ 61)를 BASE62_CHARS에서 가지고와서 맨앞에 삽입
            uuidBigInt = divRem[0]; // 몫
        }

        return base62.toString(); // Base62로 변환된 문자열 반환
    }
}
