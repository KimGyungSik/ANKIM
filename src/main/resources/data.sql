-- 상품 데이터 삽입
INSERT  INTO product (
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
INSERT  INTO product_img (img_name, img_url, ord, orig_name, prod_no, repimg_yn) VALUES
                                                                                    ('365096f6-bfe5-4937-ba62-8236c5895afb.jpg', 'https://product-uploaded-files.s3.ap-northeast-2.amazonaws.com/4a8e9b33-c985-4443-8245-f34316f11c1a.jpg', 1, '화면 캡처 2025-02-12 214640.jpg', 1051, 'Y'),
                                                                                    ('a579122e-edb0-41b8-935f-6dcf58792bd6.jpg', 'https://product-uploaded-files.s3.ap-northeast-2.amazonaws.com/a6886b27-3982-4c77-91f5-750206e29986.jpg', 2, '화면 캡처 2025-02-12 214657.jpg', 1051, 'Y'),
                                                                                    ('b5e9619b-e629-487b-bead-2e7ccb6b7bf7.jpg', 'https://product-uploaded-files.s3.ap-northeast-2.amazonaws.com/aba28418-ef3c-47ea-9bf1-2159c5734288.jpg', 3, '화면 캡처 2025-02-12 214715.jpg', 1051, 'Y'),
                                                                                    ('9ba9421e-0368-458b-a0b9-bf7f43ff22c8.jpg', 'https://product-uploaded-files.s3.ap-northeast-2.amazonaws.com/9be24280-9297-4cc1-9e8c-c8974afc8f3c.jpg', 4, '화면 캡처 2025-02-12 214731.jpg', 1051, 'Y'),
                                                                                    ('38a8e1e8-8102-46f3-9f49-209e7f7febed.jpg', 'https://product-uploaded-files.s3.ap-northeast-2.amazonaws.com/e2a85a22-b3bc-412a-80fa-5e4a22f00e87.jpg', 1, 'ㅇㅇㅇㅇㅇ 10.jpg', 1051, 'N'),
                                                                                    ('80ff23cf-1299-476b-8030-4b050fc9eed9.jpg', 'https://product-uploaded-files.s3.ap-northeast-2.amazonaws.com/e67aa6ac-a160-43e0-88ae-5cfad6055ad9.jpg', 2, 'ㅇㅇㅇㅇㅇ 5.jpg', 1051, 'N'),
                                                                                    ('d7e8fe33-f4cb-4fcb-9037-192fb99af228.jpg', 'https://product-uploaded-files.s3.ap-northeast-2.amazonaws.com/7cce16d2-e480-4f1c-a8c2-ded11b0d2724.jpg', 3, 'ㅇㅇㅇㅇㅇ 9.jpg', 1051, 'N'),
                                                                                    ('fc614454-d3bc-4b40-88e1-374873ce500f.jpg', 'https://product-uploaded-files.s3.ap-northeast-2.amazonaws.com/ee788efa-fc50-4d89-aa6e-0ff2ac7e6702.jpg', 4, 'ㅇㅇㅇㅇㅇ 8.jpg', 1051, 'N');




-- 옵션 그룹 삽입
INSERT  INTO option_group (name, prod_no) VALUES
                                             ('사이즈', 1051),
                                             ('컬러', 1051);

-- 옵션 값 삽입
INSERT  INTO option_value (color_code, name, optg_no) VALUES
                                                         (NULL, 'small', 2101),
                                                         (NULL, 'large', 2101),
                                                         ('#0000FF', 'blue', 2102),
                                                         ('#FF0000', 'red', 2102);

-- 상품 아이템 삽입
INSERT  INTO item (add_price, code, max_qty, min_qty, name, prod_no, qty, saf_qty, selling_status, thumbnail_img_url, total_price) VALUES
                                                                                                                                      (3000, '1962858-1', 40, 5, '사이즈: small, 컬러: blue', 1051, 5, 40, 'SELLING', 'https://product-uploaded-files.s3.ap-northeast-2.amazonaws.com/4a8e9b33-c985-4443-8245-f34316f11c1a.jpg', 27500),
                                                                                                                                      (2000, '1962858-2', 40, 5, '사이즈: small, 컬러: red', 1051, 40, 40, 'SELLING', 'https://product-uploaded-files.s3.ap-northeast-2.amazonaws.com/4a8e9b33-c985-4443-8245-f34316f11c1a.jpg', 27500),
                                                                                                                                      (1000, '1962858-3', 40, 5, '사이즈: large, 컬러: blue', 1051, 40, 40, 'SELLING', 'https://product-uploaded-files.s3.ap-northeast-2.amazonaws.com/4a8e9b33-c985-4443-8245-f34316f11c1a.jpg', 27500),
                                                                                                                                      (5000, '1962858-4', 40, 5, '사이즈: large, 컬러: red', 1051, 40, 40, 'SELLING', 'https://product-uploaded-files.s3.ap-northeast-2.amazonaws.com/4a8e9b33-c985-4443-8245-f34316f11c1a.jpg', 27500);

-- 아이템 옵션 연결
INSERT  INTO item_option (item_no, optv_no) VALUES
                                               (2101,3151), (2101,3153),
                                               (2102,3151), (2102,3154),
                                               (2103,3152), (2103,3153),
                                               (2104,3152), (2104,3154);


















-- 인기 검색어
-- INSERT INTO search_logs (keyword, search_count) VALUES
--                                                                            ('데님1', FLOOR(1 + RAND() * 50)),
--                                                                             ('데님2', FLOOR(1 + RAND() * 50)),
--                                                                            ('데님3', FLOOR(1 + RAND() * 50)),
--                                                                            ('팬츠1', FLOOR(1 + RAND() * 50)),
--                                                                            ('팬츠2', FLOOR(1 + RAND() * 50)),
--                                                                            ('팬츠3', FLOOR(1 + RAND() * 50)),
--                                                                            ('슬랙스1', FLOOR(1 + RAND() * 50)),
--                                                                            ('슬랙스2', FLOOR(1 + RAND() * 50)),
--                                                                            ('슬랙스3', FLOOR(1 + RAND() * 50)),
--                                                                            ('니트1', FLOOR(1 + RAND() * 50)),
--                                                                             ('니트2', FLOOR(1 + RAND() * 50)),
--                                                                            ('니트3', FLOOR(1 + RAND() * 50)),
--                                                                            ('코트1', FLOOR(1 + RAND() * 50)),
--                                                                            ('코트2', FLOOR(1 + RAND() * 50)),
--                                                                            ('코트3', FLOOR(1 + RAND() * 50)),
--                                                                            ('티셔츠1', FLOOR(1 + RAND() * 50)),
--                                                                            ('티셔츠2', FLOOR(1 + RAND() * 50)),
--                                                                            ('티셔츠3', FLOOR(1 + RAND() * 50)),
--                                                                            ('셔츠1', FLOOR(1 + RAND() * 50)),
--                                                                            ('셔츠2', FLOOR(1 + RAND() * 50));

INSERT INTO product (
    avg_rating, category_no, caution_order, caution_product, caution_shipping, code,
    description, discount_rate, display_order, free_shipping, handmade,
    name, option_available, orig_price, qna_count, qty, rel_prod_code,
    restock_available, review_count, search_keywords, sell_price, selling_status,
    ship_fee, view_count, wish_count
) VALUES (
             0, 18,
             '* 교환 및 반품이 불가능한 경우 *
             - 고객님의 책임 있는 사유로 상품이 멸실 또는 훼손된 경우 (상품 내용 확인을 위한 포장 훼손 제외)
             - 포장을 개봉하거나 훼손하여 상품 가치가 상실된 경우
             - 고객님의 사용 또는 일부 소비로 인해 상품 가치가 현저히 감소한 경우',

             '※ 다양한 컬러 특성상 각 상품마다 사이즈 스펙에 오차가 있을 수 있습니다.',

             'Delivery 브랜드 업체발송은 상품설명에 기입된 배송 공지 기준으로 출고됩니다.
             Delivery 29CM 자체발송의 경우, 오후 2시까지 결제 확인된 주문은 당일 출고되며,
             5만원 이상 주문은 무료배송, 5만원 미만 주문은 3,000원의 배송비가 추가됩니다.',

             '1962859',

             '밋밋한 반팔 티셔츠가 이제 질렸다면? 툭 입어도 포인트가 되어줄 레터링 티셔츠!',

             55, NULL, 'Y', 'Y',

             '[늘어짐X/갓성비]늘어짐 하나없는, 탄탄 레터링 반팔 티셔츠', 'Y', 25500, 0, 0, '', 'Y', 0,
             '티셔츠, white, khaki, charcoal, black', 11475, 'SELLING', 0, 0, 500
         );



insert
into
    product_img
(img_name, img_url, ord, orig_name, prod_no, repimg_yn)
values
    ('a7b0f7b0-1272-45b5-90ba-d80458c3ecc1.jpg', 'https://product-uploaded-files.s3.ap-northeast-2.amazonaws.com/4c28fb4e-880a-4edb-be7a-e104c6dea54d.jpg', 1, '화면 캡처 2025-04-25 204037.jpg', 1052, 'Y');


insert
into
    product_img
(img_name, img_url, ord, orig_name, prod_no, repimg_yn)
values
    ('2ec656be-778a-4742-ab1f-f823b06eff7b.jpg', 'https://product-uploaded-files.s3.ap-northeast-2.amazonaws.com/2d86e944-3985-4b1f-9b4c-06185a0c37ae.jpg', 2, '화면 캡처 2025-04-25 204123.jpg', 1052, 'Y');

insert
into
    product_img
(img_name, img_url, ord, orig_name, prod_no, repimg_yn)
values
    ('316375ca-9ccd-442b-854e-23a8dbc23107.jpg', 'https://product-uploaded-files.s3.ap-northeast-2.amazonaws.com/79256225-0e07-4a93-ae02-42e2f31dbc47.jpg', 3, '화면 캡처 2025-04-25 204145.jpg', 1052, 'Y');

insert
into
    product_img
(img_name, img_url, ord, orig_name, prod_no, repimg_yn)
values
    ('facfbc70-0f57-4b6c-b273-018a19f768da.jpg', 'https://product-uploaded-files.s3.ap-northeast-2.amazonaws.com/a1015ad0-920d-4b1c-aeee-dc747d0e8e33.jpg', 1, '화면 캡처 2025-04-25 204206.jpg', 1052, 'N');

insert
into
    product_img
(img_name, img_url, ord, orig_name, prod_no, repimg_yn)
values
    ('2a67800f-95bf-4147-ba62-68bdb6bb507a.jpg', 'https://product-uploaded-files.s3.ap-northeast-2.amazonaws.com/db5b39c5-8780-455f-8cb8-9bc13a755684.jpg', 2, '화면 캡처 2025-04-25 204221.jpg', 1052, 'N');

insert
into
    product_img
(img_name, img_url, ord, orig_name, prod_no, repimg_yn)
values
    ('efe108c0-9f78-43ce-a4de-2b2b1b414736.jpg', 'https://product-uploaded-files.s3.ap-northeast-2.amazonaws.com/aa2d576e-5964-46a2-a4ba-bad2171d5860.jpg', 3, '화면 캡처 2025-04-25 204243.jpg', 1052, 'N');

insert
into
    option_group
(name, prod_no)
values
    ('사이즈', 1052);

insert
into
    option_value
(color_code, name, optg_no)
values
    (NULL, 'small', 2103);

insert
into
    option_value
(color_code, name, optg_no)
values
    (NULL, 'large', 2103);

insert
into
    option_group
(name, prod_no)
values
    ('컬러', 1052);

insert
into
    option_value
(color_code, name, optg_no)
values
    ('#FFFFFF', 'white', 2104);

insert
into
    option_value
(color_code, name, optg_no)
values
    ('#BDB76B', 'khaki', 2104);

insert
into
    option_value
(color_code, name, optg_no)
values
    ('#000000', 'black', 2104);

insert
into
    item
(add_price, code, max_qty, min_qty, name, prod_no, qty, saf_qty, selling_status, thumbnail_img_url, total_price)
values
    (0, '1962859-1', 40, 1, '사이즈: small, 컬러: white', 1052, 40, 40, 'SELLING', 'https://product-uploaded-files.s3.ap-northeast-2.amazonaws.com/4c28fb4e-880a-4edb-be7a-e104c6dea54d.jpg', 25500);
insert
into
    item_option
(item_no, optv_no)
values
    (2105, 3155);
insert
into
    item_option
(item_no, optv_no)
values
    (2105, 3157);
insert
into
    item
(add_price, code, max_qty, min_qty, name, prod_no, qty, saf_qty, selling_status, thumbnail_img_url, total_price)
values
    (0, '1962859-2', 40, 1, '사이즈: small, 컬러: khaki', 1052, 40, 40, 'SELLING', 'https://product-uploaded-files.s3.ap-northeast-2.amazonaws.com/4c28fb4e-880a-4edb-be7a-e104c6dea54d.jpg', 25500);
insert
into
    item_option
(item_no, optv_no)
values
    (2106, 3155);
insert
into
    item_option
(item_no, optv_no)
values
    (2106, 3158);
insert
into
    item
(add_price, code, max_qty, min_qty, name, prod_no, qty, saf_qty, selling_status, thumbnail_img_url, total_price)
values
    (0, '1962859-3', 40, 1, '사이즈: small, 컬러: black', 1052, 40, 40, 'SELLING', 'https://product-uploaded-files.s3.ap-northeast-2.amazonaws.com/4c28fb4e-880a-4edb-be7a-e104c6dea54d.jpg', 25500);
insert
into
    item_option
(item_no, optv_no)
values
    (2107, 3155);
insert
into
    item_option
(item_no, optv_no)
values
    (2107, 3159);
insert
into
    item
(add_price, code, max_qty, min_qty, name, prod_no, qty, saf_qty, selling_status, thumbnail_img_url, total_price)
values
    (0, '1962859-4', 40, 1, '사이즈: large, 컬러: white', 1052, 40, 40, 'SELLING', 'https://product-uploaded-files.s3.ap-northeast-2.amazonaws.com/4c28fb4e-880a-4edb-be7a-e104c6dea54d.jpg', 25500);
insert
into
    item_option
(item_no, optv_no)
values
    (2108, 3156);
insert
into
    item_option
(item_no, optv_no)
values
    (2108, 3157);
insert
into
    item
(add_price, code, max_qty, min_qty, name, prod_no, qty, saf_qty, selling_status, thumbnail_img_url, total_price)
values
    (0, '1962859-5', 40, 1, '사이즈: large, 컬러: khaki', 1052, 40, 40, 'SELLING', 'https://product-uploaded-files.s3.ap-northeast-2.amazonaws.com/4c28fb4e-880a-4edb-be7a-e104c6dea54d.jpg', 25500);
insert
into
    item_option
(item_no, optv_no)
values
    (2109, 3156);
insert
into
    item_option
(item_no, optv_no)
values
    (2109, 3158);
insert
into
    item
(add_price, code, max_qty, min_qty, name, prod_no, qty, saf_qty, selling_status, thumbnail_img_url, total_price)
values
    (0, '1962859-6', 40, 1, '사이즈: large, 컬러: black', 1052, 40, 40, 'SELLING', 'https://product-uploaded-files.s3.ap-northeast-2.amazonaws.com/4c28fb4e-880a-4edb-be7a-e104c6dea54d.jpg', 25500);
insert
into
    item_option
(item_no, optv_no)
values
    (2110, 3156);
insert
into
    item_option
(item_no, optv_no)
values
    (2110, 3159);

INSERT INTO product (
    avg_rating, category_no, caution_order, caution_product, caution_shipping, code,
    description, discount_rate, display_order, free_shipping, handmade,
    name, option_available, orig_price, qna_count, qty, rel_prod_code,
    restock_available, review_count, search_keywords, sell_price, selling_status,
    ship_fee, view_count, wish_count
) VALUES (
             0, 18,
             '* 교환 및 반품이 불가능한 경우 *
             - 고객님의 책임 있는 사유로 상품이 멸실 또는 훼손된 경우 (상품 내용 확인을 위한 포장 훼손 제외)
             - 포장을 개봉하거나 훼손하여 상품 가치가 상실된 경우
             - 고객님의 사용 또는 일부 소비로 인해 상품 가치가 현저히 감소한 경우',

             '※ 다양한 컬러 특성상 각 상품마다 사이즈 스펙에 오차가 있을 수 있습니다.',

             'Delivery 브랜드 업체발송은 상품설명에 기입된 배송 공지 기준으로 출고됩니다.
             Delivery 29CM 자체발송의 경우, 오후 2시까지 결제 확인된 주문은 당일 출고되며,
             5만원 이상 주문은 무료배송, 5만원 미만 주문은 3,000원의 배송비가 추가됩니다.',

             '1962860',

             '매일 꺼내 입을 기본 반팔티',

             40, NULL, 'Y', 'Y',

             '[팔말라핏/국내제작!]팔뚝살 부각없는, 라이프 베이직 스퀘어넥 반팔 티셔츠',
             'Y', 15840, 0, 0, '', 'Y', 0,
             '티셔츠, ivory, gray, navy', 9504, 'SELLING', 0, 0, 500
         );


insert
into
    product_img
(img_name, img_url, ord, orig_name, prod_no, repimg_yn)
values
    ('f8f3a8fd-f076-4639-b718-afa801c199b4.jpg', 'https://product-uploaded-files.s3.ap-northeast-2.amazonaws.com/7dcf134e-4b02-4592-bf05-82fb0f3f2753.jpg', 1, '화면 캡처 2025-04-25 215457.jpg', 1053, 'Y');

insert
into
    product_img
(img_name, img_url, ord, orig_name, prod_no, repimg_yn)
values
    ('43adbd84-852c-40ce-9816-2d504e905e6c.jpg', 'https://product-uploaded-files.s3.ap-northeast-2.amazonaws.com/a9c3c888-5418-422f-9dee-b30fb0fcfb1c.jpg', 2, '화면 캡처 2025-04-25 215530.jpg', 1053, 'Y');


insert
into
    product_img
(img_name, img_url, ord, orig_name, prod_no, repimg_yn)
values
    ('85f57b50-4b12-489e-b4dd-f8d47fe681f0.jpg', 'https://product-uploaded-files.s3.ap-northeast-2.amazonaws.com/7af6aff4-aa2e-4e5a-8099-4ab6ef5670df.jpg', 3, '화면 캡처 2025-04-25 215625.jpg', 1053, 'Y');

insert
into
    product_img
(img_name, img_url, ord, orig_name, prod_no, repimg_yn)
values
    ('ba8fe2fe-5dec-4501-a3bf-029c6581efc3.jpg', 'https://product-uploaded-files.s3.ap-northeast-2.amazonaws.com/29146e7a-a594-44f9-b2e0-4d5b236a0f97.jpg', 1, '화면 캡처 2025-04-25 215656.jpg', 1053, 'N');

insert
into
    product_img
(img_name, img_url, ord, orig_name, prod_no, repimg_yn)
values
    ('3ac173b6-5141-43da-8135-924bf60cd8e5.jpg', 'https://product-uploaded-files.s3.ap-northeast-2.amazonaws.com/0c142b59-466c-445e-88d0-b25cc7bcb1d5.jpg', 2, '화면 캡처 2025-04-25 215721.jpg', 1053, 'N');


insert
into
    option_group
(name, prod_no)
values
    ('사이즈', 1053);

insert
into
    option_value
(color_code, name, optg_no)
values
    (NULL, 'small', 2105);

insert
into
    option_value
(color_code, name, optg_no)
values
    (NULL, 'large', 2105);

insert
into
    option_group
(name, prod_no)
values
    ('컬러', 1053);

insert
into
    option_value
(color_code, name, optg_no)
values
    ('#FFFFF0', 'ivory', 2106);
insert
into
    option_value
(color_code, name, optg_no)
values
    ('#808080', 'gray', 2106);
insert
into
    option_value
(color_code, name, optg_no)
values
    ('#000080', 'navy', 2106);
insert
into
    item
(add_price, code, max_qty, min_qty, name, prod_no, qty, saf_qty, selling_status, thumbnail_img_url, total_price)
values
    (0, '1962860-1', 40, 1, '사이즈: small, 컬러: ivory', 1053, 40, 40, 'SELLING', 'https://product-uploaded-files.s3.ap-northeast-2.amazonaws.com/7dcf134e-4b02-4592-bf05-82fb0f3f2753.jpg', 15840);
insert
into
    item_option
(item_no, optv_no)
values
    (2111, 3160);
insert
into
    item_option
(item_no, optv_no)
values
    (2111, 3162);
insert
into
    item
(add_price, code, max_qty, min_qty, name, prod_no, qty, saf_qty, selling_status, thumbnail_img_url, total_price)
values
    (0, '1962860-2', 40, 1, '사이즈: small, 컬러: gray', 1053, 40, 40, 'SELLING', 'https://product-uploaded-files.s3.ap-northeast-2.amazonaws.com/7dcf134e-4b02-4592-bf05-82fb0f3f2753.jpg', 15840);

insert
into
    item_option
(item_no, optv_no)
values
    (2112, 3160);
insert
into
    item_option
(item_no, optv_no)
values
    (2112, 3163);
insert
into
    item
(add_price, code, max_qty, min_qty, name, prod_no, qty, saf_qty, selling_status, thumbnail_img_url, total_price)
values
    (0, '1962860-3', 40, 1, '사이즈: small, 컬러: navy', 1053, 40, 40, 'SELLING', 'https://product-uploaded-files.s3.ap-northeast-2.amazonaws.com/7dcf134e-4b02-4592-bf05-82fb0f3f2753.jpg', 15840);
insert
into
    item_option
(item_no, optv_no)
values
    (2113, 3160);
insert
into
    item_option
(item_no, optv_no)
values
    (2113, 3164);
insert
into
    item
(add_price, code, max_qty, min_qty, name, prod_no, qty, saf_qty, selling_status, thumbnail_img_url, total_price)
values
    (0, '1962860-4', 40, 1, '사이즈: large, 컬러: ivory', 1053, 40, 40, 'SELLING', 'https://product-uploaded-files.s3.ap-northeast-2.amazonaws.com/7dcf134e-4b02-4592-bf05-82fb0f3f2753.jpg', 15840);
insert
into
    item_option
(item_no, optv_no)
values
    (2114, 3161);
insert
into
    item_option
(item_no, optv_no)
values
    (2114, 3162);
insert
into
    item
(add_price, code, max_qty, min_qty, name, prod_no, qty, saf_qty, selling_status, thumbnail_img_url, total_price)
values
    (0, '1962860-5', 40, 1, '사이즈: large, 컬러: gray', 1053, 40, 40, 'SELLING', 'https://product-uploaded-files.s3.ap-northeast-2.amazonaws.com/7dcf134e-4b02-4592-bf05-82fb0f3f2753.jpg', 15840);
insert
into
    item_option
(item_no, optv_no)
values
    (2115, 3161);
insert
into
    item_option
(item_no, optv_no)
values
    (2115, 3163);
insert
into
    item
(add_price, code, max_qty, min_qty, name, prod_no, qty, saf_qty, selling_status, thumbnail_img_url, total_price)
values
    (0, '1962860-6', 40, 1, '사이즈: large, 컬러: navy', 1053, 40, 40, 'SELLING', 'https://product-uploaded-files.s3.ap-northeast-2.amazonaws.com/7dcf134e-4b02-4592-bf05-82fb0f3f2753.jpg', 15840);

insert
into
    item_option
(item_no, optv_no)
values
    (2116, 3161);
insert
into
    item_option
(item_no, optv_no)
values
    (2116, 3164);

INSERT INTO product (
    avg_rating, category_no, caution_order, caution_product, caution_shipping, code,
    description, discount_rate, display_order, free_shipping, handmade,
    name, option_available, orig_price, qna_count, qty, rel_prod_code,
    restock_available, review_count, search_keywords, sell_price, selling_status,
    ship_fee, view_count, wish_count
) VALUES (
             0, 18,
             '* 교환 및 반품이 불가능한 경우 *
             - 고객님의 책임 있는 사유로 상품이 멸실 또는 훼손된 경우 (상품 내용 확인을 위한 포장 훼손 제외)
             - 포장을 개봉하거나 훼손하여 상품 가치가 상실된 경우
             - 고객님의 사용 또는 일부 소비로 인해 상품 가치가 현저히 감소한 경우',

             '※ 다양한 컬러 특성상 각 상품마다 사이즈 스펙에 오차가 있을 수 있습니다.',

             'Delivery 브랜드 업체발송은 상품설명에 기입된 배송 공지 기준으로 출고됩니다.
             Delivery 29CM 자체발송의 경우, 오후 2시까지 결제 확인된 주문은 당일 출고되며,
             5만원 이상 주문은 무료배송, 5만원 미만 주문은 3,000원의 배송비가 추가됩니다.',

             '1962861',

             '원단부터 디자인까지 하나하나 신경쓴 반팔티',

             10, NULL, 'Y', 'Y',

             '[당일출고][1+1할인/남여공용] 나파밸리 오버핏 반팔티 (3color)',
             'Y', 11000, 0, 0, '', 'Y', 0,
             '티셔츠, ivory, black', 9900, 'SELLING', 0, 0, 490
         );

insert
into
    product_img
(img_name, img_url, ord, orig_name, prod_no, repimg_yn)
values
    ('c4968416-3e6e-4bef-923f-0ac8e6bfac17.jpg', 'https://product-uploaded-files.s3.ap-northeast-2.amazonaws.com/495dc10d-88ea-407d-8ce1-55c745b725a7.jpg', 1, '화면 캡처 2025-04-25 221614.jpg', 1054, 'Y');

insert
into
    product_img
(img_name, img_url, ord, orig_name, prod_no, repimg_yn)
values
    ('0ff7e740-3486-4fbf-a1d0-43eee7ae1e55.jpg', 'https://product-uploaded-files.s3.ap-northeast-2.amazonaws.com/eb8ea575-1cca-4eb5-97a4-7010346e50e1.jpg', 2, '화면 캡처 2025-04-25 221636.jpg', 1054, 'Y');
insert
into
    product_img
(img_name, img_url, ord, orig_name, prod_no, repimg_yn)
values
    ('8cc1d8e9-4dc7-4679-ba6a-357dd29162bf.jpg', 'https://product-uploaded-files.s3.ap-northeast-2.amazonaws.com/2384de68-b551-4969-b62c-e24b78908098.jpg', 3, '화면 캡처 2025-04-25 221656.jpg', 1054, 'Y');
insert
into
    product_img
(img_name, img_url, ord, orig_name, prod_no, repimg_yn)
values
    ('68140285-0a9c-4a59-a6af-42d133c87592.jpg', 'https://product-uploaded-files.s3.ap-northeast-2.amazonaws.com/5ebbdcc2-7f3d-40b3-b71b-51f991743ab4.jpg', 1, '화면 캡처 2025-04-25 221714.jpg', 1054, 'N');
insert
into
    product_img
(img_name, img_url, ord, orig_name, prod_no, repimg_yn)
values
    ('d6863ecb-cd44-4aba-886d-0912cad36615.jpg', 'https://product-uploaded-files.s3.ap-northeast-2.amazonaws.com/d7057431-a423-4667-8c26-4494558477d7.jpg', 2, '화면 캡처 2025-04-25 221727.jpg', 1054, 'N');


insert
into
    option_group
(name, prod_no)
values
    ('사이즈', 1054);

insert
into
    option_value
(color_code, name, optg_no)
values
    (NULL, 'small', 2107);
insert
into
    option_value
(color_code, name, optg_no)
values
    (NULL, 'large', 2107);
insert
into
    option_group
(name, prod_no)
values
    ('컬러', 1054);
insert
into
    option_value
(color_code, name, optg_no)
values
    ('#FFFFF0', 'ivory', 2108);
insert
into
    option_value
(color_code, name, optg_no)
values
    ('#000000', 'black', 2108);
insert
into
    item
(add_price, code, max_qty, min_qty, name, prod_no, qty, saf_qty, selling_status, thumbnail_img_url, total_price)
values
    (0, '1962861-1', 40, 1, '사이즈: small, 컬러: ivory', 1054, 40, 40, 'SELLING', 'https://product-uploaded-files.s3.ap-northeast-2.amazonaws.com/495dc10d-88ea-407d-8ce1-55c745b725a7.jpg', 11000);
insert
into
    item_option
(item_no, optv_no)
values
    (2117, 3165);
insert
into
    item_option
(item_no, optv_no)
values
    (2117, 3167);

insert
into
    item
(add_price, code, max_qty, min_qty, name, prod_no, qty, saf_qty, selling_status, thumbnail_img_url, total_price)
values
    (0, '1962861-2', 40, 1, '사이즈: small, 컬러: black', 1054, 40, 40, 'SELLING', 'https://product-uploaded-files.s3.ap-northeast-2.amazonaws.com/495dc10d-88ea-407d-8ce1-55c745b725a7.jpg', 11000);
insert
into
    item_option
(item_no, optv_no)
values
    (2118, 3165);
insert
into
    item_option
(item_no, optv_no)
values
    (2118, 3168);
insert
into
    item
(add_price, code, max_qty, min_qty, name, prod_no, qty, saf_qty, selling_status, thumbnail_img_url, total_price)
values
    (0, '1962861-3', 40, 1, '사이즈: large, 컬러: ivory', 1054, 40, 40, 'SELLING', 'https://product-uploaded-files.s3.ap-northeast-2.amazonaws.com/495dc10d-88ea-407d-8ce1-55c745b725a7.jpg', 11000);
insert
into
    item_option
(item_no, optv_no)
values
    (2119, 3166);
insert
into
    item_option
(item_no, optv_no)
values
    (2119, 3167);
insert
into
    item
(add_price, code, max_qty, min_qty, name, prod_no, qty, saf_qty, selling_status, thumbnail_img_url, total_price)
values
    (0, '1962861-4', 40, 1, '사이즈: large, 컬러: black', 1054, 40, 40, 'SELLING', 'https://product-uploaded-files.s3.ap-northeast-2.amazonaws.com/495dc10d-88ea-407d-8ce1-55c745b725a7.jpg', 11000);
insert
into
    item_option
(item_no, optv_no)
values
    (2120, 3166);
insert
into
    item_option
(item_no, optv_no)
values
    (2120, 3168);

INSERT INTO product (
    avg_rating, category_no, caution_order, caution_product, caution_shipping, code,
    description, discount_rate, display_order, free_shipping, handmade,
    name, option_available, orig_price, qna_count, qty, rel_prod_code,
    restock_available, review_count, search_keywords, sell_price, selling_status,
    ship_fee, view_count, wish_count
) VALUES (
             0, 18,
             '* 교환 및 반품이 불가능한 경우 *
             - 고객님의 책임 있는 사유로 상품이 멸실 또는 훼손된 경우 (상품 내용 확인을 위한 포장 훼손 제외)
             - 포장을 개봉하거나 훼손하여 상품 가치가 상실된 경우
             - 고객님의 사용 또는 일부 소비로 인해 상품 가치가 현저히 감소한 경우',

             '※ 다양한 컬러 특성상 각 상품마다 사이즈 스펙에 오차가 있을 수 있습니다.',

             'Delivery 브랜드 업체발송은 상품설명에 기입된 배송 공지 기준으로 출고됩니다.
             Delivery 29CM 자체발송의 경우, 오후 2시까지 결제 확인된 주문은 당일 출고되며,
             5만원 이상 주문은 무료배송, 5만원 미만 주문은 3,000원의 배송비가 추가됩니다.',

             '1962862',

             '코튼 재질로 튼튼해요!',

             0, NULL, 'Y', 'N',

             '투캣츠 피그먼트 프린팅 루즈핏 반팔 티셔츠',
             'Y', 21500, 0, 0, '', 'Y', 0,
             '티셔츠, black', 21500, 'SELLING', 0, 0, 481
         );




insert
into
    product_img
(img_name, img_url, ord, orig_name, prod_no, repimg_yn)
values
    ('dd1fdf7b-4674-48a0-b2f1-c92b68aa49b7.jpg', 'https://product-uploaded-files.s3.ap-northeast-2.amazonaws.com/8b4f8e34-03c3-4ea8-9dc9-cc99b2f4e1fe.jpg', 1, '화면 캡처 2025-04-25 222739.jpg', 1055, 'Y');


insert
into
    product_img
(img_name, img_url, ord, orig_name, prod_no, repimg_yn)
values
    ('3d75c9c3-f191-439e-a5af-797cfbfb0445.jpg', 'https://product-uploaded-files.s3.ap-northeast-2.amazonaws.com/8154f4e0-ceb5-43c9-b5fd-3d7f97cd63a1.jpg', 2, '화면 캡처 2025-04-25 222805.jpg', 1055, 'Y');

insert
into
    product_img
(img_name, img_url, ord, orig_name, prod_no, repimg_yn)
values
    ('616ede3e-ec71-4ff4-8a19-fd7413794269.jpg', 'https://product-uploaded-files.s3.ap-northeast-2.amazonaws.com/99fa9ab3-a16b-4a93-90b4-0c5b07c528d0.jpg', 1, '화면 캡처 2025-04-25 222824.jpg', 1055, 'N');

insert
into
    product_img
(img_name, img_url, ord, orig_name, prod_no, repimg_yn)
values
    ('004f0aa2-9757-478a-8da8-11cb2d952860.jpg', 'https://product-uploaded-files.s3.ap-northeast-2.amazonaws.com/0e7df96a-5623-49d1-b3de-6967dd570d4e.jpg', 2, '화면 캡처 2025-04-25 222848.jpg', 1055, 'N');

insert
into
    option_group
(name, prod_no)
values
    ('사이즈', 1055);
insert
into
    option_value
(color_code, name, optg_no)
values
    (NULL, 'small', 2109);
insert
into
    option_group
(name, prod_no)
values
    ('컬러', 1055);
insert
into
    option_value
(color_code, name, optg_no)
values
    ('#000000', 'black', 2110);
insert
into
    item
(add_price, code, max_qty, min_qty, name, prod_no, qty, saf_qty, selling_status, thumbnail_img_url, total_price)
values
    (0, '1962862-1', 40, 1, '사이즈: small, 컬러: black', 1055, 40, 40, 'SELLING', 'https://product-uploaded-files.s3.ap-northeast-2.amazonaws.com/8b4f8e34-03c3-4ea8-9dc9-cc99b2f4e1fe.jpg', 21500);
insert
into
    item_option
(item_no, optv_no)
values
    (2121, 3169);
insert
into
    item_option
(item_no, optv_no)
values
    (2121, 3170);

INSERT INTO product (
    avg_rating, category_no, caution_order, caution_product, caution_shipping, code,
    description, discount_rate, display_order, free_shipping, handmade,
    name, option_available, orig_price, qna_count, qty, rel_prod_code,
    restock_available, review_count, search_keywords, sell_price, selling_status,
    ship_fee, view_count, wish_count
) VALUES (
             0, 18,
             '* 교환 및 반품이 불가능한 경우 *
             - 고객님의 책임 있는 사유로 상품이 멸실 또는 훼손된 경우 (상품 내용 확인을 위한 포장 훼손 제외)
             - 포장을 개봉하거나 훼손하여 상품 가치가 상실된 경우
             - 고객님의 사용 또는 일부 소비로 인해 상품 가치가 현저히 감소한 경우',

             '※ 다양한 컬러 특성상 각 상품마다 사이즈 스펙에 오차가 있을 수 있습니다.',

             'Delivery 브랜드 업체발송은 상품설명에 기입된 배송 공지 기준으로 출고됩니다.
             Delivery 29CM 자체발송의 경우, 오후 2시까지 결제 확인된 주문은 당일 출고되며,
             5만원 이상 주문은 무료배송, 5만원 미만 주문은 3,000원의 배송비가 추가됩니다.',

             '1962863',

             '스포티지하게 즐기기 좋은 반팔티 소개드릴게요!',

             0, NULL, 'Y', 'N',

             '페리엠 슬림핏 스포티룩 2COL 봄 여름 러브리본반팔티',
             'Y', 25900, 0, 0, '', 'Y', 0,
             '티셔츠, navy, white', 25900, 'SELLING', 0, 0, 482
         );


insert
into
    product_img
(img_name, img_url, ord, orig_name, prod_no, repimg_yn)
values
    ('36645215-a1c5-40aa-9af2-bf1027a2c819.jpg', 'https://product-uploaded-files.s3.ap-northeast-2.amazonaws.com/48df53a5-341f-4de4-8803-8dfe3458d11c.jpg', 1, '화면 캡처 2025-04-25 222932.jpg', 1056, 'Y');

insert
into
    product_img
(img_name, img_url, ord, orig_name, prod_no, repimg_yn)
values
    ('368ea085-6f13-4c80-a857-ffbb5f2d8290.jpg', 'https://product-uploaded-files.s3.ap-northeast-2.amazonaws.com/79bd8587-389e-4339-9a12-584e91a66c58.jpg', 2, '화면 캡처 2025-04-25 222944.jpg', 1056, 'Y');

insert
into
    product_img
(img_name, img_url, ord, orig_name, prod_no, repimg_yn)
values
    ('a9292755-9584-42a9-80b7-77056b4cfbfa.jpg', 'https://product-uploaded-files.s3.ap-northeast-2.amazonaws.com/ed8aaa8d-6830-4078-8ce5-e1fd7d406b48.jpg', 1, '화면 캡처 2025-04-25 222959.jpg', 1056, 'N');
insert
into
    product_img
(img_name, img_url, ord, orig_name, prod_no, repimg_yn)
values
    ('384f7904-6ea8-4d4c-8627-0e2814ce618e.jpg', 'https://product-uploaded-files.s3.ap-northeast-2.amazonaws.com/13a8e618-f8f0-4353-ba42-80f078923b86.jpg', 2, '화면 캡처 2025-04-25 223016.jpg', 1056, 'N');
insert
into
    option_group
(name, prod_no)
values
    ('사이즈', 1056);
insert
into
    option_value
(color_code, name, optg_no)
values
    (NULL, 'free', 2111);
insert
into
    option_group
(name, prod_no)
values
    ('컬러', 1056);
insert
into
    option_value
(color_code, name, optg_no)
values
    ('#000080', 'navy', 2112);
insert
into
    option_value
(color_code, name, optg_no)
values
    ('#FFFFFF', 'white', 2112);

insert
into
    item
(add_price, code, max_qty, min_qty, name, prod_no, qty, saf_qty, selling_status, thumbnail_img_url, total_price)
values
    (0, '1962863-1', 40, 1, '사이즈: free, 컬러: navy', 1056, 40, 40, 'SELLING', 'https://product-uploaded-files.s3.ap-northeast-2.amazonaws.com/48df53a5-341f-4de4-8803-8dfe3458d11c.jpg', 25900);
insert
into
    item_option
(item_no, optv_no)
values
    (2122, 3171);
insert
into
    item_option
(item_no, optv_no)
values
    (2122, 3172);
insert
into
    item
(add_price, code, max_qty, min_qty, name, prod_no, qty, saf_qty, selling_status, thumbnail_img_url, total_price)
values
    (0, '1962863-2', 40, 1, '사이즈: free, 컬러: white', 1056, 40, 40, 'SELLING', 'https://product-uploaded-files.s3.ap-northeast-2.amazonaws.com/48df53a5-341f-4de4-8803-8dfe3458d11c.jpg', 25900);
insert
into
    item_option
(item_no, optv_no)
values
    (2123, 3171);
insert
into
    item_option
(item_no, optv_no)
values
    (2123, 3173);

INSERT INTO product (
    avg_rating, category_no, caution_order, caution_product, caution_shipping, code,
    description, discount_rate, display_order, free_shipping, handmade,
    name, option_available, orig_price, qna_count, qty, rel_prod_code,
    restock_available, review_count, search_keywords, sell_price, selling_status,
    ship_fee, view_count, wish_count
) VALUES (
             0, 18,
             '* 교환 및 반품이 불가능한 경우 *
             - 고객님의 책임 있는 사유로 상품이 멸실 또는 훼손된 경우 (상품 내용 확인을 위한 포장 훼손 제외)
             - 포장을 개봉하거나 훼손하여 상품 가치가 상실된 경우
             - 고객님의 사용 또는 일부 소비로 인해 상품 가치가 현저히 감소한 경우',

             '※ 다양한 컬러 특성상 각 상품마다 사이즈 스펙에 오차가 있을 수 있습니다.',

             'Delivery 브랜드 업체발송은 상품설명에 기입된 배송 공지 기준으로 출고됩니다.
             Delivery 29CM 자체발송의 경우, 오후 2시까지 결제 확인된 주문은 당일 출고되며,
             5만원 이상 주문은 무료배송, 5만원 미만 주문은 3,000원의 배송비가 추가됩니다.',

             '1962864',

             '매일 편하게 입을 수 있는 티셔츠!',

             31, NULL, 'Y', 'Y',

             '루즈핏 오버핏 여성 반팔 티셔츠 여름 반팔 면 남녀공용 베이직 레터링 데일리 티셔츠',
             'Y', 28500, 0, 0, '', 'Y', 0,
             '티셔츠, white, navy, pink', 19665, 'SELLING', 0, 0, 479
         );
insert
into
    product_img
(img_name, img_url, ord, orig_name, prod_no, repimg_yn)
values
    ('c7052ed8-79dd-4444-97d7-48b9080e6c1a.jpg', 'https://product-uploaded-files.s3.ap-northeast-2.amazonaws.com/de6966a3-8b25-452b-9809-7cd8ac925893.jpg', 1, '화면 캡처 2025-04-25 224537.jpg', 1057, 'Y');
insert
into
    product_img
(img_name, img_url, ord, orig_name, prod_no, repimg_yn)
values
    ('98429085-9b5e-4910-b2de-cd313f5d521a.jpg', 'https://product-uploaded-files.s3.ap-northeast-2.amazonaws.com/7ee9f86f-533e-4e8b-bcc2-1e53f04e3dc8.jpg', 2, '화면 캡처 2025-04-25 224611.jpg', 1057, 'Y');
insert
into
    product_img
(img_name, img_url, ord, orig_name, prod_no, repimg_yn)
values
    ('bd297321-8245-45fa-b599-7ca9cc832269.jpg', 'https://product-uploaded-files.s3.ap-northeast-2.amazonaws.com/37b34388-f2bf-4ca2-ad0c-f3a20ffcea2c.jpg', 3, '화면 캡처 2025-04-25 224639.jpg', 1057, 'Y');
insert
into
    product_img
(img_name, img_url, ord, orig_name, prod_no, repimg_yn)
values
    ('f3727cec-0005-44db-9277-34b2db0f1c1f.jpg', 'https://product-uploaded-files.s3.ap-northeast-2.amazonaws.com/21317b46-05f9-4a01-9665-a5e53ce99e2f.jpg', 1, '화면 캡처 2025-04-25 224705.jpg', 1057, 'N');
insert
into
    product_img
(img_name, img_url, ord, orig_name, prod_no, repimg_yn)
values
    ('48b6edbc-746a-49bd-a0a6-bf64e0fc172d.jpg', 'https://product-uploaded-files.s3.ap-northeast-2.amazonaws.com/3de5cbfb-2c68-434a-ba05-3b053cef06f2.jpg', 2, '화면 캡처 2025-04-25 224731.jpg', 1057, 'N');
insert
into
    option_group
(name, prod_no)
values
    ('사이즈', 1057);
insert
into
    option_value
(color_code, name, optg_no)
values
    (NULL, 'free', 2113);
insert
into
    option_group
(name, prod_no)
values
    ('컬러', 1057);
insert
into
    option_value
(color_code, name, optg_no)
values
    ('#FFFFFF', 'white', 2114);
insert
into
    option_value
(color_code, name, optg_no)
values
    ('#000080', 'navy', 2114);
insert
into
    option_value
(color_code, name, optg_no)
values
    ('#FFC0CB', 'pink', 2114);
insert
into
    item
(add_price, code, max_qty, min_qty, name, prod_no, qty, saf_qty, selling_status, thumbnail_img_url, total_price)
values
    (0, '1962864-1', 40, 1, '사이즈: free, 컬러: white', 1057, 40, 40, 'SELLING', 'https://product-uploaded-files.s3.ap-northeast-2.amazonaws.com/de6966a3-8b25-452b-9809-7cd8ac925893.jpg', 28500);
insert
into
    item_option
(item_no, optv_no)
values
    (2124, 3174);
insert
into
    item_option
(item_no, optv_no)
values
    (2124, 3175);

insert
into
    item
(add_price, code, max_qty, min_qty, name, prod_no, qty, saf_qty, selling_status, thumbnail_img_url, total_price)
values
    (0, '1962864-2', 40, 1, '사이즈: free, 컬러: navy', 1057, 40, 40, 'SELLING', 'https://product-uploaded-files.s3.ap-northeast-2.amazonaws.com/de6966a3-8b25-452b-9809-7cd8ac925893.jpg', 28500);
insert
into
    item_option
(item_no, optv_no)
values
    (2125, 3174);
insert
into
    item_option
(item_no, optv_no)
values
    (2125, 3176);
insert
into
    item
(add_price, code, max_qty, min_qty, name, prod_no, qty, saf_qty, selling_status, thumbnail_img_url, total_price)
values
    (0, '1962864-3', 40, 1, '사이즈: free, 컬러: pink', 1057, 40, 40, 'SELLING', 'https://product-uploaded-files.s3.ap-northeast-2.amazonaws.com/de6966a3-8b25-452b-9809-7cd8ac925893.jpg', 28500);
insert
into
    item_option
(item_no, optv_no)
values
    (2126, 3174);
insert
into
    item_option
(item_no, optv_no)
values
    (2126, 3177);

INSERT INTO product (
    avg_rating, category_no, caution_order, caution_product, caution_shipping, code,
    description, discount_rate, display_order, free_shipping, handmade,
    name, option_available, orig_price, qna_count, qty, rel_prod_code,
    restock_available, review_count, search_keywords, sell_price, selling_status,
    ship_fee, view_count, wish_count
) VALUES (
             0, 18,
             '* 교환 및 반품이 불가능한 경우 *
             - 고객님의 책임 있는 사유로 상품이 멸실 또는 훼손된 경우 (상품 내용 확인을 위한 포장 훼손 제외)
             - 포장을 개봉하거나 훼손하여 상품 가치가 상실된 경우
             - 고객님의 사용 또는 일부 소비로 인해 상품 가치가 현저히 감소한 경우',

             '※ 다양한 컬러 특성상 각 상품마다 사이즈 스펙에 오차가 있을 수 있습니다.',

             'Delivery 브랜드 업체발송은 상품설명에 기입된 배송 공지 기준으로 출고됩니다.
             Delivery 29CM 자체발송의 경우, 오후 2시까지 결제 확인된 주문은 당일 출고되며,
             5만원 이상 주문은 무료배송, 5만원 미만 주문은 3,000원의 배송비가 추가됩니다.',

             '1962865',

             '소장 가치가 높은 캐주얼한 무드의 스프라이트 긴팔티입니다!',

             7, NULL, 'Y', 'Y',

             '[캐주얼/데일리룩!] 오버핏 스트라이프 긴팔 티셔츠 4color_스윗라떼',
             'Y', 16800, 0, 0, '', 'Y', 0,
             '티셔츠, navy, green, red', 15624, 'SELLING', 0, 0, 474
         );
insert
into
    product_img
(img_name, img_url, ord, orig_name, prod_no, repimg_yn)
values
    ('00285999-901c-47c6-b9c8-82ffb7c1ab0f.jpg', 'https://product-uploaded-files.s3.ap-northeast-2.amazonaws.com/37b7cc1a-b702-42b5-b28a-e7893b476f67.jpg', 1, '화면 캡처 2025-04-25 224856.jpg', 1058, 'Y');
insert
into
    product_img
(img_name, img_url, ord, orig_name, prod_no, repimg_yn)
values
    ('084ff02a-648a-47dd-bac4-cdadb6cba6fc.jpg', 'https://product-uploaded-files.s3.ap-northeast-2.amazonaws.com/120b03a5-8a6b-4680-a11b-705b0c92da76.jpg', 2, '화면 캡처 2025-04-25 224927.jpg', 1058, 'Y');
insert
into
    product_img
(img_name, img_url, ord, orig_name, prod_no, repimg_yn)
values
    ('d425e40c-9c8c-4d1d-bea5-0aba9633cc4f.jpg', 'https://product-uploaded-files.s3.ap-northeast-2.amazonaws.com/94192717-35a5-470c-b6eb-eb453086b404.jpg', 3, '화면 캡처 2025-04-25 224951.jpg', 1058, 'Y');
insert
into
    product_img
(img_name, img_url, ord, orig_name, prod_no, repimg_yn)
values
    ('c97651b0-724a-49a4-a851-516251faef58.jpg', 'https://product-uploaded-files.s3.ap-northeast-2.amazonaws.com/935d2e55-b852-4460-8b59-39e8b7c7b55b.jpg', 1, '화면 캡처 2025-04-25 225009.jpg', 1058, 'N');
insert
into
    product_img
(img_name, img_url, ord, orig_name, prod_no, repimg_yn)
values
    ('927c5d3b-98e0-44c6-b9dc-9a8294c06d58.jpg', 'https://product-uploaded-files.s3.ap-northeast-2.amazonaws.com/502929fc-ba03-4a15-bdaf-9a4376fadf18.jpg', 2, '화면 캡처 2025-04-25 225032.jpg', 1058, 'N');
insert
into
    option_group
(name, prod_no)
values
    ('사이즈', 1058);
insert
into
    option_value
(color_code, name, optg_no)
values
    (NULL, 'free', 2115);
insert
into
    option_group
(name, prod_no)
values
    ('컬러', 1058);
insert
into
    option_value
(color_code, name, optg_no)
values
    ('#000080', 'navy', 2116);
insert
into
    option_value
(color_code, name, optg_no)
values
    ('#008000', 'green', 2116);
insert
into
    option_value
(color_code, name, optg_no)
values
    ('#FF0000', 'red', 2116);
insert
into
    item
(add_price, code, max_qty, min_qty, name, prod_no, qty, saf_qty, selling_status, thumbnail_img_url, total_price)
values
    (0, '1962865-1', 40, 1, '사이즈: free, 컬러: navy', 1058, 40, 40, 'SELLING', 'https://product-uploaded-files.s3.ap-northeast-2.amazonaws.com/37b7cc1a-b702-42b5-b28a-e7893b476f67.jpg', 16800);
insert
into
    item_option
(item_no, optv_no)
values
    (2127, 3178);
insert
into
    item_option
(item_no, optv_no)
values
    (2127, 3179);
insert
into
    item
(add_price, code, max_qty, min_qty, name, prod_no, qty, saf_qty, selling_status, thumbnail_img_url, total_price)
values
    (0, '1962865-2', 40, 1, '사이즈: free, 컬러: green', 1058, 40, 40, 'SELLING', 'https://product-uploaded-files.s3.ap-northeast-2.amazonaws.com/37b7cc1a-b702-42b5-b28a-e7893b476f67.jpg', 16800);
insert
into
    item_option
(item_no, optv_no)
values
    (2128, 3178);
insert
into
    item_option
(item_no, optv_no)
values
    (2128, 3180);
insert
into
    item
(add_price, code, max_qty, min_qty, name, prod_no, qty, saf_qty, selling_status, thumbnail_img_url, total_price)
values
    (0, '1962865-3', 40, 1, '사이즈: free, 컬러: red', 1058, 40, 40, 'SELLING', 'https://product-uploaded-files.s3.ap-northeast-2.amazonaws.com/37b7cc1a-b702-42b5-b28a-e7893b476f67.jpg', 16800);
insert
into
    item_option
(item_no, optv_no)
values
    (2129, 3178);
insert
into
    item_option
(item_no, optv_no)
values
    (2129, 3181);

INSERT INTO product (
    avg_rating, category_no, caution_order, caution_product, caution_shipping, code,
    description, discount_rate, display_order, free_shipping, handmade,
    name, option_available, orig_price, qna_count, qty, rel_prod_code,
    restock_available, review_count, search_keywords, sell_price, selling_status,
    ship_fee, view_count, wish_count
) VALUES (
             0, 18,
             '* 교환 및 반품이 불가능한 경우 *
             - 고객님의 책임 있는 사유로 상품이 멸실 또는 훼손된 경우 (상품 내용 확인을 위한 포장 훼손 제외)
             - 포장을 개봉하거나 훼손하여 상품 가치가 상실된 경우
             - 고객님의 사용 또는 일부 소비로 인해 상품 가치가 현저히 감소한 경우',

             '※ 다양한 컬러 특성상 각 상품마다 사이즈 스펙에 오차가 있을 수 있습니다.',

             'Delivery 브랜드 업체발송은 상품설명에 기입된 배송 공지 기준으로 출고됩니다.
             Delivery 29CM 자체발송의 경우, 오후 2시까지 결제 확인된 주문은 당일 출고되며,
             5만원 이상 주문은 무료배송, 5만원 미만 주문은 3,000원의 배송비가 추가됩니다.',

             '1962866',

             '데일리로 입기 좋은 반팔 티쳐스 :)',

             5, NULL, 'N', 'N',

             '꾸안꾸 캐주얼룩 투게더 반팔 티',
             'Y', 16800, 0, 0, '', 'Y', 0,
             '티쳐스, white', 15960, 'SELLING', 2000, 0, 474
         );


insert
into
    product_img
(img_name, img_url, ord, orig_name, prod_no, repimg_yn)
values
    ('8a36f083-94ab-48ae-b7f9-62dbafeaf05c.jpg', 'https://product-uploaded-files.s3.ap-northeast-2.amazonaws.com/154a51ac-d088-423b-86cc-991abfba7813.jpg', 1, '화면 캡처 2025-04-25 232343.jpg', 1059, 'Y');

insert
into
    product_img
(img_name, img_url, ord, orig_name, prod_no, repimg_yn)
values
    ('1774bc48-8117-42e8-b66e-328ee5db8d0a.jpg', 'https://product-uploaded-files.s3.ap-northeast-2.amazonaws.com/f8fe7c6a-3106-44aa-a431-a439a5ca6e84.jpg', 2, '화면 캡처 2025-04-25 232410.jpg', 1059, 'Y');

insert
into
    product_img
(img_name, img_url, ord, orig_name, prod_no, repimg_yn)
values
    ('e224e7d5-54f9-4b72-8050-efc4d4ece135.jpg', 'https://product-uploaded-files.s3.ap-northeast-2.amazonaws.com/f137300e-ea52-420f-8c6e-e79128fd1d01.jpg', 1, '화면 캡처 2025-04-25 232429.jpg', 1059, 'N');

insert
into
    product_img
(img_name, img_url, ord, orig_name, prod_no, repimg_yn)
values
    ('e3902a1b-9679-4e0c-88da-5bea7eff1ac1.jpg', 'https://product-uploaded-files.s3.ap-northeast-2.amazonaws.com/a02057dd-f571-475c-a19b-ce404d11d6bd.jpg', 2, '화면 캡처 2025-04-25 232450.jpg', 1059, 'N');
insert
into
    option_group
(name, prod_no)
values
    ('사이즈', 1059);
insert
into
    option_value
(color_code, name, optg_no)
values
    (NULL, 'M', 2117);
insert
into
    option_value
(color_code, name, optg_no)
values
    (NULL, 'XL', 2117);
insert
into
    option_group
(name, prod_no)
values
    ('컬러', 1059);
insert
into
    option_value
(color_code, name, optg_no)
values
    ('#FFFFFF', 'white', 2118);
insert
into
    item
(add_price, code, max_qty, min_qty, name, prod_no, qty, saf_qty, selling_status, thumbnail_img_url, total_price)
values
    (0, '1962866-1', 40, 1, '사이즈: M, 컬러: white', 1059, 40, 40, 'SELLING', 'https://product-uploaded-files.s3.ap-northeast-2.amazonaws.com/154a51ac-d088-423b-86cc-991abfba7813.jpg', 16800);
insert
into
    item_option
(item_no, optv_no)
values
    (2130, 3182);
insert
into
    item_option
(item_no, optv_no)
values
    (2130, 3184);
insert
into
    item
(add_price, code, max_qty, min_qty, name, prod_no, qty, saf_qty, selling_status, thumbnail_img_url, total_price)
values
    (0, '1962866-2', 40, 1, '사이즈: XL, 컬러: white', 1059, 40, 40, 'SELLING', 'https://product-uploaded-files.s3.ap-northeast-2.amazonaws.com/154a51ac-d088-423b-86cc-991abfba7813.jpg', 16800);
insert
into
    item_option
(item_no, optv_no)
values
    (2131, 3183);
insert
into
    item_option
(item_no, optv_no)
values
    (2131, 3184);

INSERT INTO product (
    avg_rating, category_no, caution_order, caution_product, caution_shipping, code,
    description, discount_rate, display_order, free_shipping, handmade,
    name, option_available, orig_price, qna_count, qty, rel_prod_code,
    restock_available, review_count, search_keywords, sell_price, selling_status,
    ship_fee, view_count, wish_count
) VALUES (
             0, 18,
             '* 교환 및 반품이 불가능한 경우 *
             - 고객님의 책임 있는 사유로 상품이 멸실 또는 훼손된 경우 (상품 내용 확인을 위한 포장 훼손 제외)
             - 포장을 개봉하거나 훼손하여 상품 가치가 상실된 경우
             - 고객님의 사용 또는 일부 소비로 인해 상품 가치가 현저히 감소한 경우',

             '※ 다양한 컬러 특성상 각 상품마다 사이즈 스펙에 오차가 있을 수 있습니다.',

             'Delivery 브랜드 업체발송은 상품설명에 기입된 배송 공지 기준으로 출고됩니다.
             Delivery 29CM 자체발송의 경우, 오후 2시까지 결제 확인된 주문은 당일 출고되며,
             5만원 이상 주문은 무료배송, 5만원 미만 주문은 3,000원의 배송비가 추가됩니다.',

             '1962867',

             '만장돌파! 티셔츠 강추',

             0, NULL, 'Y', 'N',

             '만장돌파!(긴팔ver추가) 릴리즈 사선 포인트 크롭 반팔 긴팔 티셔츠',
             'Y', 18400, 0, 0, '', 'Y', 0,
             '티셔츠, white, black', 18400, 'SELLING', 0, 0, 473
         );

insert
into
    product_img
(img_name, img_url, ord, orig_name, prod_no, repimg_yn)
values
    ('040a39c1-38aa-44aa-984a-a42a4872dc52.jpg', 'https://product-uploaded-files.s3.ap-northeast-2.amazonaws.com/1ace9f8e-5a20-4103-bd92-d270c96a8de0.jpg', 1, '화면 캡처 2025-04-25 233207.jpg', 1060, 'Y');
insert
into
    product_img
(img_name, img_url, ord, orig_name, prod_no, repimg_yn)
values
    ('d8b8a38a-c679-4e60-9ba5-1a124cbd9bc8.jpg', 'https://product-uploaded-files.s3.ap-northeast-2.amazonaws.com/2adcefff-9498-419b-97a5-db24ede34231.jpg', 2, '화면 캡처 2025-04-25 233233.jpg', 1060, 'Y');
insert
into
    product_img
(img_name, img_url, ord, orig_name, prod_no, repimg_yn)
values
    ('b6ac9c28-8590-4937-bf77-b98efc08cd1f.jpg', 'https://product-uploaded-files.s3.ap-northeast-2.amazonaws.com/db93354f-b30a-4812-a2cf-b122a2dfb743.jpg', 1, '화면 캡처 2025-04-25 233257.jpg', 1060, 'N');
insert
into
    option_group
(name, prod_no)
values
    ('사이즈', 1060);
insert
into
    option_value
(color_code, name, optg_no)
values
    (NULL, 'free', 2119);
insert
into
    option_group
(name, prod_no)
values
    ('컬러', 1060);
insert
into
    option_value
(color_code, name, optg_no)
values
    ('#FFFFFF', 'white', 2120);
insert
into
    option_value
(color_code, name, optg_no)
values
    ('#000000', 'black', 2120);
insert
into
    item
(add_price, code, max_qty, min_qty, name, prod_no, qty, saf_qty, selling_status, thumbnail_img_url, total_price)
values
    (0, '1962867-1', 40, 1, '사이즈: free, 컬러: white', 1060, 40, 40, 'SELLING', 'https://product-uploaded-files.s3.ap-northeast-2.amazonaws.com/1ace9f8e-5a20-4103-bd92-d270c96a8de0.jpg', 18400);
insert
into
    item_option
(item_no, optv_no)
values
    (2132, 3185);
insert
into
    item_option
(item_no, optv_no)
values
    (2132, 3186);
insert
into
    item
(add_price, code, max_qty, min_qty, name, prod_no, qty, saf_qty, selling_status, thumbnail_img_url, total_price)
values
    (0, '1962867-2', 40, 1, '사이즈: free, 컬러: black', 1060, 40, 40, 'SELLING', 'https://product-uploaded-files.s3.ap-northeast-2.amazonaws.com/1ace9f8e-5a20-4103-bd92-d270c96a8de0.jpg', 18400);

insert
into
    item_option
(item_no, optv_no)
values
    (2133, 3185);
insert
into
    item_option
(item_no, optv_no)
values
    (2133, 3187);

INSERT INTO product (
    avg_rating, category_no, caution_order, caution_product, caution_shipping, code,
    description, discount_rate, display_order, free_shipping, handmade,
    name, option_available, orig_price, qna_count, qty, rel_prod_code,
    restock_available, review_count, search_keywords, sell_price, selling_status,
    ship_fee, view_count, wish_count
) VALUES (
             0, 18,
             '* 교환 및 반품이 불가능한 경우 *
             - 고객님의 책임 있는 사유로 상품이 멸실 또는 훼손된 경우 (상품 내용 확인을 위한 포장 훼손 제외)
             - 포장을 개봉하거나 훼손하여 상품 가치가 상실된 경우
             - 고객님의 사용 또는 일부 소비로 인해 상품 가치가 현저히 감소한 경우',

             '※ 다양한 컬러 특성상 각 상품마다 사이즈 스펙에 오차가 있을 수 있습니다.',

             'Delivery 브랜드 업체발송은 상품설명에 기입된 배송 공지 기준으로 출고됩니다.
             Delivery 29CM 자체발송의 경우, 오후 2시까지 결제 확인된 주문은 당일 출고되며,
             5만원 이상 주문은 무료배송, 5만원 미만 주문은 3,000원의 배송비가 추가됩니다.',

             '1962868',

             '유니버스 패치 크롭티!',

             20, NULL, 'Y', 'Y',

             '(*패치포인트*) 유니버스 크롭 패치 반팔 티 여름 박시 루즈핏 영문 레터링 티셔츠',
             'Y', 19870, 0, 0, '', 'Y', 0,
             '티셔츠, blue', 15896, 'SELLING', 0, 0, 472
         );

insert
into
    product_img
(img_name, img_url, ord, orig_name, prod_no, repimg_yn)
values
    ('63385ac8-f994-40b5-a5b7-b349e64d8f87.jpg', 'https://product-uploaded-files.s3.ap-northeast-2.amazonaws.com/e23785fb-9916-4a3a-a22e-f365f4971f6a.jpg', 1, '화면 캡처 2025-04-25 233935.jpg', 1061, 'Y');
insert
into
    product_img
(img_name, img_url, ord, orig_name, prod_no, repimg_yn)
values
    ('f6bb8d92-3594-4166-8e61-50013c3d2950.jpg', 'https://product-uploaded-files.s3.ap-northeast-2.amazonaws.com/253cd3eb-3582-40f8-8403-c913a3f01318.jpg', 1, '화면 캡처 2025-04-25 233957.jpg', 1061, 'N');


insert
into
    option_group
(name, prod_no)
values
    ('사이즈', 1061);
insert
into
    option_value
(color_code, name, optg_no)
values
    (NULL, 'free', 2121);
insert
into
    option_group
(name, prod_no)
values
    ('컬러', 1061);
insert
into
    option_value
(color_code, name, optg_no)
values
    ('#0000FF', 'blue', 2122);
insert
into
    item
(add_price, code, max_qty, min_qty, name, prod_no, qty, saf_qty, selling_status, thumbnail_img_url, total_price)
values
    (0, '1962868-1', 40, 1, '사이즈: free, 컬러: blue', 1061, 40, 40, 'SELLING', 'https://product-uploaded-files.s3.ap-northeast-2.amazonaws.com/e23785fb-9916-4a3a-a22e-f365f4971f6a.jpg', 19870);
insert
into
    item_option
(item_no, optv_no)
values
    (2134, 3188);
insert
into
    item_option
(item_no, optv_no)
values
    (2134, 3189);

INSERT INTO product (
    avg_rating, category_no, caution_order, caution_product, caution_shipping, code,
    description, discount_rate, display_order, free_shipping, handmade,
    name, option_available, orig_price, qna_count, qty, rel_prod_code,
    restock_available, review_count, search_keywords, sell_price, selling_status,
    ship_fee, view_count, wish_count
) VALUES (
             0, 18,
             '* 교환 및 반품이 불가능한 경우 *
             - 고객님의 책임 있는 사유로 상품이 멸실 또는 훼손된 경우 (상품 내용 확인을 위한 포장 훼손 제외)
             - 포장을 개봉하거나 훼손하여 상품 가치가 상실된 경우
             - 고객님의 사용 또는 일부 소비로 인해 상품 가치가 현저히 감소한 경우',

             '※ 다양한 컬러 특성상 각 상품마다 사이즈 스펙에 오차가 있을 수 있습니다.',

             'Delivery 브랜드 업체발송은 상품설명에 기입된 배송 공지 기준으로 출고됩니다.
             Delivery 29CM 자체발송의 경우, 오후 2시까지 결제 확인된 주문은 당일 출고되며,
             5만원 이상 주문은 무료배송, 5만원 미만 주문은 3,000원의 배송비가 추가됩니다.',

             '1962868',

             '레깅스랑 찰떡인 티셔츠!',

             20, NULL, 'Y', 'Y',

             '[MADE] (*레깅스랑찰떡*) 반팔 박스 티 루즈핏 빅사이즈 여름 박시 자체제작 티셔츠',
             'Y', 19870, 0, 0, '', 'Y', 0,
             '티셔츠, blue', 15896, 'SELLING', 0, 0, 472
         );

insert
into
    product_img
(img_name, img_url, ord, orig_name, prod_no, repimg_yn)
values
    ('25b98c97-13b7-43e8-ad20-d9cc956cd620.jpg', 'https://product-uploaded-files.s3.ap-northeast-2.amazonaws.com/eaa281e5-47c1-46c8-ae4d-f11c33f5f9d3.jpg', 1, '화면 캡처 2025-04-25 234508.jpg', 1062, 'Y');
insert
into
    product_img
(img_name, img_url, ord, orig_name, prod_no, repimg_yn)
values
    ('208b6486-28eb-4bc2-9531-8e8569e77d39.jpg', 'https://product-uploaded-files.s3.ap-northeast-2.amazonaws.com/a5c777a2-37b8-458d-b893-557cc3ffc242.jpg', 1, '화면 캡처 2025-04-25 234539.jpg', 1062, 'N');

insert
into
    option_group
(name, prod_no)
values
    ('사이즈', 1062);
insert
into
    option_value
(color_code, name, optg_no)
values
    (NULL, 'free', 2123);
insert
into
    option_group
(name, prod_no)
values
    ('컬러', 1062);
insert
into
    option_value
(color_code, name, optg_no)
values
    ('#FFFFFF', 'white', 2124);
insert
into
    item
(add_price, code, max_qty, min_qty, name, prod_no, qty, saf_qty, selling_status, thumbnail_img_url, total_price)
values
    (0, '1962868-1', 40, 1, '사이즈: free, 컬러: white', 1062, 40, 40, 'SELLING', 'https://product-uploaded-files.s3.ap-northeast-2.amazonaws.com/eaa281e5-47c1-46c8-ae4d-f11c33f5f9d3.jpg', 19870);
insert
into
    item_option
(item_no, optv_no)
values
    (2135, 3190);
insert
into
    item_option
(item_no, optv_no)
values
    (2135, 3191);

INSERT INTO product (
    avg_rating, category_no, caution_order, caution_product, caution_shipping, code,
    description, discount_rate, display_order, free_shipping, handmade,
    name, option_available, orig_price, qna_count, qty, rel_prod_code,
    restock_available, review_count, search_keywords, sell_price, selling_status,
    ship_fee, view_count, wish_count
) VALUES (
             0, 18,
             '* 교환 및 반품이 불가능한 경우 *
             - 고객님의 책임 있는 사유로 상품이 멸실 또는 훼손된 경우 (상품 내용 확인을 위한 포장 훼손 제외)
             - 포장을 개봉하거나 훼손하여 상품 가치가 상실된 경우
             - 고객님의 사용 또는 일부 소비로 인해 상품 가치가 현저히 감소한 경우',

             '※ 다양한 컬러 특성상 각 상품마다 사이즈 스펙에 오차가 있을 수 있습니다.',

             'Delivery 브랜드 업체발송은 상품설명에 기입된 배송 공지 기준으로 출고됩니다.
             Delivery 29CM 자체발송의 경우, 오후 2시까지 결제 확인된 주문은 당일 출고되며,
             5만원 이상 주문은 무료배송, 5만원 미만 주문은 3,000원의 배송비가 추가됩니다.',

             '1962869',

             '심플한 스프라이트 티셔츠!',

             26, NULL, 'Y', 'Y',

             '[루즈핏] 스트라이프 긴팔티 5colors 면 줄무늬 티셔츠 mpdglt H',
             'Y', 26900, 0, 0, '', 'Y', 0,
             '티셔츠, white', 19906, 'SELLING', 0, 0, 471
         );

insert
into
    product_img
(img_name, img_url, ord, orig_name, prod_no, repimg_yn)
values
    ('5d9997e6-6051-4efb-95fd-2a8d06040938.jpg', 'https://product-uploaded-files.s3.ap-northeast-2.amazonaws.com/119c0288-d867-44b1-816d-f33fb8a9a0eb.jpg', 1, '화면 캡처 2025-04-25 235559.jpg', 1063, 'Y');
insert
into
    product_img
(img_name, img_url, ord, orig_name, prod_no, repimg_yn)
values
    ('a9843630-5e89-44c8-9d73-6b37caf8962f.jpg', 'https://product-uploaded-files.s3.ap-northeast-2.amazonaws.com/a50cb084-56fb-4b93-8720-df37bb6acec5.jpg', 2, '화면 캡처 2025-04-25 235621.jpg', 1063, 'Y');
insert
into
    product_img
(img_name, img_url, ord, orig_name, prod_no, repimg_yn)
values
    ('453cf2c1-b6e5-4146-83c6-e2e5a9a2891c.jpg', 'https://product-uploaded-files.s3.ap-northeast-2.amazonaws.com/64f39102-2bcf-4d73-9242-f21005f2f3ce.jpg', 1, '화면 캡처 2025-04-25 235649.jpg', 1063, 'N');
insert
into
    option_group
(name, prod_no)
values
    ('사이즈', 1063);
insert
into
    option_value
(color_code, name, optg_no)
values
    (NULL, 'free', 2125);
insert
into
    option_group
(name, prod_no)
values
    ('컬러', 1063);
insert
into
    option_value
(color_code, name, optg_no)
values
    ('#FFFFFF', 'white', 2126);
insert
into
    item
(add_price, code, max_qty, min_qty, name, prod_no, qty, saf_qty, selling_status, thumbnail_img_url, total_price)
values
    (0, '1962869-1', 40, 1, '사이즈: free, 컬러: white', 1063, 40, 40, 'SELLING', 'https://product-uploaded-files.s3.ap-northeast-2.amazonaws.com/119c0288-d867-44b1-816d-f33fb8a9a0eb.jpg', 26900);
insert
into
    item_option
(item_no, optv_no)
values
    (2136, 3192);
insert
into
    item_option
(item_no, optv_no)
values
    (2136, 3193);

INSERT INTO product (
    avg_rating, category_no, caution_order, caution_product, caution_shipping, code,
    description, discount_rate, display_order, free_shipping, handmade,
    name, option_available, orig_price, qna_count, qty, rel_prod_code,
    restock_available, review_count, search_keywords, sell_price, selling_status,
    ship_fee, view_count, wish_count
) VALUES (
             0, 18,
             '* 교환 및 반품이 불가능한 경우 *
             - 고객님의 책임 있는 사유로 상품이 멸실 또는 훼손된 경우 (상품 내용 확인을 위한 포장 훼손 제외)
             - 포장을 개봉하거나 훼손하여 상품 가치가 상실된 경우
             - 고객님의 사용 또는 일부 소비로 인해 상품 가치가 현저히 감소한 경우',

             '※ 다양한 컬러 특성상 각 상품마다 사이즈 스펙에 오차가 있을 수 있습니다.',

             'Delivery 브랜드 업체발송은 상품설명에 기입된 배송 공지 기준으로 출고됩니다.
             Delivery 29CM 자체발송의 경우, 오후 2시까지 결제 확인된 주문은 당일 출고되며,
             5만원 이상 주문은 무료배송, 5만원 미만 주문은 3,000원의 배송비가 추가됩니다.',

             '1962870',

             '심플한 하트로 귀여운 티~!',

             10, NULL, 'Y', 'Y',

             '심플 하트 프린팅 반팔 티',
             'Y', 16500, 0, 0, '', 'Y', 0,
             '티셔츠, black', 14850, 'SELLING', 0, 0, 475
         );

insert
into
    product_img
(img_name, img_url, ord, orig_name, prod_no, repimg_yn)
values
    ('7f0a0df5-a8b1-42a6-aaf4-1a9ea1600b39.jpg', 'https://product-uploaded-files.s3.ap-northeast-2.amazonaws.com/15e3c385-3a0a-42e8-999f-91af0b406dc8.jpg', 1, '화면 캡처 2025-04-25 235722.jpg', 1064, 'Y');
insert
into
    product_img
(img_name, img_url, ord, orig_name, prod_no, repimg_yn)
values
    ('59253776-f0a7-4d5c-b8c5-0ffc1d2913d5.jpg', 'https://product-uploaded-files.s3.ap-northeast-2.amazonaws.com/e4d66b5a-116b-4fb4-8e68-22e3b0247e96.jpg', 1, '화면 캡처 2025-04-25 235739.jpg', 1064, 'N');
insert
into
    option_group
(name, prod_no)
values
    ('사이즈', 1064);
insert
into
    option_value
(color_code, name, optg_no)
values
    (NULL, 'free', 2127);
insert
into
    option_group
(name, prod_no)
values
    ('컬러', 1064);
insert
into
    option_value
(color_code, name, optg_no)
values
    ('#000000', 'black', 2128);
insert
into
    item
(add_price, code, max_qty, min_qty, name, prod_no, qty, saf_qty, selling_status, thumbnail_img_url, total_price)
values
    (0, '1962870-1', 40, 1, '사이즈: free, 컬러: black', 1064, 40, 40, 'SELLING', 'https://product-uploaded-files.s3.ap-northeast-2.amazonaws.com/15e3c385-3a0a-42e8-999f-91af0b406dc8.jpg', 16500);
insert
into
    item_option
(item_no, optv_no)
values
    (2137, 3194);
insert
into
    item_option
(item_no, optv_no)
values
    (2137, 3195);

INSERT INTO product (
    avg_rating, category_no, caution_order, caution_product, caution_shipping, code,
    description, discount_rate, display_order, free_shipping, handmade,
    name, option_available, orig_price, qna_count, qty, rel_prod_code,
    restock_available, review_count, search_keywords, sell_price, selling_status,
    ship_fee, view_count, wish_count
) VALUES (
             0, 18,
             '* 교환 및 반품이 불가능한 경우 *
             - 고객님의 책임 있는 사유로 상품이 멸실 또는 훼손된 경우 (상품 내용 확인을 위한 포장 훼손 제외)
             - 포장을 개봉하거나 훼손하여 상품 가치가 상실된 경우
             - 고객님의 사용 또는 일부 소비로 인해 상품 가치가 현저히 감소한 경우',

             '※ 다양한 컬러 특성상 각 상품마다 사이즈 스펙에 오차가 있을 수 있습니다.',

             'Delivery 브랜드 업체발송은 상품설명에 기입된 배송 공지 기준으로 출고됩니다.
             Delivery 29CM 자체발송의 경우, 오후 2시까지 결제 확인된 주문은 당일 출고되며,
             5만원 이상 주문은 무료배송, 5만원 미만 주문은 3,000원의 배송비가 추가됩니다.',

             '19628571',

             '귀여운 오버핏 티셔츠~',

             6, NULL, 'Y', 'Y',

             '[꾸안꾸/포인트룩!] 남녀공용 오버핏 펜던트 단가라 카라 반팔티 3color_스윗라떼',
             'Y', 26800, 0, 0, '', 'Y', 0,
             '티셔츠, red, pink', 25192, 'SELLING', 0, 0, 470
         );

insert
into
    product_img
(img_name, img_url, ord, orig_name, prod_no, repimg_yn)
values
    ('a5079f27-f689-4a65-9d7f-d30adae594eb.jpg', 'https://product-uploaded-files.s3.ap-northeast-2.amazonaws.com/ee05ebdd-d811-4916-a413-b293cafa3999.jpg', 1, '화면 캡처 2025-04-25 235817.jpg', 1065, 'Y');
insert
into
    product_img
(img_name, img_url, ord, orig_name, prod_no, repimg_yn)
values
    ('9aad697f-4214-4913-9756-0cbcd8b1d9e5.jpg', 'https://product-uploaded-files.s3.ap-northeast-2.amazonaws.com/84b7e110-e75c-4432-9b45-863c1ccdb402.jpg', 2, '화면 캡처 2025-04-25 235840.jpg', 1065, 'Y');
insert
into
    product_img
(img_name, img_url, ord, orig_name, prod_no, repimg_yn)
values
    ('cfd5b6b6-4023-4b4b-a8f9-0649919628f9.jpg', 'https://product-uploaded-files.s3.ap-northeast-2.amazonaws.com/0345bdcf-add5-47a2-97f2-1a428359bd77.jpg', 1, '화면 캡처 2025-04-25 235910.jpg', 1065, 'N');
insert
into
    option_group
(name, prod_no)
values
    ('사이즈', 1065);
insert
into
    option_value
(color_code, name, optg_no)
values
    (NULL, 'free', 2129);
insert
into
    option_group
(name, prod_no)
values
    ('컬러', 1065);
insert
into
    option_value
(color_code, name, optg_no)
values
    ('#FF0000', 'red', 2130);
insert
into
    option_value
(color_code, name, optg_no)
values
    ('#FFC0CB', 'pink', 2130);
insert
into
    item
(add_price, code, max_qty, min_qty, name, prod_no, qty, saf_qty, selling_status, thumbnail_img_url, total_price)
values
    (0, '19628571-1', 40, 1, '사이즈: free, 컬러: red', 1065, 40, 40, 'SELLING', 'https://product-uploaded-files.s3.ap-northeast-2.amazonaws.com/ee05ebdd-d811-4916-a413-b293cafa3999.jpg', 26800);
insert
into
    item_option
(item_no, optv_no)
values
    (2138, 3196);
insert
into
    item_option
(item_no, optv_no)
values
    (2138, 3197);
insert
into
    item
(add_price, code, max_qty, min_qty, name, prod_no, qty, saf_qty, selling_status, thumbnail_img_url, total_price)
values
    (0, '19628571-2', 40, 1, '사이즈: free, 컬러: pink', 1065, 40, 40, 'SELLING', 'https://product-uploaded-files.s3.ap-northeast-2.amazonaws.com/ee05ebdd-d811-4916-a413-b293cafa3999.jpg', 26800);
insert
into
    item_option
(item_no, optv_no)
values
    (2139, 3196);
insert
into
    item_option
(item_no, optv_no)
values
    (2139, 3198);


INSERT INTO product (
    avg_rating, category_no, caution_order, caution_product, caution_shipping, code,
    description, discount_rate, display_order, free_shipping, handmade,
    name, option_available, orig_price, qna_count, qty, rel_prod_code,
    restock_available, review_count, search_keywords, sell_price, selling_status,
    ship_fee, view_count, wish_count
) VALUES (
             0, 18,
             '* 교환 및 반품이 불가능한 경우 *
             - 고객님의 책임 있는 사유로 상품이 멸실 또는 훼손된 경우 (상품 내용 확인을 위한 포장 훼손 제외)
             - 포장을 개봉하거나 훼손하여 상품 가치가 상실된 경우
             - 고객님의 사용 또는 일부 소비로 인해 상품 가치가 현저히 감소한 경우',

             '※ 다양한 컬러 특성상 각 상품마다 사이즈 스펙에 오차가 있을 수 있습니다.',

             'Delivery 브랜드 업체발송은 상품설명에 기입된 배송 공지 기준으로 출고됩니다.
             Delivery 29CM 자체발송의 경우, 오후 2시까지 결제 확인된 주문은 당일 출고되며,
             5만원 이상 주문은 무료배송, 5만원 미만 주문은 3,000원의 배송비가 추가됩니다.',

             '1962874',

             'Blue/Red Handmade 티셔츠 : )',

             29, NULL, 'Y', 'Y',

             '루즈핏 여름 여성 반팔티 레터링 예쁜핏 데일리 밑스트링 하프 반소매 티셔츠',
             'Y', 30500, 0, 0, '', 'Y', 0,
             '티셔츠, red, blue', 21655, 'SELLING', 0, 0, 520
         );


insert
into
    product_img
(img_name, img_url, ord, orig_name, prod_no, repimg_yn)
values
    ('84d5e432-5bf2-45f6-87cd-4819e5d11291.jpg', 'https://product-uploaded-files.s3.ap-northeast-2.amazonaws.com/db955c17-2a4c-4e59-bc4e-96dcf9802182.jpg', 1, '화면 캡처 2025-04-26 002131.jpg', 1066, 'Y');
insert
into
    product_img
(img_name, img_url, ord, orig_name, prod_no, repimg_yn)
values
    ('5d340689-ea66-4421-8c20-afebf4e46007.jpg', 'https://product-uploaded-files.s3.ap-northeast-2.amazonaws.com/450187ba-cb16-4c9e-ab8a-6e9f486b1ed4.jpg', 2, '화면 캡처 2025-04-26 002146.jpg', 1066, 'Y');
insert
into
    product_img
(img_name, img_url, ord, orig_name, prod_no, repimg_yn)
values
    ('82cd560b-330e-4120-99ee-c69a51c70ca7.jpg', 'https://product-uploaded-files.s3.ap-northeast-2.amazonaws.com/788d4bb1-8921-498b-923e-7c717ead90fc.jpg', 3, '화면 캡처 2025-04-26 002159.jpg', 1066, 'Y');
insert
into
    product_img
(img_name, img_url, ord, orig_name, prod_no, repimg_yn)
values
    ('84a0501e-2a73-445d-8676-e1d5a9f29315.jpg', 'https://product-uploaded-files.s3.ap-northeast-2.amazonaws.com/d2345b57-51b8-4576-9493-c79cacbbfedd.jpg', 4, '화면 캡처 2025-04-26 002212.jpg', 1066, 'Y');
insert
into
    product_img
(img_name, img_url, ord, orig_name, prod_no, repimg_yn)
values
    ('a17316f3-ea1c-47b3-9291-ce3959d8eb3f.jpg', 'https://product-uploaded-files.s3.ap-northeast-2.amazonaws.com/ba47bcad-e2b9-4a05-a21f-7e914ef66621.jpg', 1, '화면 캡처 2025-04-26 002229.jpg', 1066, 'N');
insert
into
    product_img
(img_name, img_url, ord, orig_name, prod_no, repimg_yn)
values
    ('6c84bd7f-43a7-4208-b6c4-532d885f0649.jpg', 'https://product-uploaded-files.s3.ap-northeast-2.amazonaws.com/13605629-8fd8-4850-85ca-fe02c77553dc.jpg', 2, '화면 캡처 2025-04-26 002252.jpg', 1066, 'N');
insert
into
    option_group
(name, prod_no)
values
    ('사이즈', 1066);
insert
into
    option_value
(color_code, name, optg_no)
values
    (NULL, 'M', 2131);
insert
into
    option_value
(color_code, name, optg_no)
values
    (NULL, 'L', 2131);


insert
into
    option_value
(color_code, name, optg_no)
values
    (NULL, 'XL', 2131);
insert
into
    option_group
(name, prod_no)
values
    ('컬러', 1066);
insert
into
    option_value
(color_code, name, optg_no)
values
    ('#FF0000', 'red', 2132);
insert
into
    option_value
(color_code, name, optg_no)
values
    ('#0000FF', 'blue', 2132);
insert
into
    item
(add_price, code, max_qty, min_qty, name, prod_no, qty, saf_qty, selling_status, thumbnail_img_url, total_price)
values
    (0, '1962874-1', 40, 1, '사이즈: M, 컬러: red', 1066, 40, 40, 'SELLING', 'https://product-uploaded-files.s3.ap-northeast-2.amazonaws.com/db955c17-2a4c-4e59-bc4e-96dcf9802182.jpg', 30500);
insert
into
    item_option
(item_no, optv_no)
values
    (2140, 3199);
insert
into
    item_option
(item_no, optv_no)
values
    (2140, 3202);
insert
into
    item
(add_price, code, max_qty, min_qty, name, prod_no, qty, saf_qty, selling_status, thumbnail_img_url, total_price)
values
    (0, '1962874-2', 40, 1, '사이즈: M, 컬러: blue', 1066, 40, 40, 'SELLING', 'https://product-uploaded-files.s3.ap-northeast-2.amazonaws.com/db955c17-2a4c-4e59-bc4e-96dcf9802182.jpg', 30500);
insert
into
    item_option
(item_no, optv_no)
values
    (2141, 3199);
insert
into
    item_option
(item_no, optv_no)
values
    (2141, 3203);
insert
into
    item
(add_price, code, max_qty, min_qty, name, prod_no, qty, saf_qty, selling_status, thumbnail_img_url, total_price)
values
    (0, '1962874-3', 40, 1, '사이즈: L, 컬러: red', 1066, 40, 40, 'SELLING', 'https://product-uploaded-files.s3.ap-northeast-2.amazonaws.com/db955c17-2a4c-4e59-bc4e-96dcf9802182.jpg', 30500);
insert
into
    item_option
(item_no, optv_no)
values
    (2142, 3200);
insert
into
    item_option
(item_no, optv_no)
values
    (2142, 3202);
insert
into
    item
(add_price, code, max_qty, min_qty, name, prod_no, qty, saf_qty, selling_status, thumbnail_img_url, total_price)
values
    (0, '1962874-4', 40, 1, '사이즈: L, 컬러: blue', 1066, 40, 40, 'SELLING', 'https://product-uploaded-files.s3.ap-northeast-2.amazonaws.com/db955c17-2a4c-4e59-bc4e-96dcf9802182.jpg', 30500);
insert
into
    item_option
(item_no, optv_no)
values
    (2143, 3200);
insert
into
    item_option
(item_no, optv_no)
values
    (2143, 3203);
insert
into
    item
(add_price, code, max_qty, min_qty, name, prod_no, qty, saf_qty, selling_status, thumbnail_img_url, total_price)
values
    (1000, '1962874-5', 40, 1, '사이즈: XL, 컬러: red', 1066, 40, 40, 'SELLING', 'https://product-uploaded-files.s3.ap-northeast-2.amazonaws.com/db955c17-2a4c-4e59-bc4e-96dcf9802182.jpg', 31500);
insert
into
    item_option
(item_no, optv_no)
values
    (2144, 3201);
insert
into
    item_option
(item_no, optv_no)
values
    (2144, 3202);
insert
into
    item
(add_price, code, max_qty, min_qty, name, prod_no, qty, saf_qty, selling_status, thumbnail_img_url, total_price)
values
    (1000, '1962874-6', 40, 1, '사이즈: XL, 컬러: blue', 1066, 40, 40, 'SELLING', 'https://product-uploaded-files.s3.ap-northeast-2.amazonaws.com/db955c17-2a4c-4e59-bc4e-96dcf9802182.jpg', 31500);
insert
into
    item_option
(item_no, optv_no)
values
    (2145, 3201);
insert
into
    item_option
(item_no, optv_no)
values
    (2145, 3203);

INSERT INTO view_rolling (category_no, prod_no, period, total_views, last_updated)
VALUES (18, 1066, 'REALTIME', 20000, NOW());

INSERT INTO view_rolling (category_no, prod_no, period, total_views, last_updated)
VALUES (18, 1065, 'REALTIME', 19999, NOW());

INSERT INTO view_rolling (category_no, prod_no, period, total_views, last_updated)
VALUES (18, 1064, 'REALTIME', 19998, NOW());

INSERT INTO view_rolling (category_no, prod_no, period, total_views, last_updated)
VALUES (18, 1063, 'REALTIME', 19997, NOW());

INSERT INTO view_rolling (category_no, prod_no, period, total_views, last_updated)
VALUES (18, 1062, 'REALTIME', 19996, NOW());


-- [ 회원가입 약관 삽입 ]
-- 상위 약관
-- 1
INSERT  INTO terms (parents_no, category, name, contents, terms_yn, version, level, active_yn)
VALUES (null, 'JOIN', '회원가입 약관', 'ANKIM 이용약관', 'Y', 1, 1, 'Y');

-- 필수 약관
-- 2
INSERT  INTO terms (parents_no, category, name, contents, terms_yn, version, level, active_yn)
VALUES (1, 'JOIN', '만 14세 이상입니다', '이 약관은 만 14세 이상임을 동의하는 내용입니다.', 'Y', 1, 2, 'Y');
-- 3
INSERT  INTO terms (parents_no, category, name, contents, terms_yn, version, level, active_yn)
VALUES (1, 'JOIN', '이용약관 동의', '이 약관은 서비스 이용에 대한 동의를 포함합니다.', 'Y', 1, 2, 'Y');
--
-- 선택 약관
-- 4
INSERT  INTO terms (parents_no, category, name, contents, terms_yn, version, level, active_yn)
VALUES (1, 'JOIN', '마케팅 목적의 개인정보 수집 및 이용 동의', '마케팅 목적의 개인정보 수집 및 이용 동의', 'N', 1, 2, 'Y');

-- 5
INSERT  INTO terms (parents_no, category, name, contents, terms_yn, version, level, active_yn)
VALUES (1, 'JOIN', '광고성 정보 수신 동의', '광고성 정보를 수신하는 것에 대한 동의입니다.', 'N', 1, 2, 'Y');

-- 6
INSERT  INTO terms (parents_no, category, name, contents, terms_yn, version, level, active_yn)
VALUES (5, 'JOIN', '문자 수신 동의', '광고성 정보를 수신하는 것에 대한 동의입니다.', 'N', 1, 3, 'Y');

-- 7
INSERT  INTO terms (parents_no, category, name, contents, terms_yn, version, level, active_yn)
VALUES (5, 'JOIN', '이메일 수신 동의', '광고성 정보를 수신하는 것에 대한 동의입니다.', 'N', 1, 3, 'Y');

-- [ 주문/결제 약관 삽입 ]
-- 8
INSERT  INTO terms (parents_no, category, name, contents, terms_yn, version, level, active_yn)
VALUES (null, 'ORDER', '주문 약관', 'ANKIM 주문/결제 약관', 'Y', 1, 1, 'Y');

-- [ 탈퇴 약관 삽입 ]
-- 9
INSERT  INTO terms (parents_no, category, name, contents, terms_yn, version, level, active_yn)
VALUES (null, 'LEAVE', '탈퇴 약관', 'ANKIM 탈퇴 약관', 'Y', 1, 1, 'Y');

-- 10
INSERT  INTO terms (parents_no, category, name, contents, terms_yn, version, level, active_yn)
VALUES (9, 'LEAVE', '탈퇴 시 삭제되는 내용','- 탈퇴 시 고객님께서 보유하셨던 쿠폰과 마일리지는 모두 소멸되며 환불할 수 없습니다. 또한 다른 계정으로 양도 또는 이관할 수 없습니다.
- 탈퇴한 계정 및 이용 내역은 복구할 수 없으니 탈퇴 시 유의하시기 바랍니다.', 'Y', 1, 2, 'Y');

-- 11
INSERT  INTO terms (parents_no, category, name, contents, terms_yn, version, level, active_yn)
VALUES (9, 'LEAVE', '탈퇴 시 보관 또는 유지되는 항목',
        '- 탈퇴 시 법령에 따라 보관해야 하는 항목은 관련 법령에 따라 일정 기간 보관하며 다른 목적으로 이용하지 않습니다. 전자상거래 등에서의 소비자보호에 관한 법률에 의거하여 대금결제 및 재화 등의 공급에 관한 기록 5년, 계약 또는 청약철회 등에 관한 기록 5년, 소비자의 불만 또는 분쟁처리에 관한 기록은 3년동안 보관됩니다.
- 아이디(이메일), 이메일, 비밀번호는 부정 이용ㆍ탈퇴 방지를 위해 탈퇴 요청 시 7일 간 별도 보관 후 파기합니다.
- 탈퇴 후에도 서비스에 등록한 게시물 및 댓글은 그대로 남아 있습니다. 상품 리뷰, 게시글, 이벤트 댓글 등은 삭제되지 않습니다. 탈퇴 후에는 회원정보가 삭제되어 본인 여부를 확인할 수 없으므로 게시글을 임의로 삭제해드릴 수 없습니다. 먼저 해당 게시물을 삭제하신 후 탈퇴를 신청하시기 바랍니다.', 'Y', 1, 2, 'Y');




-- [ 회원 삽입 ]
INSERT  INTO member (
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
INSERT  INTO mem_addr (MEM_NO, ZIP_CODE, ADDR_MAIN, ADDR_DTL, ADDR_NAME, PHONE_NUM, ADDR_DEF, ACTIVE_YN)
VALUES (1, 12345, '서울특별시 강남구', '10층 D강의실', '기본 배송지', '010-1234-5678', 'Y', 'Y');

INSERT  INTO mem_addr (MEM_NO, ZIP_CODE, ADDR_MAIN, ADDR_DTL, RECEIVER, PHONE_NUM, PHONE_EMGCY, ADDR_DEF, ACTIVE_YN)
VALUES (1, 98765, '제주특별시 서귀포구', 'oo아파트 101호', '안정훈', '010-1234-5678', '010-8282-8282', 'N', 'Y');

INSERT  INTO mem_addr (MEM_NO, ZIP_CODE, ADDR_MAIN, ADDR_DTL, RECEIVER, PHONE_NUM, PHONE_EMGCY, ADDR_DEF, ACTIVE_YN)
VALUES (1, 02587, '경기도 부천시', 'oo빌라', '안정훈', '010-1234-5678', '010-2424-5252', 'N', 'Y');

-- 탈퇴사유
INSERT  INTO leave_rsn (reason, active_yn) VALUES
                                              ('탈퇴 후 재가입을 위해서', 'Y'),
                                              ('사고 싶은 상품이 없어서', 'Y'),
                                              ('자주 이용하지 않아서', 'Y'),
                                              ('서비스 및 고객지원이 만족스럽지 않아서', 'Y'),
                                              ('광고성 알림이 너무 많이 와서', 'Y'),
                                              ('기타', 'Y');
