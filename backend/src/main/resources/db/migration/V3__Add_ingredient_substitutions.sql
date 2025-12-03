-- Migration: Add ingredient substitutes and alternatives tables
-- Substitutes: Ingredients that can be used instead (e.g., "Coconut Rum" instead of "Malibu")
-- Alternatives: Noticeably different but usable alternatives (e.g., Prosecco instead of Champagne)

-- Create ingredient_substitutes table
CREATE TABLE IF NOT EXISTS ingredient_substitutes (
    ingredient_id BIGINT NOT NULL,
    substitute_id BIGINT NOT NULL,
    CONSTRAINT fk_ingredient_substitutes_ingredient FOREIGN KEY (ingredient_id) REFERENCES ingredients(id) ON DELETE CASCADE,
    CONSTRAINT fk_ingredient_substitutes_substitute FOREIGN KEY (substitute_id) REFERENCES ingredients(id) ON DELETE CASCADE,
    PRIMARY KEY (ingredient_id, substitute_id)
);

-- Create ingredient_alternatives table
CREATE TABLE IF NOT EXISTS ingredient_alternatives (
    ingredient_id BIGINT NOT NULL,
    alternative_id BIGINT NOT NULL,
    CONSTRAINT fk_ingredient_alternatives_ingredient FOREIGN KEY (ingredient_id) REFERENCES ingredients(id) ON DELETE CASCADE,
    CONSTRAINT fk_ingredient_alternatives_alternative FOREIGN KEY (alternative_id) REFERENCES ingredients(id) ON DELETE CASCADE,
    PRIMARY KEY (ingredient_id, alternative_id)
);

-- Create indexes for better query performance
CREATE INDEX IF NOT EXISTS idx_ingredient_substitutes_ingredient_id ON ingredient_substitutes(ingredient_id);
CREATE INDEX IF NOT EXISTS idx_ingredient_substitutes_substitute_id ON ingredient_substitutes(substitute_id);
CREATE INDEX IF NOT EXISTS idx_ingredient_alternatives_ingredient_id ON ingredient_alternatives(ingredient_id);
CREATE INDEX IF NOT EXISTS idx_ingredient_alternatives_alternative_id ON ingredient_alternatives(alternative_id);
