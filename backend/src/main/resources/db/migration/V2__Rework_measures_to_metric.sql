-- Migration: Rework measures from string to metric (ml)
-- This migration converts the measure column from VARCHAR to DOUBLE PRECISION (ml)
-- 
-- Non-liquid ingredients (garnishes, leaves, etc.) are stored as negative values
-- The absolute value represents the quantity (e.g., -8 for 8 mint leaves, -2 for 2 dashes)
-- The frontend displays the absolute value without any unit

-- Step 1: Add new column for metric measurement
ALTER TABLE cocktail_ingredients ADD COLUMN measure_ml DOUBLE PRECISION;

-- Step 2: Migrate existing data - attempt to parse numeric values
-- Default value explanations:
--   - 30ml (1 oz): Standard pour for unparseable values, common base spirit measurement
--   - 60ml: Used for "top" instructions (e.g., "top with soda")
--   - Negative values: Used for non-liquid ingredients (e.g., -8 for 8 leaves, -2 for 2 dashes)
UPDATE cocktail_ingredients SET measure_ml = CASE
    -- Try to extract numeric part from common patterns
    WHEN measure ~ '^[0-9]+(\.[0-9]+)?\s*(ml|ML)' THEN
        CAST(REGEXP_REPLACE(measure, '[^0-9.]', '', 'g') AS DOUBLE PRECISION)
    WHEN measure ~ '^[0-9]+(\.[0-9]+)?\s*(oz|OZ)' THEN
        CAST(REGEXP_REPLACE(measure, '[^0-9.]', '', 'g') AS DOUBLE PRECISION) * 29.5735
    WHEN measure ~ '^[0-9]+(\.[0-9]+)?\s*(cl|CL)' THEN
        CAST(REGEXP_REPLACE(measure, '[^0-9.]', '', 'g') AS DOUBLE PRECISION) * 10
    WHEN measure ~ '^[0-9]+(\.[0-9]+)?$' THEN
        CAST(measure AS DOUBLE PRECISION)
    ELSE 30.0 -- Default to 30ml (1 oz) for unparseable liquid values
END;

-- Step 3: Set NOT NULL constraint and default
ALTER TABLE cocktail_ingredients ALTER COLUMN measure_ml SET NOT NULL;
ALTER TABLE cocktail_ingredients ALTER COLUMN measure_ml SET DEFAULT -1;

-- Step 4: Drop the old measure column
ALTER TABLE cocktail_ingredients DROP COLUMN measure;
