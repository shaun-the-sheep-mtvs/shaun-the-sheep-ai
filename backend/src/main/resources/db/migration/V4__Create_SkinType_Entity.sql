-- Create skin_types table with byte IDs
CREATE TABLE skin_types (
    id SMALLINT PRIMARY KEY,
    english_name VARCHAR(50) UNIQUE NOT NULL,
    korean_name VARCHAR(50) UNIQUE NOT NULL,
    description TEXT NOT NULL
);

-- Insert the 6 skin types
INSERT INTO skin_types (id, english_name, korean_name, description) VALUES
(1, 'sensitive', '민감성', '외부 자극에 민감하게 반응하는 피부'),
(2, 'dry', '건성', '수분과 유분이 부족한 건조한 피부'),
(3, 'oily', '지성', '과다한 유분 분비로 번들거리는 피부'),
(4, 'combination', '복합성', 'T존은 지성, U존은 건성인 복합 피부'),
(5, 'dehydrated', '수분부족지성', '수분 부족으로 겉은 번들, 속은 건조한 피부'),
(6, 'default', '표준형', '균형잡힌 건강한 피부');

-- Update skin_mbtis table structure
ALTER TABLE skin_mbtis 
DROP COLUMN korean_name,
ADD COLUMN skin_type_id SMALLINT NOT NULL DEFAULT 6;

-- Update MBTI data with correct skin type IDs
UPDATE skin_mbtis SET skin_type_id = 2 WHERE mbti_code IN ('DBIL', 'DBIT', 'DBSL', 'DBST'); -- 건성
UPDATE skin_mbtis SET skin_type_id = 5 WHERE mbti_code IN ('DOIL', 'DOIT', 'DOSL', 'DOST'); -- 수분부족지성
UPDATE skin_mbtis SET skin_type_id = 4 WHERE mbti_code IN ('MBIL', 'MBIT'); -- 복합성
UPDATE skin_mbtis SET skin_type_id = 1 WHERE mbti_code IN ('MBSL', 'MBST', 'MOSL', 'MOST'); -- 민감성
UPDATE skin_mbtis SET skin_type_id = 3 WHERE mbti_code IN ('MOIL', 'MOIT'); -- 지성

-- Add foreign key constraint
ALTER TABLE skin_mbtis 
ADD CONSTRAINT fk_skin_mbtis_skin_type 
FOREIGN KEY (skin_type_id) REFERENCES skin_types(id);

-- Update products table structure - change recommended_type from string to byte
ALTER TABLE products 
ALTER COLUMN recommended_type TYPE SMALLINT USING CASE 
    WHEN recommended_type = '건성' THEN 2
    WHEN recommended_type = '지성' THEN 3  
    WHEN recommended_type = '복합성' THEN 4
    WHEN recommended_type = '민감성' THEN 1
    WHEN recommended_type = '수분부족지성' THEN 5
    ELSE 6
END;

-- Add foreign key constraint for products
ALTER TABLE products 
ADD CONSTRAINT fk_products_skin_type 
FOREIGN KEY (recommended_type) REFERENCES skin_types(id);

-- Create indexes for performance
CREATE INDEX idx_skin_mbtis_skin_type_id ON skin_mbtis(skin_type_id);
CREATE INDEX idx_products_recommended_type ON products(recommended_type);