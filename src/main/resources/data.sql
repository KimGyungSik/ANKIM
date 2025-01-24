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
VALUES (null, 'JOIN', '회원가입 약관', 'ANKIM 이용약관', 'Y', 'v1', 1, 'Y');

-- 필수 약관
INSERT INTO terms (parents_no, category, name, contents, terms_yn, version, level, active_yn)
VALUES (1, 'JOIN', '만 14세 이상입니다', '이 약관은 만 14세 이상임을 동의하는 내용입니다.', 'Y', 'v1', 2, 'Y');

INSERT INTO terms (parents_no, category, name, contents, terms_yn, version, level, active_yn)
VALUES (1, 'JOIN', '이용약관 동의', '이 약관은 서비스 이용에 대한 동의를 포함합니다.', 'Y', 'v1', 2, 'Y');

-- 선택 약관
INSERT INTO terms (parents_no, category, name, contents, terms_yn, version, level, active_yn)
VALUES (1, 'JOIN', '마케팅 목적의 개인정보 수집 및 이용 동의', '마케팅 목적의 개인정보 수집 및 이용 동의', 'N', 'v1', 2, 'Y');

INSERT INTO terms (parents_no, category, name, contents, terms_yn, version, level, active_yn)
VALUES (1, 'JOIN', '광고성 정보 수신 동의', '광고성 정보를 수신하는 것에 대한 동의입니다.', 'N', 'v1', 2, 'Y');

INSERT INTO terms (parents_no, category, name, contents, terms_yn, version, level, active_yn)
VALUES (5, 'JOIN', '문자 수신 동의', '광고성 정보를 수신하는 것에 대한 동의입니다.', 'N', 'v1', 3, 'Y');

INSERT INTO terms (parents_no, category, name, contents, terms_yn, version, level, active_yn)
VALUES (5, 'JOIN', '이메일 수신 동의', '광고성 정보를 수신하는 것에 대한 동의입니다.', 'N', 'v1', 3, 'Y');

-- [ 주문/결제 약관 삽입 ]
INSERT INTO terms (parents_no, category, name, contents, terms_yn, version, level, active_yn)
VALUES (null, 'ORDER', '주문 약관', 'ANKIM 주문/결제 약관', 'Y', 'v1', 1, 'Y');

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

-- 탈퇴사유
INSERT INTO leave_rsn (reason, active_yn) VALUES
                                              ('탈퇴 후 재가입을 위해서', 'Y'),
                                              ('사고 싶은 상품이 없어서', 'Y'),
                                              ('자주 이용하지 않아서', 'Y'),
                                              ('서비스 및 고객지원이 만족스럽지 않아서', 'Y'),
                                              ('광고성 알림이 너무 많이 와서', 'Y'),
                                              ('기타', 'Y');