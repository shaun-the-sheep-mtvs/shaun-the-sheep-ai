-- Add concern ID columns to products table
ALTER TABLE products 
ADD COLUMN concern_id_1 SMALLINT,
ADD COLUMN concern_id_2 SMALLINT,
ADD COLUMN concern_id_3 SMALLINT;

-- Add comments for documentation
COMMENT ON COLUMN products.concern_id_1 IS 'Primary concern ID (1-13) from skin_concerns table';
COMMENT ON COLUMN products.concern_id_2 IS 'Secondary concern ID (1-13) from skin_concerns table';
COMMENT ON COLUMN products.concern_id_3 IS 'Tertiary concern ID (1-13) from skin_concerns table';

-- Add check constraints to ensure valid concern IDs (1-13)
ALTER TABLE products 
ADD CONSTRAINT check_concern_id_1 CHECK (concern_id_1 IS NULL OR (concern_id_1 >= 1 AND concern_id_1 <= 13)),
ADD CONSTRAINT check_concern_id_2 CHECK (concern_id_2 IS NULL OR (concern_id_2 >= 1 AND concern_id_2 <= 13)),
ADD CONSTRAINT check_concern_id_3 CHECK (concern_id_3 IS NULL OR (concern_id_3 >= 1 AND concern_id_3 <= 13));

-- Add constraint to ensure concerns are in ascending order
ALTER TABLE products 
ADD CONSTRAINT check_concerns_order CHECK (
    (concern_id_1 IS NULL OR concern_id_2 IS NULL OR concern_id_1 < concern_id_2) AND
    (concern_id_2 IS NULL OR concern_id_3 IS NULL OR concern_id_2 < concern_id_3)
);

-- Create indexes for efficient querying by concerns
CREATE INDEX idx_products_concern_id_1 ON products(concern_id_1);
CREATE INDEX idx_products_concern_id_2 ON products(concern_id_2);
CREATE INDEX idx_products_concern_id_3 ON products(concern_id_3);