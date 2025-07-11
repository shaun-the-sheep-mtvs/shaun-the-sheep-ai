-- Create formulations table with byte IDs
CREATE TABLE formulations (
    id SMALLINT PRIMARY KEY,
    english_name VARCHAR(50) UNIQUE NOT NULL,
    korean_name VARCHAR(50) UNIQUE NOT NULL,
    description TEXT
);

-- Insert the formulation types from Kinds enum
INSERT INTO formulations (id, english_name, korean_name, description) VALUES
(1, 'toner', '토너', '피부 결을 정리하고 수분을 공급하는 기초 스킨케어 제품'),
(2, 'ampoule', '앰플', '고농축 유효성분을 함유한 집중 케어 제품'),
(3, 'cream', '크림', '영양과 수분을 공급하는 진한 질감의 보습 제품'),
(4, 'serum', '세럼', '피부 깊숙이 침투하는 고농축 에센스'),
(5, 'lotion', '로션', '가벼운 질감의 보습 제품'),
(6, 'mask', '팩', '집중 케어를 위한 마스크 타입 제품'),
(7, 'pad', '패드', '토너나 에센스가 함유된 패드형 제품'),
(8, 'skin', '스킨', '가장 기본적인 기초 화장수');

-- Update products table to use formulation_id instead of formulation string
ALTER TABLE products 
ADD COLUMN formulation_id SMALLINT;

-- Convert existing formulation string values to formulation_id
UPDATE products SET formulation_id = CASE 
    -- Korean names
    WHEN formulation = '토너' THEN 1
    WHEN formulation = '앰플' THEN 2
    WHEN formulation = '크림' THEN 3
    WHEN formulation = '세럼' THEN 4
    WHEN formulation = '로션' THEN 5
    WHEN formulation = '팩' THEN 6
    WHEN formulation = '패드' THEN 7
    WHEN formulation = '스킨' THEN 8
    -- English names (for backward compatibility)
    WHEN formulation = 'toner' THEN 1
    WHEN formulation = 'ampoule' THEN 2
    WHEN formulation = 'cream' THEN 3
    WHEN formulation = 'serum' THEN 4
    WHEN formulation = 'lotion' THEN 5
    WHEN formulation = 'mask' THEN 6
    WHEN formulation = 'pad' THEN 7
    WHEN formulation = 'skin' THEN 8
    ELSE NULL
END;

-- Drop the old formulation column
ALTER TABLE products DROP COLUMN formulation;

-- Add foreign key constraint
ALTER TABLE products 
ADD CONSTRAINT fk_products_formulation 
FOREIGN KEY (formulation_id) REFERENCES formulations(id);

-- Create index for performance
CREATE INDEX idx_products_formulation_id ON products(formulation_id);