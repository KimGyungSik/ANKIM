ALTER TABLE product ADD FULLTEXT INDEX idx_ft_product (name, search_keywords, description);
