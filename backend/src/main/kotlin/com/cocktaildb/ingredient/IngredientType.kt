package com.cocktaildb.ingredient

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Type of ingredient")
enum class IngredientType {
    @Schema(description = "Base spirit (e.g., vodka, rum, whiskey)")
    SPIRIT,
    @Schema(description = "Liqueur or flavored spirit")
    LIQUEUR,
    @Schema(description = "Wine-based ingredient")
    WINE,
    @Schema(description = "Beer or malt beverage")
    BEER,
    @Schema(description = "Fruit or vegetable juice")
    JUICE,
    @Schema(description = "Carbonated soft drink")
    SODA,
    @Schema(description = "Sweetener or flavored syrup")
    SYRUP,
    @Schema(description = "Aromatic bitters")
    BITTERS,
    @Schema(description = "Garnish or decoration (count-based)")
    GARNISH,
    @Schema(description = "Other ingredient type (count-based)")
    OTHER
}