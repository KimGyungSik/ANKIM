-- 카테고리 데이터 (대분류, 중분류, 소분류)
INSERT INTO category (name, level, parent_no) VALUES ('아우터', 'MIDDLE', NULL);
INSERT INTO category (name, level, parent_no) VALUES ('상의', 'MIDDLE', NULL);

-- 소분류 (중분류 '아우터')
INSERT INTO category (name, level, parent_no) VALUES ('코트', 'SUB', 1);
INSERT INTO category (name, level, parent_no) VALUES ('자켓', 'SUB', 1);
INSERT INTO category (name, level, parent_no) VALUES ('가디건', 'SUB', 1);

-- 소분류 (중분류 '상의')
INSERT INTO category (name, level, parent_no) VALUES ('티셔츠', 'SUB', 2);
INSERT INTO category (name, level, parent_no) VALUES ('블라우스', 'SUB', 2);
INSERT INTO category (name, level, parent_no) VALUES ('니트', 'SUB', 2);

-- 상품 데이터
INSERT INTO product (name, desc, disc_rate, orig_price, qty, selling_status, category_no)
VALUES
    ('캐시미어 코트', '부드럽고 고급스러운 캐시미어 코트', 10, 120000, 100, 'SELLING', 3),
    ('울 자켓', '보온성이 뛰어난 울 자켓', 15, 80000, 150, 'SELLING', 4),
    ('면 티셔츠', '편안한 착용감의 면 티셔츠', 20, 20000, 300, 'SELLING', 6),
    ('니트 스웨터', '포근한 느낌의 니트 스웨터', 25, 40000, 200, 'STOP_SELLING', 8);

-- 옵션 그룹 데이터
INSERT INTO option_group (group_name, product_no) VALUES
                                                      ('컬러', 1),
                                                      ('사이즈', 1),
                                                      ('컬러', 3),
                                                      ('사이즈', 3);

-- 옵션 값 데이터
INSERT INTO option_value (value_name, color_code, option_group_no) VALUES
                                                                       ('블랙', '#000000', 1),
                                                                       ('그레이', '#808080', 1),
                                                                       ('M', NULL, 2),
                                                                       ('L', NULL, 2),
                                                                       ('화이트', '#FFFFFF', 3),
                                                                       ('블루', '#0000FF', 3),
                                                                       ('S', NULL, 4),
                                                                       ('XL', NULL, 4);

-- 품목 데이터
INSERT INTO item (name, code, add_price, qty, saf_qty, max_qty, min_qty, product_no) VALUES
                                                                                         ('색상: 블랙, 사이즈: M', 'P001-BLK-M', 0, 50, 10, 5, 1, 1),
                                                                                         ('색상: 블랙, 사이즈: L', 'P001-BLK-L', 0, 30, 5, 3, 1, 1),
                                                                                         ('색상: 그레이, 사이즈: M', 'P001-GRY-M', 1000, 40, 8, 4, 1, 1),
                                                                                         ('색상: 그레이, 사이즈: L', 'P001-GRY-L', 1000, 20, 5, 2, 1, 1),
                                                                                         ('색상: 화이트, 사이즈: S', 'P003-WHT-S', 0, 100, 20, 10, 1, 3),
                                                                                         ('색상: 블루, 사이즈: XL', 'P003-BLU-XL', 2000, 80, 15, 8, 2, 3);

-- 상품 이미지 데이터
INSERT INTO product_img (img_name, ori_img_name, img_url, repimg_yn, ord, product_no) VALUES
                                                                                          ('cashmere-coat-thumbnail.jpg', '캐시미어 코트 썸네일', 'http://example.com/images/cashmere-coat-thumbnail.jpg', 'Y', 1, 1),
                                                                                          ('cashmere-coat-detail.jpg', '캐시미어 코트 상세', 'http://example.com/images/cashmere-coat-detail.jpg', 'N', 1, 1),
                                                                                          ('cotton-tshirt-thumbnail.jpg', '면 티셔츠 썸네일', 'http://example.com/images/cotton-tshirt-thumbnail.jpg', 'Y', 1, 3),
                                                                                          ('cotton-tshirt-detail.jpg', '면 티셔츠 상세', 'http://example.com/images/cotton-tshirt-detail.jpg', 'N', 1, 3);
