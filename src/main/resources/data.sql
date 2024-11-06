-- 중분류 카테고리 삽입 (no 필드 생략)
INSERT INTO category (name, level, parent_no) VALUES ('아우터', 'MIDDLE', NULL);
INSERT INTO category (name, level, parent_no) VALUES ('상의', 'MIDDLE', NULL);

-- 소분류 카테고리 삽입 (중분류 '아우터'에 속한 소분류들)
INSERT INTO category (name, level, parent_no) VALUES ('코트', 'SUB', 1);
INSERT INTO category (name, level, parent_no) VALUES ('자켓', 'SUB', 1);
INSERT INTO category (name, level, parent_no) VALUES ('가디건', 'SUB', 1);

-- 소분류 카테고리 삽입 (중분류 '상의'에 속한 소분류들)
INSERT INTO category (name, level, parent_no) VALUES ('티셔츠', 'SUB', 2);
INSERT INTO category (name, level, parent_no) VALUES ('블라우스', 'SUB', 2);
INSERT INTO category (name, level, parent_no) VALUES ('니트', 'SUB', 2);
