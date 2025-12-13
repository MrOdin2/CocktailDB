import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { Subscription } from 'rxjs';
import { ApiService } from '../../../services/api.service';
import { MeasureService } from '../../../services/measure.service';
import { TranslateService } from '../../../services/translate.service';
import { TranslatePipe } from '../../../pipes/translate.pipe';
import { Cocktail, Ingredient, MeasureUnit, CocktailsWithSubstitutions } from '../../../models/models';

@Component({
  selector: 'app-visitor-recipe',
  standalone: true,
  imports: [CommonModule, RouterModule, TranslatePipe],
  templateUrl: './visitor-recipe.component.html',
  styleUrls: ['./visitor-recipe.component.css']
})
export class VisitorRecipeComponent implements OnInit, OnDestroy {
  cocktail: Cocktail | null = null;
  ingredients: Map<number, Ingredient> = new Map();
  cocktailsWithSubstitutions: CocktailsWithSubstitutions | null = null;
  loading: boolean = false;
  error: string | null = null;
  
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
    private route: ActivatedRoute,
    private router: Router,
    private apiService: ApiService,
    private measureService: MeasureService,
    private translateService: TranslateService
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.loadCocktail(+id);
      this.loadSubstitutionInfo();
    } else {
      this.error = 'Invalid cocktail ID';
    }
    
    this.unitSubscription = this.measureService.getUnit().subscribe(unit => {
      this.currentUnit = unit;
    });
  }
  
  ngOnDestroy(): void {
    this.unitSubscription?.unsubscribe();
  }

  loadCocktail(id: number): void {
    this.loading = true;
    this.error = null;

    this.apiService.getCocktailById(id).subscribe({
      next: (cocktail) => {
        this.cocktail = cocktail;
        this.loadIngredients(cocktail);
      },
      error: (err) => {
        console.error('Error loading cocktail:', err);
        this.error = 'Failed to load cocktail recipe. Please try again.';
        this.loading = false;
      }
    });
  }
  
  // Compute substitute info for all cocktail ingredients
  private computeSubstituteInfo(): void {
    if (!this.cocktail) return;
    
    this.ingredientSubstituteInfo.clear();
    
    this.cocktail.ingredients.forEach(item => {
      const ingredient = this.ingredients.get(item.ingredientId);
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
      if (ingredient.substituteIds) {
        ingredient.substituteIds.forEach(subId => {
          const sub = this.ingredients.get(subId);
          if (sub) {
            if (sub.inStock) {
              substitutesInStock.push(sub.name);
            } else {
              substitutesOutOfStock.push(sub.name);
            }
          }
        });
      }
      
      // Process alternatives
      if (ingredient.alternativeIds) {
        ingredient.alternativeIds.forEach(altId => {
          const alt = this.ingredients.get(altId);
          if (alt) {
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

  loadIngredients(cocktail: Cocktail): void {
    this.apiService.getAllIngredients().subscribe({
      next: (allIngredients) => {
        // Only load ingredients that are needed for this cocktail or its substitutes/alternatives
        const cocktailIngredientIds = new Set(cocktail.ingredients.map(ci => ci.ingredientId));
        
        allIngredients.forEach(ingredient => {
          if (ingredient.id && (
            cocktailIngredientIds.has(ingredient.id) ||
            // Include substitutes and alternatives of cocktail ingredients
            (ingredient.substituteIds && ingredient.substituteIds.some(id => cocktailIngredientIds.has(id))) ||
            (ingredient.alternativeIds && ingredient.alternativeIds.some(id => cocktailIngredientIds.has(id)))
          )) {
            this.ingredients.set(ingredient.id, ingredient);
          }
        });
        
        // Also add all ingredients that are substitutes/alternatives of loaded ingredients
        allIngredients.forEach(ingredient => {
          if (ingredient.id) {
            const existingIngredient = this.ingredients.get(ingredient.id);
            if (existingIngredient) {
              // Add substitutes
              if (existingIngredient.substituteIds) {
                existingIngredient.substituteIds.forEach(subId => {
                  const sub = allIngredients.find(ing => ing.id === subId);
                  if (sub && sub.id) {
                    this.ingredients.set(sub.id, sub);
                  }
                });
              }
              // Add alternatives
              if (existingIngredient.alternativeIds) {
                existingIngredient.alternativeIds.forEach(altId => {
                  const alt = allIngredients.find(ing => ing.id === altId);
                  if (alt && alt.id) {
                    this.ingredients.set(alt.id, alt);
                  }
                });
              }
            }
          }
        });
        
        // Compute substitute info after all ingredients are loaded
        this.computeSubstituteInfo();
        
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading ingredients:', err);
        this.loading = false;
      }
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
    const ingredient = this.ingredients.get(ingredientId);
    return ingredient ? ingredient.name : 'Unknown ingredient';
  }

  getIngredient(ingredientId: number): Ingredient | undefined {
    return this.ingredients.get(ingredientId);
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
    const ingredient = this.ingredients.get(ingredientId);
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
    this.router.navigate(['/visitor/cocktails']);
  }

  getAlcoholType(abv: number): string {
    if (abv === 0) {
      return this.translateService.translate('visitor.cocktailList.nonAlcoholic');
    }
    return this.translateService.translate('visitor.cocktailList.alcoholic');
  }

  getAlcoholClass(abv: number): string {
    return abv > 0 ? 'alcoholic' : 'non-alcoholic';
  }
}
