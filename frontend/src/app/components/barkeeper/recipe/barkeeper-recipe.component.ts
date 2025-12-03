import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { Location } from '@angular/common';
import { Subscription } from 'rxjs';
import { ApiService } from '../../../services/api.service';
import { MeasureService } from '../../../services/measure.service';
import { TranslatePipe } from '../../../pipes/translate.pipe';
import { Cocktail, Ingredient, MeasureUnit } from '../../../models/models';

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

  getIngredientName(ingredientId: number): string {
    const ingredient = this.ingredientMap.get(ingredientId);
    return ingredient ? ingredient.name : 'Unknown';
  }
  
  isIngredientInStock(ingredientId: number): boolean {
    const ingredient = this.ingredientMap.get(ingredientId);
    return ingredient ? ingredient.inStock : false;
  }
  
  getInStockSubstitute(ingredientId: number): Ingredient | null {
    const ingredient = this.ingredientMap.get(ingredientId);
    if (!ingredient || ingredient.inStock) return null;
    
    // First check substitutes
    const substituteIds = ingredient.substituteIds || [];
    for (const subId of substituteIds) {
      const sub = this.ingredientMap.get(subId);
      if (sub && sub.inStock) {
        return sub;
      }
    }
    return null;
  }
  
  getInStockAlternative(ingredientId: number): Ingredient | null {
    const ingredient = this.ingredientMap.get(ingredientId);
    if (!ingredient || ingredient.inStock) return null;
    
    // Only check alternatives if no substitute found
    if (this.getInStockSubstitute(ingredientId)) return null;
    
    const alternativeIds = ingredient.alternativeIds || [];
    for (const altId of alternativeIds) {
      const alt = this.ingredientMap.get(altId);
      if (alt && alt.inStock) {
        return alt;
      }
    }
    return null;
  }
  
  formatMeasure(measureMl: number): string {
    return this.measureService.formatMeasure(measureMl, this.currentUnit);
  }

  goBack(): void {
    this.location.back();
  }
}
