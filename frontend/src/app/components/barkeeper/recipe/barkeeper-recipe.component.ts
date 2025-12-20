import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { Location } from '@angular/common';
import { Subscription } from 'rxjs';
import { ApiService } from '../../../services/api.service';
import { MeasureService } from '../../../services/measure.service';
import { TranslatePipe } from '../../../pipes/translate.pipe';
import { Cocktail, Ingredient, MeasureUnit, CocktailsWithSubstitutions } from '../../../models/models';

@Component({
  selector: 'app-barkeeper-recipe',
  standalone: true,
  imports: [CommonModule, RouterModule, TranslatePipe],
  templateUrl: './barkeeper-recipe.component.html',
  styleUrls: ['../barkeeper-shared.css', './barkeeper-recipe.component.css']
})
export class BarkeeperRecipeComponent implements OnInit, OnDestroy {
  cocktail: Cocktail | null = null;
  baseCocktail: Cocktail | null = null;
  ingredients: Ingredient[] = [];
  ingredientMap: Map<number, Ingredient> = new Map();
  cocktailsWithSubstitutions: CocktailsWithSubstitutions | null = null;
  isLoading = false;
  
  // Pre-computed properties for each ingredient to avoid repeated method calls in template
  ingredientSubstituteInfo: Map<number, { 
    substitutes: { inStock: string[], outOfStock: string[] }, 
    alternatives: { inStock: string[], outOfStock: string[] },
    hasSubstitute: boolean,
    hasAlternative: boolean
  }> = new Map();
  
  currentUnit: MeasureUnit = MeasureUnit.ML;
  private unitSubscription?: Subscription;

  constructor(
    private apiService: ApiService,
    private measureService: MeasureService,
    private route: ActivatedRoute,
    private router: Router,
    private location: Location
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.loadRecipe(+id);
      this.loadIngredients();
      this.loadSubstitutionInfo();
    }
    
    this.unitSubscription = this.measureService.getUnit().subscribe(unit => {
      this.currentUnit = unit;
    });
  }
  
  ngOnDestroy(): void {
    this.unitSubscription?.unsubscribe();
  }

  loadRecipe(id: number): void {
    this.isLoading = true;
    this.apiService.getCocktailById(id).subscribe({
      next: (cocktail: Cocktail) => {
        this.cocktail = cocktail;
        // Load base cocktail if this is a variation
        if (cocktail.variationOfId) {
          this.loadBaseCocktail(cocktail.variationOfId);
        }
        // Compute substitute info after cocktail is loaded (if ingredients are ready)
        if (this.ingredientMap.size > 0) {
          this.computeSubstituteInfo();
        }
        this.isLoading = false;
      },
      error: (error: any) => {
        console.error('Error loading cocktail:', error);
        this.isLoading = false;
      }
    });
  }
  
  loadBaseCocktail(baseCocktailId: number): void {
    this.apiService.getCocktailById(baseCocktailId).subscribe({
      next: (cocktail) => {
        this.baseCocktail = cocktail;
      },
      error: (err) => {
        console.error('Error loading base cocktail:', err);
        // Non-critical error, don't show to user
      }
    });
  }

  loadIngredients(): void {
    this.apiService.getAllIngredients().subscribe({
      next: (ingredients: Ingredient[]) => {
        this.ingredients = ingredients;
        this.ingredientMap = new Map(ingredients.map(i => [i.id!, i]));
        // Compute substitute info after ingredients are loaded (if cocktail is ready)
        if (this.cocktail) {
          this.computeSubstituteInfo();
        }
      },
      error: (error: any) => {
        console.error('Error loading ingredients:', error);
      }
    });
  }
  
  // Compute substitute info for all cocktail ingredients
  private computeSubstituteInfo(): void {
    if (!this.cocktail) return;
    
    this.ingredientSubstituteInfo.clear();
    
    this.cocktail.ingredients.forEach(item => {
      const ingredient = this.ingredientMap.get(item.ingredientId);
      if (!ingredient) {
        this.ingredientSubstituteInfo.set(item.ingredientId, {
          substitutes: { inStock: [], outOfStock: [] },
          alternatives: { inStock: [], outOfStock: [] },
          hasSubstitute: false,
          hasAlternative: false
        });
        return;
      }
      
      const substitutesInStock: string[] = [];
      const substitutesOutOfStock: string[] = [];
      const alternativesInStock: string[] = [];
      const alternativesOutOfStock: string[] = [];
      
      // Process substitutes
      if (ingredient.substituteIds && Array.isArray(ingredient.substituteIds)) {
        ingredient.substituteIds.forEach(subId => {
          const sub = this.ingredientMap.get(subId);
          if (sub && sub.name) {
            if (sub.inStock) {
              substitutesInStock.push(sub.name);
            } else {
              substitutesOutOfStock.push(sub.name);
            }
          }
        });
      }
      
      // Process alternatives
      if (ingredient.alternativeIds && Array.isArray(ingredient.alternativeIds)) {
        ingredient.alternativeIds.forEach(altId => {
          const alt = this.ingredientMap.get(altId);
          if (alt && alt.name) {
            if (alt.inStock) {
              alternativesInStock.push(alt.name);
            } else {
              alternativesOutOfStock.push(alt.name);
            }
          }
        });
      }
      
      this.ingredientSubstituteInfo.set(item.ingredientId, {
        substitutes: { inStock: substitutesInStock, outOfStock: substitutesOutOfStock },
        alternatives: { inStock: alternativesInStock, outOfStock: alternativesOutOfStock },
        hasSubstitute: substitutesInStock.length > 0 || substitutesOutOfStock.length > 0,
        hasAlternative: alternativesInStock.length > 0 || alternativesOutOfStock.length > 0
      });
    });
  }

  loadSubstitutionInfo(): void {
    this.apiService.getAvailableCocktailsWithSubstitutions().subscribe({
      next: (result: CocktailsWithSubstitutions) => {
        this.cocktailsWithSubstitutions = result;
      },
      error: (error: any) => {
        console.error('Error loading substitution info:', error);
      }
    });
  }

  getIngredientName(ingredientId: number): string {
    const ingredient = this.ingredientMap.get(ingredientId);
    return ingredient ? ingredient.name : 'Unknown';
  }

  getIngredient(ingredientId: number): Ingredient | undefined {
    return this.ingredientMap.get(ingredientId);
  }

  // Check if this cocktail uses substitutes
  usesSubstitutes(): boolean {
    if (!this.cocktail || !this.cocktailsWithSubstitutions) return false;
    return this.cocktailsWithSubstitutions.withSubstitutes.some(c => c.id === this.cocktail!.id);
  }

  // Check if this cocktail uses alternatives
  usesAlternatives(): boolean {
    if (!this.cocktail || !this.cocktailsWithSubstitutions) return false;
    return this.cocktailsWithSubstitutions.withAlternatives.some(c => c.id === this.cocktail!.id);
  }

  // Check if an ingredient is out of stock
  isOutOfStock(ingredientId: number): boolean {
    const ingredient = this.ingredientMap.get(ingredientId);
    return ingredient ? !ingredient.inStock : false;
  }

  // Get pre-computed substitute info for an ingredient
  getSubstituteInfo(ingredientId: number): { 
    substitutes: { inStock: string[], outOfStock: string[] }, 
    alternatives: { inStock: string[], outOfStock: string[] },
    hasSubstitute: boolean,
    hasAlternative: boolean
  } {
    return this.ingredientSubstituteInfo.get(ingredientId) || {
      substitutes: { inStock: [], outOfStock: [] },
      alternatives: { inStock: [], outOfStock: [] },
      hasSubstitute: false,
      hasAlternative: false
    };
  }
  
  formatMeasure(measureMl: number): string {
    return this.measureService.formatMeasure(measureMl, this.currentUnit);
  }

  goBack(): void {
    this.location.back();
  }
}
