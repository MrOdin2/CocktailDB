import { Component, OnInit } from '@angular/core';

import { FormsModule } from '@angular/forms';
import { Ingredient, IngredientType } from '../../models/models';
import { ApiService } from '../../services/api.service';
import { ModalComponent } from '../util/modal.component';
import { TranslatePipe } from '../../pipes/translate.pipe';

@Component({
    selector: 'app-ingredients',
    imports: [FormsModule, ModalComponent, TranslatePipe],
    templateUrl: './ingredients.component.html',
    styleUrls: ['../admin-shared.css', './ingredients.component.css']
})
export class IngredientsComponent implements OnInit {
  ingredients: Ingredient[] = [];
  newIngredient: Ingredient = {
    name: '',
    type: IngredientType.SPIRIT,
    abv: 0,
    inStock: false,
    substituteIds: [],
    alternativeIds: []
  };
  ingredientTypes = Object.values(IngredientType);
  editingIngredient: Ingredient | null = null;
  isModalOpen = false;
  isSubstitutesModalOpen = false;
  selectedIngredientForSubstitutes: Ingredient | null = null;
  
  // Filter properties
  nameFilter = '';
  typeFilter = '';
  
  // Sort properties
  sortBy: 'name' | 'type' | 'abv' = 'name';
  sortDirection: 'asc' | 'desc' = 'asc';
  
  // Substitutes/Alternatives search
  substituteSearch = '';
  alternativeSearch = '';

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
    this.editingIngredient = { 
      ...ingredient,
      substituteIds: [...(ingredient.substituteIds || [])],
      alternativeIds: [...(ingredient.alternativeIds || [])]
    };
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
      inStock: false,
      substituteIds: [],
      alternativeIds: []
    };
  }
  
  // Substitutes/Alternatives Modal Methods
  openSubstitutesModal(ingredient: Ingredient): void {
    this.selectedIngredientForSubstitutes = { 
      ...ingredient,
      substituteIds: [...(ingredient.substituteIds || [])],
      alternativeIds: [...(ingredient.alternativeIds || [])]
    };
    this.isSubstitutesModalOpen = true;
    this.substituteSearch = '';
    this.alternativeSearch = '';
  }
  
  closeSubstitutesModal(): void {
    this.isSubstitutesModalOpen = false;
    this.selectedIngredientForSubstitutes = null;
    this.substituteSearch = '';
    this.alternativeSearch = '';
  }
  
  saveSubstitutes(): void {
    if (this.selectedIngredientForSubstitutes) {
      this.updateIngredient(this.selectedIngredientForSubstitutes);
      this.closeSubstitutesModal();
    }
  }
  
  getIngredientName(id: number): string {
    const ingredient = this.ingredients.find(i => i.id === id);
    return ingredient ? ingredient.name : 'Unknown';
  }
  
  getAvailableSubstitutes(): Ingredient[] {
    if (!this.selectedIngredientForSubstitutes) return [];
    
    const currentId = this.selectedIngredientForSubstitutes.id;
    const existingSubIds = this.selectedIngredientForSubstitutes.substituteIds || [];
    
    return this.ingredients.filter(i => 
      i.id !== currentId && 
      !existingSubIds.includes(i.id!) &&
      i.name.toLowerCase().includes(this.substituteSearch.toLowerCase())
    );
  }
  
  getAvailableAlternatives(): Ingredient[] {
    if (!this.selectedIngredientForSubstitutes) return [];
    
    const currentId = this.selectedIngredientForSubstitutes.id;
    const existingAltIds = this.selectedIngredientForSubstitutes.alternativeIds || [];
    
    return this.ingredients.filter(i => 
      i.id !== currentId && 
      !existingAltIds.includes(i.id!) &&
      i.name.toLowerCase().includes(this.alternativeSearch.toLowerCase())
    );
  }
  
  addSubstitute(ingredientId: number): void {
    if (this.selectedIngredientForSubstitutes) {
      if (!this.selectedIngredientForSubstitutes.substituteIds) {
        this.selectedIngredientForSubstitutes.substituteIds = [];
      }
      this.selectedIngredientForSubstitutes.substituteIds.push(ingredientId);
    }
  }
  
  removeSubstitute(ingredientId: number): void {
    if (this.selectedIngredientForSubstitutes) {
      this.selectedIngredientForSubstitutes.substituteIds = 
        (this.selectedIngredientForSubstitutes.substituteIds || []).filter(id => id !== ingredientId);
    }
  }
  
  addAlternative(ingredientId: number): void {
    if (this.selectedIngredientForSubstitutes) {
      if (!this.selectedIngredientForSubstitutes.alternativeIds) {
        this.selectedIngredientForSubstitutes.alternativeIds = [];
      }
      this.selectedIngredientForSubstitutes.alternativeIds.push(ingredientId);
    }
  }
  
  removeAlternative(ingredientId: number): void {
    if (this.selectedIngredientForSubstitutes) {
      this.selectedIngredientForSubstitutes.alternativeIds = 
        (this.selectedIngredientForSubstitutes.alternativeIds || []).filter(id => id !== ingredientId);
    }
  }
  
  getSubstitutesCount(ingredient: Ingredient): number {
    return (ingredient.substituteIds?.length || 0) + (ingredient.alternativeIds?.length || 0);
  }
}
