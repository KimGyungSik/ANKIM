package shoppingmall.ankim.domain.product.service.query;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductQueryService {
    // 상품 상세페이지 조회 by User (상품 기본 필드, 상품 이미지, 옵션 그룹 및 옵션 값)


    // 상품 수정 페이지 by Admin (모든 필드를 보여줘야함)

    // 1차 필터링 : 최신 한달 이내 등록 상품 / 할인 중인 상품 / 좋아요 특정 갯수 이상 / 중분류 카테고리별 상품 / 핸드메이드 상품
    // 2차 필터링 : 1차 필터링 결과를 2차로 소분류 카테고리별 필터링 또는 검색 결과나 정렬 결과를 카테고리별 필터링
    // 정렬 -> 최신순(default) / 추천순 / 인기순 / 가격 낮은 순 / 가격 높은 순 / 할인율 높은 순 / 리뷰 많은순 / 좋아요 많은순 / 판매순
    // 모든 필터링 / 정렬 / 검색은 원하는대로 동시에 이루어져야함
}
