-- Convert ConcernList (skin_concerns) and MBTIList (skin_mbtis) entity IDs from BIGINT to SMALLINT

-- Drop foreign key constraints that reference these tables before altering column types
-- Check if user_skin_concerns join table exists and drop its foreign keys first
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'user_skin_concerns') THEN
        -- Drop foreign key constraints on join table
        ALTER TABLE user_skin_concerns DROP CONSTRAINT IF EXISTS fk_user_skin_concerns_concern;
        ALTER TABLE user_skin_concerns DROP CONSTRAINT IF EXISTS fk_user_skin_concerns_userskin;
        
        -- Alter the concern_id column type in the join table
        ALTER TABLE user_skin_concerns ALTER COLUMN concern_id TYPE SMALLINT;
    END IF;
END $$;

-- Drop foreign key constraints on user_skins table if they exist
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'user_skins') THEN
        ALTER TABLE user_skins DROP CONSTRAINT IF EXISTS fk_user_skins_mbti;
        
        -- Alter the mbti_id column type in user_skins table
        ALTER TABLE user_skins ALTER COLUMN mbti_id TYPE SMALLINT;
    END IF;
END $$;

-- For skin_concerns table
ALTER TABLE skin_concerns 
ALTER COLUMN id TYPE SMALLINT;

-- Reset the sequence to start from 1
-- Note: PostgreSQL auto-creates sequences for IDENTITY columns
ALTER SEQUENCE skin_concerns_id_seq RESTART WITH 1;

-- For skin_mbtis table  
ALTER TABLE skin_mbtis 
ALTER COLUMN id TYPE SMALLINT;

-- Reset the sequence to start from 1
ALTER SEQUENCE skin_mbtis_id_seq RESTART WITH 1;

-- Re-add foreign key constraints
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'user_skin_concerns') THEN
        ALTER TABLE user_skin_concerns 
        ADD CONSTRAINT fk_user_skin_concerns_concern 
        FOREIGN KEY (concern_id) REFERENCES skin_concerns(id);
        
        ALTER TABLE user_skin_concerns 
        ADD CONSTRAINT fk_user_skin_concerns_userskin 
        FOREIGN KEY (user_skin_id) REFERENCES user_skins(id);
    END IF;
    
    IF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'user_skins') THEN
        ALTER TABLE user_skins 
        ADD CONSTRAINT fk_user_skins_mbti 
        FOREIGN KEY (mbti_id) REFERENCES skin_mbtis(id);
    END IF;
END $$;

-- Create indexes for better performance (if not already exist)
CREATE INDEX IF NOT EXISTS idx_skin_concerns_label ON skin_concerns(label);
CREATE INDEX IF NOT EXISTS idx_skin_mbtis_mbti_code ON skin_mbtis(mbti_code);
CREATE INDEX IF NOT EXISTS idx_user_skin_concerns_concern_id ON user_skin_concerns(concern_id);
CREATE INDEX IF NOT EXISTS idx_user_skins_mbti_id ON user_skins(mbti_id);