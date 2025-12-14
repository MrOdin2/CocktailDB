import { Component, OnInit } from '@angular/core';

import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Ingredient, IngredientType } from '../../models/models';
import { ApiService } from '../../services/api.service';
import { FuzzySearchService } from '../../services/fuzzy-search.service';
import { ModalComponent } from '../util/modal.component';
import { TranslatePipe } from '../../pipes/translate.pipe';

@Component({
    selector: 'app-ingredients',
    imports: [CommonModule, FormsModule, ModalComponent, TranslatePipe],
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
  
  // Filter properties
  nameFilter = '';
  typeFilter = '';
  
  // Sort properties
  sortBy: 'name' | 'type' | 'abv' = 'name';
  sortDirection: 'asc' | 'desc' = 'asc';

  // Search properties for dropdowns
  substituteSearch = '';
  alternativeSearch = '';
  substituteSearchEdit = '';
  alternativeSearchEdit = '';

  constructor(
    private apiService: ApiService,
    private fuzzySearchService: FuzzySearchService
  ) {}

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
    
    // Apply name filter with fuzzy search
    if (this.nameFilter) {
      const results = this.fuzzySearchService.search(
        this.nameFilter,
        filtered,
        ingredient => ingredient.name
      );
      filtered = results.map(r => r.item);
    }
    
    // Apply type filter
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
      substituteIds: ingredient.substituteIds ? [...ingredient.substituteIds] : [],
      alternativeIds: ingredient.alternativeIds ? [...ingredient.alternativeIds] : []
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

  getIngredientName(id: number): string {
    const ingredient = this.ingredients.find(i => i.id === id);
    return ingredient ? ingredient.name : 'Unknown';
  }

  getAvailableIngredientsForSubstitutes(currentId?: number): Ingredient[] {
    return this.ingredients.filter(i => i.id !== currentId);
  }

  toggleSubstitute(ingredientId: number): void {
    if (this.editingIngredient) {
      if (!this.editingIngredient.substituteIds) {
        this.editingIngredient.substituteIds = [];
      }
      const index = this.editingIngredient.substituteIds.indexOf(ingredientId);
      if (index > -1) {
        this.editingIngredient.substituteIds.splice(index, 1);
      } else {
        this.editingIngredient.substituteIds.push(ingredientId);
      }
    } else if (this.isModalOpen) {
      if (!this.newIngredient.substituteIds) {
        this.newIngredient.substituteIds = [];
      }
      const index = this.newIngredient.substituteIds.indexOf(ingredientId);
      if (index > -1) {
        this.newIngredient.substituteIds.splice(index, 1);
      } else {
        this.newIngredient.substituteIds.push(ingredientId);
      }
    }
  }

  toggleAlternative(ingredientId: number): void {
    if (this.editingIngredient) {
      if (!this.editingIngredient.alternativeIds) {
        this.editingIngredient.alternativeIds = [];
      }
      const index = this.editingIngredient.alternativeIds.indexOf(ingredientId);
      if (index > -1) {
        this.editingIngredient.alternativeIds.splice(index, 1);
      } else {
        this.editingIngredient.alternativeIds.push(ingredientId);
      }
    } else if (this.isModalOpen) {
      if (!this.newIngredient.alternativeIds) {
        this.newIngredient.alternativeIds = [];
      }
      const index = this.newIngredient.alternativeIds.indexOf(ingredientId);
      if (index > -1) {
        this.newIngredient.alternativeIds.splice(index, 1);
      } else {
        this.newIngredient.alternativeIds.push(ingredientId);
      }
    }
  }

  isSubstituteSelected(ingredientId: number): boolean {
    if (this.editingIngredient) {
      return this.editingIngredient.substituteIds?.includes(ingredientId) || false;
    }
    return this.newIngredient.substituteIds?.includes(ingredientId) || false;
  }

  isAlternativeSelected(ingredientId: number): boolean {
    if (this.editingIngredient) {
      return this.editingIngredient.alternativeIds?.includes(ingredientId) || false;
    }
    return this.newIngredient.alternativeIds?.includes(ingredientId) || false;
  }

  getFilteredSubstitutes(isEdit: boolean = false): Ingredient[] {
    const currentId = isEdit ? this.editingIngredient?.id : undefined;
    const searchTerm = isEdit ? this.substituteSearchEdit : this.substituteSearch;
    const available = this.getAvailableIngredientsForSubstitutes(currentId);
    
    if (!searchTerm.trim()) {
      return available;
    }
    
    // Use fuzzy search
    const results = this.fuzzySearchService.search(
      searchTerm,
      available,
      ing => ing.name
    );
    return results.map(r => r.item);
  }

  getFilteredAlternatives(isEdit: boolean = false): Ingredient[] {
    const currentId = isEdit ? this.editingIngredient?.id : undefined;
    const searchTerm = isEdit ? this.alternativeSearchEdit : this.alternativeSearch;
    const available = this.getAvailableIngredientsForSubstitutes(currentId);
    
    if (!searchTerm.trim()) {
      return available;
    }
    
    // Use fuzzy search
    const results = this.fuzzySearchService.search(
      searchTerm,
      available,
      ing => ing.name
    );
    return results.map(r => r.item);
  }

  removeSubstitute(ingredientId: number, isEdit: boolean = false): void {
    if (isEdit && this.editingIngredient) {
      this.editingIngredient.substituteIds = this.editingIngredient.substituteIds?.filter(id => id !== ingredientId) || [];
    } else {
      this.newIngredient.substituteIds = this.newIngredient.substituteIds?.filter(id => id !== ingredientId) || [];
    }
  }

  removeAlternative(ingredientId: number, isEdit: boolean = false): void {
    if (isEdit && this.editingIngredient) {
      this.editingIngredient.alternativeIds = this.editingIngredient.alternativeIds?.filter(id => id !== ingredientId) || [];
    } else {
      this.newIngredient.alternativeIds = this.newIngredient.alternativeIds?.filter(id => id !== ingredientId) || [];
    }
  }
}
