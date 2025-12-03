import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { Location } from '@angular/common';
import { Subscription } from 'rxjs';
import { ApiService } from '../../../services/api.service';
import { MeasureService } from '../../../services/measure.service';
import { TranslatePipe } from '../../../pipes/translate.pipe';
import { Cocktail, Ingredient, MeasureUnit } from '../../../models/models';

interface IngredientStatus {
  inStock: boolean;
  substitute: Ingredient | null;
  alternative: Ingredient | null;
}

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
  ingredientStatusCache: Map<number, IngredientStatus> = new Map();
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
        this.buildIngredientStatusCache();
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
        this.buildIngredientStatusCache();
      },
      error: (error: any) => {
        console.error('Error loading ingredients:', error);
      }
    });
  }
  
  private buildIngredientStatusCache(): void {
    this.ingredientStatusCache.clear();
    if (!this.cocktail || this.ingredientMap.size === 0) return;
    
    for (const cocktailIng of this.cocktail.ingredients) {
      const status = this.calculateIngredientStatus(cocktailIng.ingredientId);
      this.ingredientStatusCache.set(cocktailIng.ingredientId, status);
    }
  }
  
  private calculateIngredientStatus(ingredientId: number): IngredientStatus {
    const ingredient = this.ingredientMap.get(ingredientId);
    if (!ingredient) {
      return { inStock: false, substitute: null, alternative: null };
    }
    
    if (ingredient.inStock) {
      return { inStock: true, substitute: null, alternative: null };
    }
    
    // Find substitute
    const substituteIds = ingredient.substituteIds || [];
    let substitute: Ingredient | null = null;
    for (const subId of substituteIds) {
      const sub = this.ingredientMap.get(subId);
      if (sub && sub.inStock) {
        substitute = sub;
        break;
      }
    }
    
    // Find alternative (only if no substitute found)
    let alternative: Ingredient | null = null;
    if (!substitute) {
      const alternativeIds = ingredient.alternativeIds || [];
      for (const altId of alternativeIds) {
        const alt = this.ingredientMap.get(altId);
        if (alt && alt.inStock) {
          alternative = alt;
          break;
        }
      }
    }
    
    return { inStock: false, substitute, alternative };
  }

  getIngredientName(ingredientId: number): string {
    const ingredient = this.ingredientMap.get(ingredientId);
    return ingredient ? ingredient.name : 'Unknown';
  }
  
  getIngredientStatus(ingredientId: number): IngredientStatus {
    return this.ingredientStatusCache.get(ingredientId) || 
      { inStock: false, substitute: null, alternative: null };
  }
  
  formatMeasure(measureMl: number): string {
    return this.measureService.formatMeasure(measureMl, this.currentUnit);
  }

  goBack(): void {
    this.location.back();
  }
}
