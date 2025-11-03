import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Cocktail, CocktailIngredient, Ingredient } from '../../models/models';
import { ApiService } from '../../services/api.service';

@Component({
  selector: 'app-cocktails',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './cocktails.component.html',
  styleUrls: ['./cocktails.component.css']
})
export class CocktailsComponent implements OnInit {
  cocktails: Cocktail[] = [];
  availableCocktails: Cocktail[] = [];
  ingredients: Ingredient[] = [];
  showOnlyAvailable = false;
  
  newCocktail: Cocktail = {
    name: '',
    ingredients: [],
    steps: [],
    notes: ''
  };
  
  newIngredientEntry: CocktailIngredient = {
    ingredientId: 0,
    measure: ''
  };
  
  newStep = '';

  constructor(private apiService: ApiService) {}

  ngOnInit(): void {
    this.loadCocktails();
    this.loadIngredients();
    this.loadAvailableCocktails();
  }

  loadCocktails(): void {
    this.apiService.getAllCocktails().subscribe(
      (data) => {
        this.cocktails = data;
      },
      (error) => {
        console.error('Error loading cocktails:', error);
      }
    );
  }

  loadIngredients(): void {
    this.apiService.getAllIngredients().subscribe(
      (data) => {
        this.ingredients = data;
      },
      (error) => {
        console.error('Error loading ingredients:', error);
      }
    );
  }

  loadAvailableCocktails(): void {
    this.apiService.getAvailableCocktails().subscribe(
      (data) => {
        this.availableCocktails = data;
      },
      (error) => {
        console.error('Error loading available cocktails:', error);
      }
    );
  }

  get displayedCocktails(): Cocktail[] {
    return this.showOnlyAvailable ? this.availableCocktails : this.cocktails;
  }

  addIngredientToCocktail(): void {
    if (this.newIngredientEntry.ingredientId > 0 && this.newIngredientEntry.measure) {
      this.newCocktail.ingredients.push({ ...this.newIngredientEntry });
      this.newIngredientEntry = { ingredientId: 0, measure: '' };
    }
  }

  removeIngredient(index: number): void {
    this.newCocktail.ingredients.splice(index, 1);
  }

  addStep(): void {
    if (this.newStep.trim()) {
      this.newCocktail.steps.push(this.newStep);
      this.newStep = '';
    }
  }

  removeStep(index: number): void {
    this.newCocktail.steps.splice(index, 1);
  }

  createCocktail(): void {
    if (this.newCocktail.name && this.newCocktail.ingredients.length > 0) {
      this.apiService.createCocktail(this.newCocktail).subscribe(
        () => {
          this.loadCocktails();
          this.loadAvailableCocktails();
          this.resetNewCocktail();
        },
        (error) => {
          console.error('Error creating cocktail:', error);
        }
      );
    }
  }

  deleteCocktail(id: number | undefined): void {
    if (id && confirm('Are you sure you want to delete this cocktail?')) {
      this.apiService.deleteCocktail(id).subscribe(
        () => {
          this.loadCocktails();
          this.loadAvailableCocktails();
        },
        (error) => {
          console.error('Error deleting cocktail:', error);
        }
      );
    }
  }

  getIngredientName(ingredientId: number): string {
    const ingredient = this.ingredients.find(i => i.id === ingredientId);
    return ingredient ? ingredient.name : 'Unknown';
  }

  resetNewCocktail(): void {
    this.newCocktail = {
      name: '',
      ingredients: [],
      steps: [],
      notes: ''
    };
  }

  toggleAvailableOnly(): void {
    this.showOnlyAvailable = !this.showOnlyAvailable;
  }
}
