export enum IngredientType {
  SPIRIT = 'SPIRIT',
  LIQUEUR = 'LIQUEUR',
  WINE = 'WINE',
  BEER = 'BEER',
  JUICE = 'JUICE',
  SODA = 'SODA',
  SYRUP = 'SYRUP',
  BITTERS = 'BITTERS',
  GARNISH = 'GARNISH',
  OTHER = 'OTHER'
}

export enum MeasureUnit {
  ML = 'ml',
  OZ = 'oz',
  CL = 'cl'
}

export interface Ingredient {
  id?: number;
  name: string;
  type: IngredientType;
  abv: number;
  inStock: boolean;
  substituteIds?: number[];
  alternativeIds?: number[];
}

export interface CocktailIngredient {
  ingredientId: number;
  measureMl: number;
}

export interface Cocktail {
  id?: number;
  name: string;
  ingredients: CocktailIngredient[];
  steps: string[];
  notes?: string;
  tags: string[];
  abv: number;
  baseSpirit: string;
}

export enum CocktailAvailability {
  AVAILABLE = 'AVAILABLE',
  AVAILABLE_WITH_SUBSTITUTES = 'AVAILABLE_WITH_SUBSTITUTES',
  AVAILABLE_WITH_ALTERNATIVES = 'AVAILABLE_WITH_ALTERNATIVES',
  UNAVAILABLE = 'UNAVAILABLE'
}

export interface CocktailAvailabilityInfo {
  cocktail: Cocktail;
  availability: CocktailAvailability;
  substitutionsNeeded: { [originalIngredientId: number]: number };
  alternativesNeeded: { [originalIngredientId: number]: number };
}
