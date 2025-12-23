-- Add glassware types, ice types, and cocktail variation support
-- This migration adds support for:
-- 1. Glassware recommendations (e.g., highball, martini glass)
-- 2. Ice type recommendations (e.g., cubed, crushed)
-- 3. Cocktail variations (linking variations to base recipes)

-- Add variation_of_id column to cocktails table
ALTER TABLE cocktails ADD COLUMN variation_of_id BIGINT;

-- Add foreign key constraint for variation_of_id
ALTER TABLE cocktails ADD CONSTRAINT fk_cocktails_variation_of 
    FOREIGN KEY (variation_of_id) REFERENCES cocktails(id) ON DELETE SET NULL;

-- Create cocktail_glassware table for glassware types
CREATE TABLE IF NOT EXISTS cocktail_glassware (
    cocktail_id BIGINT NOT NULL,
    glassware VARCHAR(255) NOT NULL,
    CONSTRAINT fk_cocktail_glassware_cocktail FOREIGN KEY (cocktail_id) REFERENCES cocktails(id) ON DELETE CASCADE
);

-- Create cocktail_ice table for ice types
CREATE TABLE IF NOT EXISTS cocktail_ice (
    cocktail_id BIGINT NOT NULL,
    ice VARCHAR(255) NOT NULL,
    CONSTRAINT fk_cocktail_ice_cocktail FOREIGN KEY (cocktail_id) REFERENCES cocktails(id) ON DELETE CASCADE
);

-- Create indexes for better query performance
CREATE INDEX IF NOT EXISTS idx_cocktails_variation_of_id ON cocktails(variation_of_id);
CREATE INDEX IF NOT EXISTS idx_cocktail_glassware_cocktail_id ON cocktail_glassware(cocktail_id);
CREATE INDEX IF NOT EXISTS idx_cocktail_ice_cocktail_id ON cocktail_ice(cocktail_id);
