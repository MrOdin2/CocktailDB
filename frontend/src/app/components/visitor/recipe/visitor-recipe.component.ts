import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { Subscription } from 'rxjs';
import { ApiService } from '../../../services/api.service';
import { MeasureService } from '../../../services/measure.service';
import { Cocktail, Ingredient, MeasureUnit } from '../../../models/models';

@Component({
  selector: 'app-visitor-recipe',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './visitor-recipe.component.html',
  styleUrls: ['./visitor-recipe.component.css']
})
export class VisitorRecipeComponent implements OnInit, OnDestroy {
  cocktail: Cocktail | null = null;
  ingredients: Map<number, Ingredient> = new Map();
  loading: boolean = false;
  error: string | null = null;
  
  currentUnit: MeasureUnit = MeasureUnit.ML;
  private unitSubscription?: Subscription;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private apiService: ApiService,
    private measureService: MeasureService
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.loadCocktail(+id);
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

  loadIngredients(cocktail: Cocktail): void {
    const ingredientIds = cocktail.ingredients.map(ci => ci.ingredientId);
    
    this.apiService.getAllIngredients().subscribe({
      next: (allIngredients) => {
        allIngredients.forEach(ingredient => {
          if (ingredient.id && ingredientIds.includes(ingredient.id)) {
            this.ingredients.set(ingredient.id, ingredient);
          }
        });
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading ingredients:', err);
        this.loading = false;
      }
    });
  }

  getIngredientName(ingredientId: number): string {
    const ingredient = this.ingredients.get(ingredientId);
    return ingredient ? ingredient.name : 'Unknown ingredient';
  }
  
  formatMeasure(measureMl: number): string {
    return this.measureService.formatMeasure(measureMl, this.currentUnit);
  }

  goBack(): void {
    this.router.navigate(['/visitor/cocktails']);
  }

  getAlcoholType(abv: number): string {
    return abv > 0 ? 'Alcoholic' : 'Non-Alcoholic';
  }

  getAlcoholClass(abv: number): string {
    return abv > 0 ? 'alcoholic' : 'non-alcoholic';
  }
}
