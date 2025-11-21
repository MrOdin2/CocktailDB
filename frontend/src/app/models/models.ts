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

export interface Ingredient {
  id?: number;
  name: string;
  type: IngredientType;
  abv: number;
  inStock: boolean;
}

export interface CocktailIngredient {
  ingredientId: number;
  measure: string;
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
