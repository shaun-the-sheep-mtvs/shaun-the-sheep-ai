-- Remove unique constraint on user_id in user_skins table to allow multiple skin analyses per user
-- The constraint name "ukq47qitu0go3w2x70l7hy0p247" might be auto-generated, so we'll drop by column

-- Drop the unique constraint on user_id
ALTER TABLE user_skins DROP CONSTRAINT IF EXISTS ukq47qitu0go3w2x70l7hy0p247;

-- Also drop any other unique constraints that might exist on user_id
DO $$
DECLARE
    constraint_name TEXT;
BEGIN
    -- Find and drop any unique constraints on user_id column
    FOR constraint_name IN 
        SELECT conname 
        FROM pg_constraint 
        WHERE conrelid = 'user_skins'::regclass 
        AND contype = 'u' 
        AND conkey = (SELECT array_agg(attnum) FROM pg_attribute WHERE attrelid = 'user_skins'::regclass AND attname = 'user_id')
    LOOP
        EXECUTE 'ALTER TABLE user_skins DROP CONSTRAINT ' || constraint_name;
        RAISE NOTICE 'Dropped unique constraint: %', constraint_name;
    END LOOP;
END $$;

-- Add comment explaining the relationship change
COMMENT ON COLUMN user_skins.user_id IS 'Foreign key to users table - one user can have multiple skin analyses over time';