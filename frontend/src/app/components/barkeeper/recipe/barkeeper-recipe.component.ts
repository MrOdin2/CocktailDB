import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { Location } from '@angular/common';
import { ApiService } from '../../../services/api.service';
import { Cocktail, Ingredient } from '../../../models/models';

@Component({
  selector: 'app-barkeeper-recipe',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './barkeeper-recipe.component.html',
  styleUrls: ['../barkeeper-shared.css', './barkeeper-recipe.component.css']
})
export class BarkeeperRecipeComponent implements OnInit {
  cocktail: Cocktail | null = null;
  ingredients: Ingredient[] = [];
  ingredientMap: Map<number, Ingredient> = new Map();
  isLoading = false;

  constructor(
    private apiService: ApiService,
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

  goBack(): void {
    this.location.back();
  }
}
