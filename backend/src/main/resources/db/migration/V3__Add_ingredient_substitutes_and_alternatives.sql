-- Add substitutes and alternatives relationships for ingredients
-- Substitutes: Ingredients that can be used instead (e.g., generic vs branded)
-- Alternatives: Noticeably different but still usable (e.g., Champagne vs Prosecco)

-- Create ingredient_substitutes table (many-to-many)
CREATE TABLE IF NOT EXISTS ingredient_substitutes (
    ingredient_id BIGINT NOT NULL,
    substitute_id BIGINT NOT NULL,
    PRIMARY KEY (ingredient_id, substitute_id),
    CONSTRAINT fk_ingredient_substitutes_ingredient FOREIGN KEY (ingredient_id) REFERENCES ingredients(id) ON DELETE CASCADE,
    CONSTRAINT fk_ingredient_substitutes_substitute FOREIGN KEY (substitute_id) REFERENCES ingredients(id) ON DELETE CASCADE,
    CONSTRAINT chk_not_self_substitute CHECK (ingredient_id != substitute_id)
);

-- Create ingredient_alternatives table (many-to-many)
CREATE TABLE IF NOT EXISTS ingredient_alternatives (
    ingredient_id BIGINT NOT NULL,
    alternative_id BIGINT NOT NULL,
    PRIMARY KEY (ingredient_id, alternative_id),
    CONSTRAINT fk_ingredient_alternatives_ingredient FOREIGN KEY (ingredient_id) REFERENCES ingredients(id) ON DELETE CASCADE,
    CONSTRAINT fk_ingredient_alternatives_alternative FOREIGN KEY (alternative_id) REFERENCES ingredients(id) ON DELETE CASCADE,
    CONSTRAINT chk_not_self_alternative CHECK (ingredient_id != alternative_id)
);

-- Create indexes for better query performance
CREATE INDEX IF NOT EXISTS idx_ingredient_substitutes_substitute_id ON ingredient_substitutes(substitute_id);
CREATE INDEX IF NOT EXISTS idx_ingredient_alternatives_alternative_id ON ingredient_alternatives(alternative_id);
