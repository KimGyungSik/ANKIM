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
VALUES (1, 'JOIN', '마케팅 목적의 개인정보 수집 및 이용 동의', '마케팅 목적으로 개인정보를 수집 및 이용하는 것에 대한 동의입니다.', 'N', 'v1', 2, 'Y');

INSERT INTO terms (parents_no, category, name, contents, terms_yn, version, level, active_yn)
VALUES (1, 'JOIN', '광고성 정보 수신 동의', '광고성 정보를 수신하는 것에 대한 동의입니다.', 'N', 'v1', 2, 'Y');

INSERT INTO terms (parents_no, category, name, contents, terms_yn, version, level, active_yn)
VALUES (5, 'JOIN', '문자 수신 동의', '광고성 정보를 수신하는 것에 대한 동의입니다.', 'N', 'v1', 3, 'Y');

INSERT INTO terms (parents_no, category, name, contents, terms_yn, version, level, active_yn)
VALUES (5, 'JOIN', '이메일 수신 동의', '광고성 정보를 수신하는 것에 대한 동의입니다.', 'N', 'v1', 3, 'Y');

-- [ 주문/결제 약관 삽입 ]
-- 상위 약관
INSERT INTO terms (parents_no, category, name, contents, terms_yn, version, level, active_yn)
VALUES (null, 'ORDER', '주문 약관', 'ANKIM 주문/결제 약관', 'Y', 'v1', 1, 'Y');