import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Cocktail, CocktailIngredient, Ingredient } from '../models/models';
import { ApiService } from '../services/api.service';
import { ModalComponent } from './modal.component';

@Component({
  selector: 'app-cocktails',
  standalone: true,
  imports: [CommonModule, FormsModule, ModalComponent],
  templateUrl: './cocktails.component.html',
  styleUrls: ['./cocktails.component.css']
})
export class CocktailsComponent implements OnInit {
  cocktails: Cocktail[] = [];
  availableCocktails: Cocktail[] = [];
  ingredients: Ingredient[] = [];
  showOnlyAvailable = false;
  isModalOpen = false;
  
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
    this.apiService.getAllCocktails().subscribe({
      next: (data: Cocktail[]) => {
        this.cocktails = data;
      },
      error: (error: any) => {
        console.error('Error loading cocktails:', error);
      }
    });
  }

  loadIngredients(): void {
    this.apiService.getAllIngredients().subscribe({
      next: (data: Ingredient[]) => {
        this.ingredients = data;
      },
      error: (error: any) => {
        console.error('Error loading ingredients:', error);
      }
    });
  }

  loadAvailableCocktails(): void {
    this.apiService.getAvailableCocktails().subscribe({
      next: (data: Cocktail[]) => {
        this.availableCocktails = data;
      },
      error: (error: any) => {
        console.error('Error loading available cocktails:', error);
      }
    });
  }

  get displayedCocktails(): Cocktail[] {
    return this.showOnlyAvailable ? this.availableCocktails : this.cocktails;
  }

  openModal(): void {
    this.isModalOpen = true;
  }

  closeModal(): void {
    this.isModalOpen = false;
    this.resetNewCocktail();
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
      this.apiService.createCocktail(this.newCocktail).subscribe({
        next: () => {
          this.loadCocktails();
          this.loadAvailableCocktails();
          this.closeModal();
        },
        error: (error: any) => {
          console.error('Error creating cocktail:', error);
        }
      });
    }
  }

  deleteCocktail(id: number | undefined): void {
    if (id && confirm('Are you sure you want to delete this cocktail?')) {
      this.apiService.deleteCocktail(id).subscribe({
        next: () => {
          this.loadCocktails();
          this.loadAvailableCocktails();
        },
        error: (error: any) => {
          console.error('Error deleting cocktail:', error);
        }
      });
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
