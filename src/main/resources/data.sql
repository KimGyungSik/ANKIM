-- 상품 데이터 삽입
INSERT INTO product (
    avg_rating, category_no, caution_order, caution_product, caution_shipping, code,
    created_at, description, discount_rate, display_order, free_shipping, handmade,
    modified_at, name, option_available, orig_price, qna_count, qty, rel_prod_code,
    restock_available, review_count, search_keywords, sell_price, selling_status,
    ship_fee, view_count, wish_count
) VALUES (
             4.5, 18,
             '* 교환 및 반품이 불가능한 경우 *
             - 고객님의 책임 있는 사유로 상품이 멸실 또는 훼손된 경우 (상품 내용 확인을 위한 포장 훼손 제외)
             - 포장을 개봉하거나 훼손하여 상품 가치가 상실된 경우
             - 고객님의 사용 또는 일부 소비로 인해 상품 가치가 현저히 감소한 경우',

             '※ 다양한 컬러 특성상 각 상품마다 사이즈 스펙에 오차가 있을 수 있습니다.',

             'Delivery 브랜드 업체발송은 상품설명에 기입된 배송 공지 기준으로 출고됩니다.
             Delivery 29CM 자체발송의 경우, 오후 2시까지 결제 확인된 주문은 당일 출고되며,
             5만원 이상 주문은 무료배송, 5만원 미만 주문은 3,000원의 배송비가 추가됩니다.',

             '1962858',
             '2025-02-12 22:11:46',
             '* 사계절 언제나 적당한 두께감의 클래식 숏 슬리브 제품입니다.
             - 세탁시 변형을 최소화하도록 워싱 프로세스를 거쳤으며, 내구성이 우수합니다.
             - 탄탄하고 밀도 높은 원단으로 착용 시 안정감과 편안함을 제공합니다.',

             17, NULL, 'Y', 'Y',
             '2025-02-12 22:11:46',
             'TC5-TS03 체인티 (17 Color)', 'Y', 25500, 100, 40, NULL, 'Y', 580, '#0000FF, #FF0000', 21165, 'SELLING', 0, 500, 100
         );

-- 제품 이미지 데이터 삽입
INSERT INTO product_img (img_name, img_url, ord, orig_name, prod_no, repimg_yn) VALUES
                                                                                    ('365096f6-bfe5-4937-ba62-8236c5895afb.jpg', 'https://product-uploaded-files.s3.ap-northeast-2.amazonaws.com/4a8e9b33-c985-4443-8245-f34316f11c1a.jpg', 1, '화면 캡처 2025-02-12 214640.jpg', 841, 'Y'),
                                                                                    ('a579122e-edb0-41b8-935f-6dcf58792bd6.jpg', 'https://product-uploaded-files.s3.ap-northeast-2.amazonaws.com/a6886b27-3982-4c77-91f5-750206e29986.jpg', 2, '화면 캡처 2025-02-12 214657.jpg', 841, 'Y'),
                                                                                    ('b5e9619b-e629-487b-bead-2e7ccb6b7bf7.jpg', 'https://product-uploaded-files.s3.ap-northeast-2.amazonaws.com/aba28418-ef3c-47ea-9bf1-2159c5734288.jpg', 3, '화면 캡처 2025-02-12 214715.jpg', 841, 'Y'),
                                                                                    ('9ba9421e-0368-458b-a0b9-bf7f43ff22c8.jpg', 'https://product-uploaded-files.s3.ap-northeast-2.amazonaws.com/9be24280-9297-4cc1-9e8c-c8974afc8f3c.jpg', 4, '화면 캡처 2025-02-12 214731.jpg', 841, 'Y');

-- 옵션 그룹 삽입
INSERT INTO option_group (name, prod_no) VALUES
                                             ('사이즈', 841),
                                             ('컬러', 841);

-- 옵션 값 삽입
INSERT INTO option_value (color_code, name, optg_no) VALUES
                                                         (NULL, 'small', 1681),
                                                         (NULL, 'large', 1681),
                                                         ('#0000FF', 'blue', 1682),
                                                         ('#FF0000', 'red', 1682);

-- 상품 아이템 삽입
INSERT INTO item (add_price, code, max_qty, min_qty, name, prod_no, qty, saf_qty, selling_status, thumbnail_img_url, total_price) VALUES
                                                                                                                                      (3000, '1962858-1', 40, 5, '사이즈: small, 컬러: blue', 841, 40, 40, 'SELLING', 'https://product-uploaded-files.s3.ap-northeast-2.amazonaws.com/4a8e9b33-c985-4443-8245-f34316f11c1a.jpg', 27500),
                                                                                                                                      (2000, '1962858-2', 40, 5, '사이즈: small, 컬러: red', 841, 40, 40, 'SELLING', 'https://product-uploaded-files.s3.ap-northeast-2.amazonaws.com/4a8e9b33-c985-4443-8245-f34316f11c1a.jpg', 27500),
                                                                                                                                      (1000, '1962858-3', 40, 5, '사이즈: large, 컬러: blue', 841, 40, 40, 'SELLING', 'https://product-uploaded-files.s3.ap-northeast-2.amazonaws.com/4a8e9b33-c985-4443-8245-f34316f11c1a.jpg', 27500),
                                                                                                                                      (5000, '1962858-4', 40, 5, '사이즈: large, 컬러: red', 841, 40, 40, 'SELLING', 'https://product-uploaded-files.s3.ap-northeast-2.amazonaws.com/4a8e9b33-c985-4443-8245-f34316f11c1a.jpg', 27500);

-- 아이템 옵션 연결
INSERT INTO item_option (item_no, optv_no) VALUES
                                               (1681, 2521), (1681, 2523),
                                               (1682, 2521), (1682, 2524),
                                               (1683, 2522), (1683, 2523),
                                               (1684, 2522), (1684, 2524);






-- -- 카테고리 데이터 (대분류, 중분류, 소분류)
-- INSERT INTO category (name, level, parent_no) VALUES ('아우터', 'MIDDLE', NULL);
-- INSERT INTO category (name, level, parent_no) VALUES ('상의', 'MIDDLE', NULL);
--
-- -- 소분류 (중분류 '아우터')
-- INSERT INTO category (name, level, parent_no) VALUES ('코트', 'SUB', 1);
-- INSERT INTO category (name, level, parent_no) VALUES ('자켓', 'SUB', 1);
-- INSERT INTO category (name, level, parent_no) VALUES ('가디건', 'SUB', 1);
--
-- -- 소분류 (중분류 '상의')
-- INSERT INTO category (name, level, parent_no) VALUES ('티셔츠', 'SUB', 2);
-- INSERT INTO category (name, level, parent_no) VALUES ('블라우스', 'SUB', 2);
-- INSERT INTO category (name, level, parent_no) VALUES ('니트', 'SUB', 2);
--
-- -- 상품 데이터
-- INSERT INTO product (name, description, discount_rate, orig_price, qty, selling_status, category_no)
-- VALUES
--     ('캐시미어 코트', '부드럽고 고급스러운 캐시미어 코트', 10, 120000, 100, 'SELLING', 3),
--     ('울 자켓', '보온성이 뛰어난 울 자켓', 15, 80000, 150, 'SELLING', 4),
--     ('면 티셔츠', '편안한 착용감의 면 티셔츠', 20, 20000, 300, 'SELLING', 6),
--     ('니트 스웨터', '포근한 느낌의 니트 스웨터', 25, 40000, 200, 'STOP_SELLING', 8);
--
-- -- 옵션 그룹 데이터
-- INSERT INTO option_group (name, prod_no) VALUES
--                                                       ('컬러', 1),
--                                                       ('사이즈', 1),
--                                                       ('컬러', 3),
--                                                       ('사이즈', 3);
--
-- -- 옵션 값 데이터
-- INSERT INTO option_value (name, color_code, optg_no) VALUES
--                                                                        ('블랙', '#000000', 1),
--                                                                        ('그레이', '#808080', 1),
--                                                                        ('M', NULL, 2),
--                                                                        ('L', NULL, 2),
--                                                                        ('화이트', '#FFFFFF', 3),
--                                                                        ('블루', '#0000FF', 3),
--                                                                        ('S', NULL, 4),
--                                                                        ('XL', NULL, 4);
--
-- -- 품목 데이터
-- INSERT INTO item (name, code, add_price, qty, saf_qty, max_qty, min_qty, prod_no) VALUES
--                                                                                          ('색상: 블랙, 사이즈: M', 'P001-BLK-M', 0, 50, 10, 5, 1, 1),
--                                                                                          ('색상: 블랙, 사이즈: L', 'P001-BLK-L', 0, 30, 5, 3, 1, 1),
--                                                                                          ('색상: 그레이, 사이즈: M', 'P001-GRY-M', 1000, 40, 8, 4, 1, 1),
--                                                                                          ('색상: 그레이, 사이즈: L', 'P001-GRY-L', 1000, 20, 5, 2, 1, 1),
--                                                                                          ('색상: 화이트, 사이즈: S', 'P003-WHT-S', 0, 100, 20, 10, 1, 3),
--                                                                                          ('색상: 블루, 사이즈: XL', 'P003-BLU-XL', 2000, 80, 15, 8, 2, 3);
--
-- -- 상품 이미지 데이터
-- INSERT INTO product_img (img_name, orig_name, img_url, repimg_yn, ord, prod_no) VALUES
--                                                                                           ('cashmere-coat-thumbnail.jpg', '캐시미어 코트 썸네일', 'http://example.com/images/cashmere-coat-thumbnail.jpg', 'Y', 1, 1),
--                                                                                           ('cashmere-coat-detail.jpg', '캐시미어 코트 상세', 'http://example.com/images/cashmere-coat-detail.jpg', 'N', 1, 1),
--                                                                                           ('cotton-tshirt-thumbnail.jpg', '면 티셔츠 썸네일', 'http://example.com/images/cotton-tshirt-thumbnail.jpg', 'Y', 1, 3),
--                                                                                           ('cotton-tshirt-detail.jpg', '면 티셔츠 상세', 'http://example.com/images/cotton-tshirt-detail.jpg', 'N', 1, 3);
-- [ 회원가입 약관 삽입 ]
-- 상위 약관
INSERT INTO terms (parents_no, category, name, contents, terms_yn, version, level, active_yn)
VALUES (null, 'JOIN', '회원가입 약관', 'ANKIM 이용약관', 'Y', 1, 1, 'Y');

-- 필수 약관
INSERT INTO terms (parents_no, category, name, contents, terms_yn, version, level, active_yn)
VALUES (1, 'JOIN', '만 14세 이상입니다', '이 약관은 만 14세 이상임을 동의하는 내용입니다.', 'Y', 1, 2, 'Y');

INSERT INTO terms (parents_no, category, name, contents, terms_yn, version, level, active_yn)
VALUES (1, 'JOIN', '이용약관 동의', '이 약관은 서비스 이용에 대한 동의를 포함합니다.', 'Y', 1, 2, 'Y');

-- 선택 약관
INSERT INTO terms (parents_no, category, name, contents, terms_yn, version, level, active_yn)
VALUES (1, 'JOIN', '마케팅 목적의 개인정보 수집 및 이용 동의', '마케팅 목적의 개인정보 수집 및 이용 동의', 'N', 1, 2, 'Y');

INSERT INTO terms (parents_no, category, name, contents, terms_yn, version, level, active_yn)
VALUES (1, 'JOIN', '광고성 정보 수신 동의', '광고성 정보를 수신하는 것에 대한 동의입니다.', 'N', 1, 2, 'Y');

INSERT INTO terms (parents_no, category, name, contents, terms_yn, version, level, active_yn)
VALUES (5, 'JOIN', '문자 수신 동의', '광고성 정보를 수신하는 것에 대한 동의입니다.', 'N', 1, 3, 'Y');

INSERT INTO terms (parents_no, category, name, contents, terms_yn, version, level, active_yn)
VALUES (5, 'JOIN', '이메일 수신 동의', '광고성 정보를 수신하는 것에 대한 동의입니다.', 'N', 1, 3, 'Y');

-- [ 주문/결제 약관 삽입 ]
INSERT INTO terms (parents_no, category, name, contents, terms_yn, version, level, active_yn)
VALUES (null, 'ORDER', '주문 약관', 'ANKIM 주문/결제 약관', 'Y', 1, 1, 'Y');

-- [ 탈퇴 약관 삽입 ]
INSERT INTO terms (parents_no, category, name, contents, terms_yn, version, level, active_yn)
VALUES (null, 'LEAVE', '탈퇴 약관', 'ANKIM 탈퇴 약관', 'Y', 1, 1, 'Y');

INSERT INTO terms (parents_no, category, name, contents, terms_yn, version, level, active_yn)
VALUES (9, 'LEAVE', '탈퇴 시 삭제되는 내용','- 탈퇴 시 고객님께서 보유하셨던 쿠폰과 마일리지는 모두 소멸되며 환불할 수 없습니다. 또한 다른 계정으로 양도 또는 이관할 수 없습니다.
- 탈퇴한 계정 및 이용 내역은 복구할 수 없으니 탈퇴 시 유의하시기 바랍니다.', 'Y', 1, 2, 'Y');

INSERT INTO terms (parents_no, category, name, contents, terms_yn, version, level, active_yn)
VALUES (9, 'LEAVE', '탈퇴 시 보관 또는 유지되는 항목',
        '- 탈퇴 시 법령에 따라 보관해야 하는 항목은 관련 법령에 따라 일정 기간 보관하며 다른 목적으로 이용하지 않습니다. 전자상거래 등에서의 소비자보호에 관한 법률에 의거하여 대금결제 및 재화 등의 공급에 관한 기록 5년, 계약 또는 청약철회 등에 관한 기록 5년, 소비자의 불만 또는 분쟁처리에 관한 기록은 3년동안 보관됩니다.
        - 아이디(이메일), 이메일, 비밀번호는 부정 이용ㆍ탈퇴 방지를 위해 탈퇴 요청 시 7일 간 별도 보관 후 파기합니다.
        - 탈퇴 후에도 서비스에 등록한 게시물 및 댓글은 그대로 남아 있습니다. 상품 리뷰, 게시글, 이벤트 댓글 등은 삭제되지 않습니다. 탈퇴 후에는 회원정보가 삭제되어 본인 여부를 확인할 수 없으므로 게시글을 임의로 삭제해드릴 수 없습니다. 먼저 해당 게시물을 삭제하신 후 탈퇴를 신청하시기 바랍니다.', 'Y', 1, 2, 'Y');



-- [ 회원 삽입 ]
INSERT INTO member (
    login_id,
    password,
    name,
    phone_num,
    birth,
    gender,
    grade,
    join_date,
    status
) VALUES (
             'sample@example.com',        -- login_id
             '$2a$10$FjPDD9Y58GVTeOei3JvgVu84kZFxxZSs9j39yOLdkWXOlzOaDrhZe',            -- pwd( passWorld! )
             '홍길동',                    -- name
             '010-1234-5678',           -- phone_num
             '1990-01-01',              -- birth
             'M',                       -- gender
             '50',                      -- grade
             '2024-11-18T23:59:59',     -- join_date (현재 시간)
             'ACTIVE'                   -- status
         );

-- [ 회원 주소 삽입]
INSERT INTO MEM_ADDR (MEM_NO, ZIP_CODE, ADDR_MAIN, ADDR_DTL, ADDR_NAME, PHONE_NUM, ADDR_DEF, ACTIVE_YN)
VALUES (1, 12345, '서울특별시 강남구', '10층 D강의실', '기본 배송지', '010-1234-5678', 'Y', 'Y');

-- 탈퇴사유
INSERT INTO leave_rsn (reason, active_yn) VALUES
                                              ('탈퇴 후 재가입을 위해서', 'Y'),
                                              ('사고 싶은 상품이 없어서', 'Y'),
                                              ('자주 이용하지 않아서', 'Y'),
                                              ('서비스 및 고객지원이 만족스럽지 않아서', 'Y'),
                                              ('광고성 알림이 너무 많이 와서', 'Y'),
                                              ('기타', 'Y');
