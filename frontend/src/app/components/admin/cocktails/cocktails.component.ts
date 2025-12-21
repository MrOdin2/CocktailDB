import { Component, OnInit, OnDestroy } from '@angular/core';

import { FormsModule } from '@angular/forms';
import { Cocktail, CocktailIngredient, Ingredient, IngredientType, MeasureUnit } from '../../../models/models';
import { ApiService, CsvImportResult } from '../../../services/api.service';
import { ExportService, ExportFormat, ExportType } from '../../../services/export.service';
import { MeasureService } from '../../../services/measure.service';
import { FuzzySearchService } from '../../../services/fuzzy-search.service';
import { ModalComponent } from '../../util/modal.component';
import { CocktailCardComponent } from './cocktail-card/cocktail-card.component';
import { CocktailFormModalComponent } from './cocktail-form-modal/cocktail-form-modal.component';
import { IngredientModalComponent } from './ingredient-modal/ingredient-modal.component';
import { TranslatePipe } from '../../../pipes/translate.pipe';
import { TranslateService } from '../../../services/translate.service';
import { Subscription } from 'rxjs';

@Component({
    selector: 'app-cocktails',
    imports: [FormsModule, ModalComponent, TranslatePipe, CocktailCardComponent, CocktailFormModalComponent, IngredientModalComponent],
    templateUrl: './cocktails.component.html',
    styleUrls: ['../admin-shared.css', './cocktails.component.css']
})
export class CocktailsComponent implements OnInit, OnDestroy {
  cocktails: Cocktail[] = [];
  availableCocktails: Cocktail[] = [];
  ingredients: Ingredient[] = [];
  showOnlyAvailable = false;
  isCocktailModalOpen = false;
  isEditMode = false;
  editingCocktail?: Cocktail;
  
  // Measurement unit
  currentUnit: MeasureUnit = MeasureUnit.ML;
  availableUnits = Object.values(MeasureUnit);
  private unitSubscription?: Subscription;
  
  // Filter properties
  nameFilter = '';
  spiritFilter = '';
  tagFilter = '';
  
  // Nested ingredient creation modal
  isIngredientModalOpen = false;
  ingredientTypes = Object.values(IngredientType);
  
  // Export modal
  isExportModalOpen = false;
  exportType: ExportType = ExportType.MENU;
  exportFormat: ExportFormat = ExportFormat.HTML;
  exportGroupBy: 'spirit' | 'tags' = 'spirit';
  
  // Tag selection modal for export
  isTagSelectionModalOpen = false;
  availableTagsForExport: string[] = [];
  selectedTagsForExport: string[] = [];
  
  // CSV import modal
  isCsvImportModalOpen = false;
  csvImportResult: CsvImportResult | null = null;
  
  // Predefined suggestions for glassware types (defaults)
  readonly defaultGlasswareTypes = [
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
  
  // Predefined suggestions for ice types (defaults)
  readonly defaultIceTypes = [
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
    this.editingCocktail = undefined;
    this.isCocktailModalOpen = true;
  }

  openEditModal(cocktail: Cocktail): void {
    this.isEditMode = true;
    this.editingCocktail = cocktail;
    this.isCocktailModalOpen = true;
  }

  openDuplicateModal(cocktail: Cocktail): void {
    this.isEditMode = false;
    // Create a copy with "Copy of" prefix and without the id
    this.editingCocktail = {
      ...cocktail,
      id: undefined,
      name: `Copy of ${cocktail.name}`
    };
    this.isCocktailModalOpen = true;
  }

  closeCocktailModal(): void {
    this.isCocktailModalOpen = false;
    this.isEditMode = false;
    this.editingCocktail = undefined;
  }

  onCocktailSaved(cocktail: Cocktail): void {
    if (this.isEditMode && this.editingCocktail?.id) {
      // Update existing cocktail
      this.apiService.updateCocktail(this.editingCocktail.id, cocktail).subscribe({
        next: () => {
          this.loadCocktails();
          this.loadAvailableCocktails();
          this.closeCocktailModal();
        },
        error: (error: any) => {
          console.error('Error updating cocktail:', error);
        }
      });
    } else {
      // Create new cocktail
      this.apiService.createCocktail(cocktail).subscribe({
        next: () => {
          this.loadCocktails();
          this.loadAvailableCocktails();
          this.closeCocktailModal();
        },
        error: (error: any) => {
          console.error('Error creating cocktail:', error);
        }
      });
    }
  }

  setUnit(unit: MeasureUnit): void {
    this.measureService.setUnit(unit);
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
  
  get allGlasswareTypes(): string[] {
    const glassware = new Set<string>();
    // Add defaults first
    this.defaultGlasswareTypes.forEach(type => glassware.add(type));
    // Add existing types from cocktails
    this.cocktails.forEach(cocktail => {
      (cocktail.glasswareTypes || []).forEach(type => glassware.add(type));
    });
    return Array.from(glassware).sort();
  }
  
  get allIceTypes(): string[] {
    const ice = new Set<string>();
    // Add defaults first
    this.defaultIceTypes.forEach(type => ice.add(type));
    // Add existing types from cocktails
    this.cocktails.forEach(cocktail => {
      (cocktail.iceTypes || []).forEach(type => ice.add(type));
    });
    return Array.from(ice).sort();
  }
  
  openIngredientModal(): void {
    this.isIngredientModalOpen = true;
  }
  
  closeIngredientModal(): void {
    this.isIngredientModalOpen = false;
  }
  
  onIngredientCreated(ingredient: Ingredient): void {
    this.apiService.createIngredient(ingredient).subscribe({
      next: (createdIngredient: Ingredient) => {
        this.loadIngredients();
        this.closeIngredientModal();
      },
      error: (error: any) => {
        console.error('Error creating ingredient:', error);
      }
    });
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
  
  // CSV Import/Export methods
  exportCsv(): void {
    this.apiService.exportCocktailsCsv().subscribe({
      next: (blob: Blob) => {
        const url = window.URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = url;
        link.download = `cocktails-${new Date().toISOString().split('T')[0]}.csv`;
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
        window.URL.revokeObjectURL(url);
      },
      error: (error: any) => {
        console.error('Error exporting cocktails CSV:', error);
        alert('Failed to export cocktails. Please try again.');
      }
    });
  }

  onCsvFileSelected(event: any): void {
    const file = event.target.files[0];
    if (file) {
      this.apiService.importCocktailsCsv(file).subscribe({
        next: (result: CsvImportResult) => {
          this.csvImportResult = result;
          this.isCsvImportModalOpen = true;
          
          // Reload cocktails if any were imported
          if (result.imported.length > 0) {
            this.loadCocktails();
            this.loadAvailableCocktails();
          }
          
          // Reset file input
          event.target.value = '';
        },
        error: (error: any) => {
          console.error('Error importing cocktails CSV:', error);
          alert('Failed to import cocktails. Please check the file format and try again.');
          event.target.value = '';
        }
      });
    }
  }

  closeCsvImportModal(): void {
    this.isCsvImportModalOpen = false;
    this.csvImportResult = null;
  }
  
  get ExportType() {
    return ExportType;
  }
  
  get ExportFormat() {
    return ExportFormat;
  }
}
