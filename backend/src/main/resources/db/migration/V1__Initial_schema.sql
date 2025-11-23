-- CocktailDB Initial Schema Migration
-- This migration creates the initial database schema for the CocktailDB application

-- Create ingredients table
CREATE TABLE IF NOT EXISTS ingredients (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    type VARCHAR(50) NOT NULL,
    abv INTEGER NOT NULL DEFAULT 0,
    in_stock BOOLEAN NOT NULL DEFAULT false
);

-- Create cocktails table
CREATE TABLE IF NOT EXISTS cocktails (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    notes VARCHAR(1000),
    abv INTEGER NOT NULL DEFAULT 0,
    base_spirit VARCHAR(50) NOT NULL DEFAULT 'none'
);

-- Create cocktail_ingredients table (junction table)
CREATE TABLE IF NOT EXISTS cocktail_ingredients (
    cocktail_id BIGINT NOT NULL,
    ingredient_id BIGINT NOT NULL,
    measure VARCHAR(255) NOT NULL,
    CONSTRAINT fk_cocktail_ingredients_cocktail FOREIGN KEY (cocktail_id) REFERENCES cocktails(id) ON DELETE CASCADE,
    CONSTRAINT fk_cocktail_ingredients_ingredient FOREIGN KEY (ingredient_id) REFERENCES ingredients(id)
);

-- Create cocktail_steps table
CREATE TABLE IF NOT EXISTS cocktail_steps (
    cocktail_id BIGINT NOT NULL,
    step VARCHAR(1000) NOT NULL,
    CONSTRAINT fk_cocktail_steps_cocktail FOREIGN KEY (cocktail_id) REFERENCES cocktails(id) ON DELETE CASCADE
);

-- Create cocktail_tags table
CREATE TABLE IF NOT EXISTS cocktail_tags (
    cocktail_id BIGINT NOT NULL,
    tag VARCHAR(255) NOT NULL,
    CONSTRAINT fk_cocktail_tags_cocktail FOREIGN KEY (cocktail_id) REFERENCES cocktails(id) ON DELETE CASCADE
);

-- Create app_settings table
CREATE TABLE IF NOT EXISTS app_settings (
    id BIGSERIAL PRIMARY KEY,
    setting_key VARCHAR(255) NOT NULL UNIQUE,
    setting_value VARCHAR(255) NOT NULL
);

-- Create indexes for better query performance
CREATE INDEX IF NOT EXISTS idx_ingredients_type ON ingredients(type);
CREATE INDEX IF NOT EXISTS idx_ingredients_in_stock ON ingredients(in_stock);
CREATE INDEX IF NOT EXISTS idx_cocktails_base_spirit ON cocktails(base_spirit);
CREATE INDEX IF NOT EXISTS idx_cocktail_ingredients_cocktail_id ON cocktail_ingredients(cocktail_id);
CREATE INDEX IF NOT EXISTS idx_cocktail_ingredients_ingredient_id ON cocktail_ingredients(ingredient_id);
CREATE INDEX IF NOT EXISTS idx_cocktail_steps_cocktail_id ON cocktail_steps(cocktail_id);
CREATE INDEX IF NOT EXISTS idx_cocktail_tags_cocktail_id ON cocktail_tags(cocktail_id);
CREATE INDEX IF NOT EXISTS idx_app_settings_key ON app_settings(setting_key);
