import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Cocktail, CocktailIngredient, Ingredient, IngredientType } from '../models/models';
import { ApiService } from '../services/api.service';
import { ExportService, ExportFormat, ExportType } from '../services/export.service';
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
  isEditMode = false;
  editingCocktailId?: number;
  
  // Filter properties
  nameFilter = '';
  spiritFilter = '';
  tagFilter = '';
  
  newCocktail: Cocktail = {
    name: '',
    ingredients: [],
    steps: [],
    notes: '',
    tags: []
  };
  
  newIngredientEntry: CocktailIngredient = {
    ingredientId: 0,
    measure: ''
  };
  
  newStep = '';
  newTag = '';
  customTag = '';
  
  // Ingredient search filter
  ingredientSearchFilter = '';
  
  // Nested ingredient creation modal
  isIngredientModalOpen = false;
  newIngredientForCocktail: Ingredient = {
    name: '',
    type: IngredientType.SPIRIT,
    abv: 0,
    inStock: false
  };
  
  // Export modal
  isExportModalOpen = false;
  exportType: ExportType = ExportType.MENU;
  exportFormat: ExportFormat = ExportFormat.HTML;
  exportGroupBy: 'spirit' | 'tags' = 'spirit';
  
  // Tag selection modal for export
  isTagSelectionModalOpen = false;
  availableTagsForExport: string[] = [];
  selectedTagsForExport: string[] = [];

  constructor(private apiService: ApiService, private exportService: ExportService) {}

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
    if (this.showOnlyAvailable) {
      return this.availableCocktails;
    }
    
    // Apply filters
    if (!this.nameFilter && !this.spiritFilter && !this.tagFilter) {
      return this.cocktails;
    }
    
    return this.cocktails.filter(cocktail => {
      let matches = true;
      
      // Name filter
      if (this.nameFilter) {
        matches = matches && cocktail.name.toLowerCase().includes(this.nameFilter.toLowerCase());
      }
      
      // Spirit filter
      if (this.spiritFilter) {
        const hasSpirit = cocktail.ingredients.some(ing => {
          const ingredient = this.ingredients.find(i => i.id === ing.ingredientId);
          return ingredient && ingredient.name.toLowerCase() === this.spiritFilter.toLowerCase();
        });
        matches = matches && hasSpirit;
      }
      
      // Tag filter
      if (this.tagFilter) {
        matches = matches && cocktail.tags.some(tag => 
          tag.toLowerCase().includes(this.tagFilter.toLowerCase())
        );
      }
      
      return matches;
    });
  }

  openModal(): void {
    this.isEditMode = false;
    this.editingCocktailId = undefined;
    this.resetNewCocktail();
    this.isModalOpen = true;
  }

  openEditModal(cocktail: Cocktail): void {
    this.isEditMode = true;
    this.editingCocktailId = cocktail.id;
    this.newCocktail = {
      name: cocktail.name,
      ingredients: [...cocktail.ingredients],
      steps: [...cocktail.steps],
      notes: cocktail.notes || '',
      tags: [...cocktail.tags]
    };
    this.isModalOpen = true;
  }

  closeModal(): void {
    this.isModalOpen = false;
    this.isEditMode = false;
    this.editingCocktailId = undefined;
    this.resetNewCocktail();
  }

  addIngredientToCocktail(): void {
    if (this.newIngredientEntry.ingredientId > 0 && this.newIngredientEntry.measure) {
      this.newCocktail.ingredients.push({ ...this.newIngredientEntry });
      this.newIngredientEntry = { ingredientId: 0, measure: '' };
      // Clear search filter after adding ingredient
      this.ingredientSearchFilter = '';
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

  addTag(): void {
    const tagToAdd = this.customTag.trim() || this.newTag.trim();
    if (tagToAdd && !this.newCocktail.tags.includes(tagToAdd)) {
      this.newCocktail.tags.push(tagToAdd);
      this.newTag = '';
      this.customTag = '';
    }
  }

  removeTag(index: number): void {
    this.newCocktail.tags.splice(index, 1);
  }

  createCocktail(): void {
    if (this.newCocktail.name && this.newCocktail.ingredients.length > 0) {
      if (this.isEditMode && this.editingCocktailId) {
        // Update existing cocktail
        this.apiService.updateCocktail(this.editingCocktailId, this.newCocktail).subscribe({
          next: () => {
            this.loadCocktails();
            this.loadAvailableCocktails();
            this.closeModal();
          },
          error: (error: any) => {
            console.error('Error updating cocktail:', error);
          }
        });
      } else {
        // Create new cocktail
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
      notes: '',
      tags: []
    };
  }

  toggleAvailableOnly(): void {
    this.showOnlyAvailable = !this.showOnlyAvailable;
  }

  clearFilters(): void {
    this.nameFilter = '';
    this.spiritFilter = '';
    this.tagFilter = '';
    this.showOnlyAvailable = false;
  }

  get uniqueSpirits(): string[] {
    const spirits = new Set<string>();
    this.ingredients
      .filter(ing => ing.type === 'SPIRIT')
      .forEach(ing => spirits.add(ing.name));
    return Array.from(spirits).sort();
  }

  get allTags(): string[] {
    const tags = new Set<string>();
    this.cocktails.forEach(cocktail => {
      cocktail.tags.forEach(tag => tags.add(tag));
    });
    return Array.from(tags).sort();
  }
  
  get filteredIngredients(): Ingredient[] {
    if (!this.ingredientSearchFilter) {
      return this.ingredients;
    }
    return this.ingredients.filter(ingredient =>
      ingredient.name.toLowerCase().includes(this.ingredientSearchFilter.toLowerCase())
    );
  }
  
  get ingredientTypes(): string[] {
    return Object.values(IngredientType);
  }
  
  openIngredientModal(): void {
    this.isIngredientModalOpen = true;
  }
  
  closeIngredientModal(): void {
    this.isIngredientModalOpen = false;
    this.resetNewIngredientForCocktail();
  }
  
  createIngredientFromCocktail(): void {
    this.apiService.createIngredient(this.newIngredientForCocktail).subscribe({
      next: (createdIngredient: Ingredient) => {
        this.loadIngredients();
        // Auto-select the newly created ingredient
        if (createdIngredient && createdIngredient.id) {
          this.newIngredientEntry.ingredientId = createdIngredient.id;
          // Clear search filter to show the selected ingredient
          this.ingredientSearchFilter = '';
        }
        this.closeIngredientModal();
      },
      error: (error: any) => {
        console.error('Error creating ingredient:', error);
      }
    });
  }
  
  resetNewIngredientForCocktail(): void {
    this.newIngredientForCocktail = {
      name: '',
      type: IngredientType.SPIRIT,
      abv: 0,
      inStock: false
    };
  }
  
  openExportModal(): void {
    this.isExportModalOpen = true;
  }
  
  closeExportModal(): void {
    this.isExportModalOpen = false;
    this.selectedTagsForExport = [];
  }
  
  proceedWithExport(): void {
    // If grouping by tags and it's a menu export, show tag selection modal
    if (this.exportType === ExportType.MENU && this.exportGroupBy === 'tags') {
      this.openTagSelectionModal();
    } else {
      this.performExport();
    }
  }
  
  openTagSelectionModal(): void {
    // Get all tags from displayed cocktails
    const tagsInDisplayedCocktails = new Set<string>();
    this.displayedCocktails.forEach(cocktail => {
      if (cocktail.tags && cocktail.tags.length > 0) {
        cocktail.tags.forEach(tag => tagsInDisplayedCocktails.add(tag));
      }
    });
    
    this.availableTagsForExport = Array.from(tagsInDisplayedCocktails).sort();
    this.selectedTagsForExport = [...this.availableTagsForExport]; // Select all by default
    this.isTagSelectionModalOpen = true;
  }
  
  closeTagSelectionModal(): void {
    this.isTagSelectionModalOpen = false;
  }
  
  toggleTagSelection(tag: string): void {
    const index = this.selectedTagsForExport.indexOf(tag);
    if (index > -1) {
      this.selectedTagsForExport.splice(index, 1);
    } else {
      // Add tag in alphabetical order
      this.selectedTagsForExport.push(tag);
      this.selectedTagsForExport.sort();
    }
  }
  
  moveTagUp(index: number): void {
    if (index > 0) {
      const temp = this.selectedTagsForExport[index];
      this.selectedTagsForExport[index] = this.selectedTagsForExport[index - 1];
      this.selectedTagsForExport[index - 1] = temp;
    }
  }
  
  moveTagDown(index: number): void {
    if (index < this.selectedTagsForExport.length - 1) {
      const temp = this.selectedTagsForExport[index];
      this.selectedTagsForExport[index] = this.selectedTagsForExport[index + 1];
      this.selectedTagsForExport[index + 1] = temp;
    }
  }
  
  isTagSelected(tag: string): boolean {
    return this.selectedTagsForExport.includes(tag);
  }
  
  confirmTagSelection(): void {
    this.closeTagSelectionModal();
    this.performExport();
  }
  
  performExport(): void {
    const cocktailsToExport = this.displayedCocktails;
    
    if (cocktailsToExport.length === 0) {
      console.warn('No cocktails to export. Adjust filters to include cocktails.');
      return;
    }
    
    this.exportService.exportCocktails(
      cocktailsToExport,
      this.ingredients,
      this.exportType,
      this.exportFormat,
      this.exportGroupBy,
      this.selectedTagsForExport.length > 0 ? this.selectedTagsForExport : undefined
    );
    
    this.closeExportModal();
  }
  
  get ExportType() {
    return ExportType;
  }
  
  get ExportFormat() {
    return ExportFormat;
  }
}
