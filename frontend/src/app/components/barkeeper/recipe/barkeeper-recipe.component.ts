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
  ingredients: Ingredient[] = [];
  ingredientMap: Map<number, Ingredient> = new Map();
  cocktailsWithSubstitutions: CocktailsWithSubstitutions | null = null;
  isLoading = false;
  
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
        this.isLoading = false;
      },
      error: (error: any) => {
        console.error('Error loading cocktail:', error);
        this.isLoading = false;
      }
    });
  }

  loadIngredients(): void {
    this.apiService.getAllIngredients().subscribe({
      next: (ingredients: Ingredient[]) => {
        this.ingredients = ingredients;
        this.ingredientMap = new Map(ingredients.map(i => [i.id!, i]));
      },
      error: (error: any) => {
        console.error('Error loading ingredients:', error);
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

  // Get substitute/alternative ingredients for a given ingredient
  getSubstituteInfo(ingredientId: number): { type: 'substitute' | 'alternative' | null, names: string[] } {
    const ingredient = this.ingredientMap.get(ingredientId);
    if (!ingredient) return { type: null, names: [] };

    // Check if we have substitutes
    if (ingredient.substituteIds && ingredient.substituteIds.length > 0) {
      const substituteNames = ingredient.substituteIds
        .map(id => this.getIngredientName(id))
        .filter(name => name !== 'Unknown');
      if (substituteNames.length > 0) {
        return { type: 'substitute', names: substituteNames };
      }
    }

    // Check if we have alternatives
    if (ingredient.alternativeIds && ingredient.alternativeIds.length > 0) {
      const alternativeNames = ingredient.alternativeIds
        .map(id => this.getIngredientName(id))
        .filter(name => name !== 'Unknown');
      if (alternativeNames.length > 0) {
        return { type: 'alternative', names: alternativeNames };
      }
    }

    return { type: null, names: [] };
  }
  
  formatMeasure(measureMl: number): string {
    return this.measureService.formatMeasure(measureMl, this.currentUnit);
  }

  goBack(): void {
    this.location.back();
  }
}
