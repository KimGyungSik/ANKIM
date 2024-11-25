package shoppingmall.ankim.domain.security.service;

import java.time.Duration;

public interface RedisSingleDataService {

    int setSingleData(String key, Object value); // 단일 데이터 값 등록, 수정

    int setSingleData(String key, Object value, Duration duration); // 단일 데이터 값 등록,수정( duration 값 존재하는 경우 메모리 상 유효시간 지정 )

    String getSingleData(String key); // 키 값을 기반으로 단일 데이터 값 조회

    int deleteSingleData(String key); // 키 값을 기반으로 단일 데이터 값 삭제
}