package shoppingmall.ankim.global.util;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MaskingUtil {

    // 객체 생성 방지를 위해서 private 기본 생성자 작성
    private MaskingUtil() {
        throw new IllegalStateException("Utility class");
    }

    // 이름의 첫 글자와 마지막 글자를 제외한 중간 글자를 *로 변경하는 로직
    // 만약 두글자 이름이면 마지막 글자를 *로 변경
    public static String maskName(String name) {
        if(name.length() == 2) {
            return name.charAt(0) + "*";
        }

        String firstChar = name.substring(0, 1);
        String lastChar = name.substring(name.length() - 1);
        String middle = "*".repeat(name.length() - 2);

        log.info("maskName = " + firstChar + middle + lastChar);

        return firstChar + middle + lastChar;
    }

    // 로그인 ID 마스킹: ex*****@*****.com 형태
    public static String maskLoginId(String loginId) {
        if (loginId == null || !loginId.contains("@")) return "";

        String[] parts = loginId.split("@");
        if (parts.length != 2) return "";

        String idPart = parts[0];  // 이메일 앞부분
        String domainPart = parts[1];  // 이메일 뒷부분

        // 첫 두 글자 보이기, 나머지 마스킹
        String maskedIdPart = idPart.length() <= 2
                ? idPart.charAt(0) + "*".repeat(Math.max(0, idPart.length() - 1))
                : idPart.substring(0, 2) + "*".repeat(idPart.length() - 2);

        // 도메인 마스킹 처리 (ex: *****.com)
        int lastDotIndex = domainPart.lastIndexOf('.');
        String maskedDomain;
        if (lastDotIndex > 0) {
            String domainSuffix = domainPart.substring(lastDotIndex); // .com, .net 등 유지
            maskedDomain = "*".repeat(lastDotIndex) + domainSuffix;
        } else {
            maskedDomain = "*".repeat(domainPart.length());
        }

        return maskedIdPart + "@" + maskedDomain;
    }

    // 전화번호 마스킹 처리
    public static String maskPhoneNum(String phoneNum) {
        if (phoneNum == null || phoneNum.isEmpty()) return "";

        // 전화번호 형식이 '-' 포함된 경우 (010-1234-5678)
        if (phoneNum.contains("-")) {
            String[] parts = phoneNum.split("-");
            if (parts.length != 3) return phoneNum; // 예상된 형식이 아닐 경우 원본 반환

            return parts[0] + "-****-" + parts[2];
        }

        // '-'가 없는 경우 (01012345678 형식)
        if (phoneNum.length() == 11) { // 01012345678 → 010-****-5678
            return phoneNum.substring(0, 3) + "-****-" + phoneNum.substring(7);
        } else if (phoneNum.length() == 10) { // 지역번호 포함 (021234567) → 02-****-5678
            return phoneNum.substring(0, 2) + "-****-" + phoneNum.substring(6);
        }

        return phoneNum; // 예외적인 경우 원본 반환
    }
}
