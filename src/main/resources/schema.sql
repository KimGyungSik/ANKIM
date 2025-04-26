-- 인덱스가 존재하면 삭제
DROP INDEX idx_ft_product ON product;

-- 그리고 새로 생성
ALTER TABLE product ADD FULLTEXT INDEX idx_ft_product (name, search_keywords, description)
  WITH PARSER ngram;


CREATE TABLE IF NOT EXISTS shedlock (
                                        name VARCHAR(64) NOT NULL PRIMARY KEY,
                                        lock_until TIMESTAMP NOT NULL,
                                        locked_at TIMESTAMP NOT NULL,
                                        locked_by VARCHAR(255) NOT NULL
    );
