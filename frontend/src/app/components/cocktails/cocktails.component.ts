import { Component, OnInit, OnDestroy } from '@angular/core';

import { FormsModule } from '@angular/forms';
import { CdkDragDrop, DragDropModule, moveItemInArray } from '@angular/cdk/drag-drop';
import { Cocktail, CocktailIngredient, Ingredient, IngredientType, MeasureUnit } from '../../models/models';
import { ApiService } from '../../services/api.service';
import { ExportService, ExportFormat, ExportType } from '../../services/export.service';
import { MeasureService } from '../../services/measure.service';
import { FuzzySearchService } from '../../services/fuzzy-search.service';
import { ModalComponent } from '../util/modal.component';
import { TranslatePipe } from '../../pipes/translate.pipe';
import { TranslateService } from '../../services/translate.service';
import { Subscription } from 'rxjs';

@Component({
    selector: 'app-cocktails',
    imports: [FormsModule, ModalComponent, TranslatePipe, DragDropModule],
    templateUrl: './cocktails.component.html',
    styleUrls: ['../admin-shared.css', './cocktails.component.css']
})
export class CocktailsComponent implements OnInit, OnDestroy {
  cocktails: Cocktail[] = [];
  availableCocktails: Cocktail[] = [];
  ingredients: Ingredient[] = [];
  showOnlyAvailable = false;
  isModalOpen = false;
  isEditMode = false;
  editingCocktailId?: number;
  
  // Measurement unit
  currentUnit: MeasureUnit = MeasureUnit.ML;
  private unitSubscription?: Subscription;
  
  // Filter properties
  nameFilter = '';
  spiritFilter = '';
  tagFilter = '';
  
  newCocktail: Cocktail = {
    name: '',
    ingredients: [],
    steps: [],
    notes: '',
    tags: [],
    abv: 0,
    baseSpirit: 'none',
    glasswareType: undefined,
    iceType: undefined
  };
  
  // For ingredient entry - store value in display unit, convert to ml on add
  newIngredientEntry: { ingredientId: number; measureValue: number } = {
    ingredientId: 0,
    measureValue: 0
  };
  
  // Track if current ingredient is count-based (GARNISH/OTHER)
  isCountBasedIngredient = false;
  
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
  
  // Predefined glassware types
  readonly glasswareTypes = [
    'Coupe',
    'Highball',
    'Rocks / Old Fashioned',
    'Martini / Cocktail',
    'Collins',
    'Hurricane',
    'Margarita',
    'Wine',
    'Champagne Flute',
    'Shot',
    'Mug',
    'Tiki',
    'Nick & Nora',
    'Snifter'
  ];
  
  // Predefined ice types
  readonly iceTypes = [
    'None',
    'Cubed',
    'Crushed',
    'Large Cube',
    'Ice Sphere',
    'Cracked',
    'Shaved'
  ];

  constructor(
    private apiService: ApiService, 
    private exportService: ExportService,
    private measureService: MeasureService,
    private translateService: TranslateService,
    private fuzzySearchService: FuzzySearchService
  ) {}

  ngOnInit(): void {
    this.loadCocktails();
    this.loadIngredients();
    this.loadAvailableCocktails();
    
    // Subscribe to unit changes
    this.unitSubscription = this.measureService.getUnit().subscribe(unit => {
      this.currentUnit = unit;
    });
  }
  
  ngOnDestroy(): void {
    this.unitSubscription?.unsubscribe();
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
    
    let filtered = this.cocktails;
    
    // Apply name filter with fuzzy search (returns scored and sorted results)
    if (this.nameFilter) {
      const results = this.fuzzySearchService.search(
        this.nameFilter,
        filtered,
        cocktail => cocktail.name
      );
      filtered = results.map(r => r.item);
    }
    
    // Apply spirit filter with fuzzy matching
    if (this.spiritFilter) {
      filtered = filtered.filter(cocktail => {
        return cocktail.ingredients.some(ing => {
          const ingredient = this.ingredients.find(i => i.id === ing.ingredientId);
          return ingredient && this.fuzzySearchService.matches(this.spiritFilter, ingredient.name);
        });
      });
    }
    
    // Apply tag filter with fuzzy matching
    if (this.tagFilter) {
      filtered = filtered.filter(cocktail => 
        cocktail.tags.some(tag => 
          this.fuzzySearchService.matches(this.tagFilter, tag)
        )
      );
    }
    
    return filtered;
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
      tags: [...cocktail.tags],
      abv: cocktail.abv,
      baseSpirit: cocktail.baseSpirit,
      glasswareType: cocktail.glasswareType,
      iceType: cocktail.iceType
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
    // Check if search filter matches an ingredient name exactly (case-insensitive)
    const searchText = this.ingredientSearchFilter.trim().toLowerCase();
    if (searchText && this.newIngredientEntry.ingredientId === 0) {
      const matchingIngredient = this.ingredients.find(
        ing => ing.name.toLowerCase() === searchText
      );
      if (matchingIngredient && matchingIngredient.id) {
        this.newIngredientEntry.ingredientId = matchingIngredient.id;
      }
    }
    
    if (this.newIngredientEntry.ingredientId > 0 && this.newIngredientEntry.measureValue > 0) {
      let measureMl: number;
      
      // For count-based ingredients (GARNISH/OTHER), store as negative value
      if (this.isCountBasedIngredient) {
        measureMl = -Math.abs(this.newIngredientEntry.measureValue);
      } else {
        // Convert from current display unit to ml for volume-based storage
        measureMl = this.measureService.convertToMl(this.newIngredientEntry.measureValue, this.currentUnit);
      }
      
      this.newCocktail.ingredients.push({ 
        ingredientId: this.newIngredientEntry.ingredientId, 
        measureMl: measureMl 
      });
      this.newIngredientEntry = { ingredientId: 0, measureValue: 0 };
      this.isCountBasedIngredient = false;
      // Clear search filter after adding ingredient
      this.ingredientSearchFilter = '';
    }
  }
  
  onIngredientSelected(): void {
    // Check if selected ingredient is count-based (GARNISH or OTHER type)
    const selectedIngredient = this.ingredients.find(
      ing => ing.id === this.newIngredientEntry.ingredientId
    );
    this.isCountBasedIngredient = selectedIngredient 
      ? (selectedIngredient.type === IngredientType.GARNISH || selectedIngredient.type === IngredientType.OTHER)
      : false;
  }

  removeIngredient(index: number): void {
    this.newCocktail.ingredients.splice(index, 1);
  }

  dropIngredient(event: CdkDragDrop<CocktailIngredient[]>): void {
    moveItemInArray(this.newCocktail.ingredients, event.previousIndex, event.currentIndex);
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

  dropStep(event: CdkDragDrop<string[]>): void {
    moveItemInArray(this.newCocktail.steps, event.previousIndex, event.currentIndex);
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
  
  formatMeasure(measureMl: number): string {
    return this.measureService.formatMeasure(measureMl, this.currentUnit);
  }
  
  get availableUnits(): MeasureUnit[] {
    return this.measureService.getAvailableUnits();
  }
  
  setUnit(unit: MeasureUnit): void {
    this.measureService.setUnit(unit);
  }

  resetNewCocktail(): void {
    this.newCocktail = {
      name: '',
      ingredients: [],
      steps: [],
      notes: '',
      tags: [],
      abv: 0,
      baseSpirit: 'none',
      glasswareType: undefined,
      iceType: undefined
    };
    this.newIngredientEntry = { ingredientId: 0, measureValue: 0 };
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
    // Use fuzzy search and extract just the items
    const results = this.fuzzySearchService.search(
      this.ingredientSearchFilter,
      this.ingredients,
      ingredient => ingredient.name
    );
    return results.map(r => r.item);
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
