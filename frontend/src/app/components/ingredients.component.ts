import { Component, OnInit } from '@angular/core';

import { FormsModule } from '@angular/forms';
import { Ingredient, IngredientType } from '../models/models';
import { ApiService } from '../services/api.service';
import { ModalComponent } from './modal.component';

@Component({
    selector: 'app-ingredients',
    imports: [FormsModule, ModalComponent],
    templateUrl: './ingredients.component.html',
    styleUrls: ['./ingredients.component.css']
})
export class IngredientsComponent implements OnInit {
  ingredients: Ingredient[] = [];
  newIngredient: Ingredient = {
    name: '',
    type: IngredientType.SPIRIT,
    abv: 0,
    inStock: false
  };
  ingredientTypes = Object.values(IngredientType);
  editingIngredient: Ingredient | null = null;
  isModalOpen = false;
  
  // Filter properties
  nameFilter = '';
  typeFilter = '';
  
  // Sort properties
  sortBy: 'name' | 'type' | 'abv' = 'name';
  sortDirection: 'asc' | 'desc' = 'asc';

  constructor(private apiService: ApiService) {}

  ngOnInit(): void {
    this.loadIngredients();
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
  
  get displayedIngredients(): Ingredient[] {
    let filtered = this.ingredients;
    
    // Apply filters
    if (this.nameFilter) {
      filtered = filtered.filter(ingredient => 
        ingredient.name.toLowerCase().includes(this.nameFilter.toLowerCase())
      );
    }
    
    if (this.typeFilter) {
      filtered = filtered.filter(ingredient => 
        ingredient.type === this.typeFilter
      );
    }
    
    // Apply sorting
    return filtered.sort((a, b) => {
      let comparison = 0;
      
      switch (this.sortBy) {
        case 'name':
          comparison = a.name.localeCompare(b.name);
          break;
        case 'type':
          comparison = a.type.localeCompare(b.type);
          break;
        case 'abv':
          comparison = a.abv - b.abv;
          break;
      }
      
      return this.sortDirection === 'asc' ? comparison : -comparison;
    });
  }
  
  setSortBy(field: 'name' | 'type' | 'abv'): void {
    if (this.sortBy === field) {
      // Toggle direction if clicking the same field
      this.sortDirection = this.sortDirection === 'asc' ? 'desc' : 'asc';
    } else {
      // Set new field and default to ascending
      this.sortBy = field;
      this.sortDirection = 'asc';
    }
  }
  
  clearFilters(): void {
    this.nameFilter = '';
    this.typeFilter = '';
  }

  openModal(): void {
    this.isModalOpen = true;
  }

  closeModal(): void {
    this.isModalOpen = false;
    this.resetNewIngredient();
  }

  createIngredient(): void {
    this.apiService.createIngredient(this.newIngredient).subscribe({
      next: () => {
        this.loadIngredients();
        this.closeModal();
      },
      error: (error: any) => {
        console.error('Error creating ingredient:', error);
      }
    });
  }

  updateIngredient(ingredient: Ingredient): void {
    if (ingredient.id) {
      this.apiService.updateIngredient(ingredient.id, ingredient).subscribe({
        next: () => {
          this.loadIngredients();
          this.editingIngredient = null;
        },
        error: (error: any) => {
          console.error('Error updating ingredient:', error);
        }
      });
    }
  }

  deleteIngredient(id: number | undefined): void {
    if (id && confirm('Are you sure you want to delete this ingredient?')) {
      this.apiService.deleteIngredient(id).subscribe({
        next: () => {
          this.loadIngredients();
        },
        error: (error: any) => {
          console.error('Error deleting ingredient:', error);
        }
      });
    }
  }

  toggleStock(ingredient: Ingredient): void {
    ingredient.inStock = !ingredient.inStock;
    this.updateIngredient(ingredient);
  }

  startEdit(ingredient: Ingredient): void {
    this.editingIngredient = { ...ingredient };
  }

  cancelEdit(): void {
    this.editingIngredient = null;
  }

  saveEdit(): void {
    if (this.editingIngredient) {
      this.updateIngredient(this.editingIngredient);
    }
  }

  resetNewIngredient(): void {
    this.newIngredient = {
      name: '',
      type: IngredientType.SPIRIT,
      abv: 0,
      inStock: false
    };
  }
}
