package shoppingmall.ankim.global.config.uuid;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class Base62UUIDTest {

    /*
    * 1,000,000,000 ns = 1초
    * */

    @Test
    @DisplayName("UUID와 Base62 변환 비교 테스트")
    void testUuidAndBase62Performance() {
        String loginId = "test@example.com";

        // 원본 UUID 생성
        UUID uuid = UUID.randomUUID();
        String originalUuid = uuid.toString();

        // Base62 변환
        long startBase62 = System.nanoTime();
        String base62Uuid = Base62UUID.toBase62(uuid);
        long endBase62 = System.nanoTime();

        // 결과 출력
        System.out.println("로그인 ID: " + loginId);
        System.out.println("원본 UUID: " + originalUuid);
        System.out.println("원본 UUID 길이: " + originalUuid.length());
        System.out.println("Base62 UUID: " + base62Uuid);
        System.out.println("Base62 UUID 길이: " + base62Uuid.length());
        System.out.println("Base62 변환 시간 (ns): " + (endBase62 - startBase62));

        // 검증
        assertThat(originalUuid.length()).isEqualTo(36); // 하이픈 포함 UUID 길이
        assertThat(base62Uuid.length()).isLessThan(36); // Base62는 더 짧아야 함
    }

    @Test
    @DisplayName("UUID 생성 속도 vs Base62 변환 속도 테스트")
    void testPerformanceComparison() {
        int iterations = 1_000_000; // 테스트 반복 횟수
        long uuidTime = 0;
        long base62Time = 0;

        // UUID 생성 속도 테스트
        long startUuid = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            UUID.randomUUID();
        }
        long endUuid = System.nanoTime();
        uuidTime = endUuid - startUuid;

        // Base62 변환 속도 테스트
        UUID uuid = UUID.randomUUID();
        long startBase62 = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            Base62UUID.toBase62(uuid);
        }
        long endBase62 = System.nanoTime();
        base62Time = endBase62 - startBase62;

        // 결과 출력
        System.out.println("반복 횟수 : " + iterations);
        System.out.println("UUID 생성 시간 (ns): " + uuidTime);
        System.out.println("Base62 변환 시간 (ns): " + base62Time);
        System.out.println("평균 UUID 생성 시간 (ns): " + (uuidTime / iterations));
        System.out.println("평균 Base62 생성 시간 (ns): " + (base62Time / iterations));
    }
}