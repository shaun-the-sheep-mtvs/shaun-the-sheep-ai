-- V7: Refactor Userskin concerns from ManyToMany to individual fields
-- This migration changes the user_skins table to have individual concern fields
-- instead of a separate junction table

-- Step 1: Add new concern columns to user_skins table
ALTER TABLE user_skins ADD COLUMN concern1_id SMALLINT;
ALTER TABLE user_skins ADD COLUMN concern2_id SMALLINT;
ALTER TABLE user_skins ADD COLUMN concern3_id SMALLINT;

-- Step 2: Add foreign key constraints for the new columns
ALTER TABLE user_skins ADD CONSTRAINT fk_user_skins_concern1
    FOREIGN KEY (concern1_id) REFERENCES skin_concerns(id);
ALTER TABLE user_skins ADD CONSTRAINT fk_user_skins_concern2
    FOREIGN KEY (concern2_id) REFERENCES skin_concerns(id);
ALTER TABLE user_skins ADD CONSTRAINT fk_user_skins_concern3
    FOREIGN KEY (concern3_id) REFERENCES skin_concerns(id);

-- Step 3: Migrate existing data from user_skin_concerns to new fields
-- This will take the first 3 concerns for each user_skin
WITH ranked_concerns AS (
    SELECT
        user_skin_id,
        concern_id,
        ROW_NUMBER() OVER (PARTITION BY user_skin_id ORDER BY concern_id) as rn
    FROM user_skin_concerns
)
UPDATE user_skins
SET concern1_id = (
    SELECT concern_id
    FROM ranked_concerns
    WHERE ranked_concerns.user_skin_id = user_skins.id AND rn = 1
),
concern2_id = (
    SELECT concern_id
    FROM ranked_concerns
    WHERE ranked_concerns.user_skin_id = user_skins.id AND rn = 2
),
concern3_id = (
    SELECT concern_id
    FROM ranked_concerns
    WHERE ranked_concerns.user_skin_id = user_skins.id AND rn = 3
);
--
-- -- Step 4: Drop the old junction table
-- DROP TABLE IF EXISTS user_skin_concerns;