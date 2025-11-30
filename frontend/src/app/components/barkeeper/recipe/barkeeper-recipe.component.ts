import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { Location } from '@angular/common';
import { Subscription } from 'rxjs';
import { MeasureService } from '../../../services/measure.service';
import { Cocktail, Ingredient, MeasureUnit } from '../../../models/models';
import {IngredientService} from "../../../services/ingredient.service";
import {CocktailService} from "../../../services/cocktail.service";

@Component({
  selector: 'app-barkeeper-recipe',
  standalone: true,
  imports: [CommonModule, RouterModule],
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
    private ingredientService: IngredientService,
    private cocktailService: CocktailService,
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
    this.cocktailService.getById(id).subscribe({
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
    this.ingredientService.getAll().subscribe({
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
  
  formatMeasure(measureMl: number): string {
    return this.measureService.formatMeasure(measureMl, this.currentUnit);
  }

  goBack(): void {
    this.location.back();
  }
}
