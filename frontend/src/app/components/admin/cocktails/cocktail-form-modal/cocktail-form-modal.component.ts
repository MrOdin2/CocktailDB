import { Component, EventEmitter, Input, Output, OnInit, OnChanges, SimpleChanges } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CdkDragDrop, DragDropModule, moveItemInArray } from '@angular/cdk/drag-drop';
import { Cocktail, CocktailIngredient, Ingredient, IngredientType, MeasureUnit } from '../../../../models/models';
import { MeasureService } from '../../../../services/measure.service';
import { FuzzySearchService } from '../../../../services/fuzzy-search.service';
import { ModalComponent } from '../../../util/modal.component';
import { TranslatePipe } from '../../../../pipes/translate.pipe';

@Component({
  selector: 'app-cocktail-form-modal',
  standalone: true,
  imports: [FormsModule, ModalComponent, TranslatePipe, DragDropModule],
  templateUrl: './cocktail-form-modal.component.html',
  styleUrls: ['./cocktail-form-modal.component.css']
})
export class CocktailFormModalComponent implements OnInit, OnChanges {
  @Input() isOpen = false;
  @Input() isEditMode = false;
  @Input() cocktail?: Cocktail;
  @Input() ingredients: Ingredient[] = [];
  @Input() allCocktails: Cocktail[] = [];
  @Input() currentUnit: MeasureUnit = MeasureUnit.ML;
  @Input() availableUnits: MeasureUnit[] = Object.values(MeasureUnit);
  @Input() allTags: string[] = [];
  @Input() allGlasswareTypes: string[] = [];
  @Input() allIceTypes: string[] = [];
  
  @Output() closed = new EventEmitter<void>();
  @Output() cocktailSaved = new EventEmitter<Cocktail>();
  @Output() openIngredientModal = new EventEmitter<void>();
  @Output() unitChanged = new EventEmitter<MeasureUnit>();

  formCocktail: Cocktail = this.getEmptyCocktail();
  
  // For ingredient entry
  newIngredientEntry: { ingredientId: number; measureValue: number } = {
    ingredientId: 0,
    measureValue: 0
  };
  
  isCountBasedIngredient = false;
  ingredientSearchFilter = '';
  variationSearchFilter = '';
  
  // Form fields
  newStep = '';
  newTag = '';
  customTag = '';
  newGlassware = '';
  customGlassware = '';
  newIce = '';
  customIce = '';

  constructor(
    private measureService: MeasureService,
    private fuzzySearchService: FuzzySearchService
  ) {}

  ngOnInit(): void {
    this.initializeForm();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['isOpen'] && this.isOpen) {
      this.initializeForm();
    }
  }

  private initializeForm(): void {
    if (this.isEditMode && this.cocktail) {
      this.formCocktail = {
        name: this.cocktail.name,
        ingredients: [...this.cocktail.ingredients],
        steps: [...this.cocktail.steps],
        notes: this.cocktail.notes || '',
        tags: [...this.cocktail.tags],
        abv: this.cocktail.abv,
        baseSpirit: this.cocktail.baseSpirit,
        glasswareTypes: [...(this.cocktail.glasswareTypes || [])],
        iceTypes: [...(this.cocktail.iceTypes || [])],
        variationOfId: this.cocktail.variationOfId
      };
    } else {
      this.formCocktail = this.getEmptyCocktail();
    }
  }

  private getEmptyCocktail(): Cocktail {
    return {
      name: '',
      ingredients: [],
      steps: [],
      notes: '',
      tags: [],
      abv: 0,
      baseSpirit: 'none',
      glasswareTypes: [],
      iceTypes: []
    };
  }

  get filteredIngredients(): Ingredient[] {
    if (!this.ingredientSearchFilter.trim()) {
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

  onIngredientSelected(): void {
    const selectedIngredient = this.ingredients.find(
      ing => ing.id === this.newIngredientEntry.ingredientId
    );
    this.isCountBasedIngredient = selectedIngredient 
      ? (selectedIngredient.type === IngredientType.GARNISH || selectedIngredient.type === IngredientType.OTHER)
      : false;
  }

  addIngredientToCocktail(): void {
    // Check if search filter matches an ingredient name exactly
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
      
      if (this.isCountBasedIngredient) {
        measureMl = -Math.abs(this.newIngredientEntry.measureValue);
      } else {
        measureMl = this.measureService.convertToMl(this.newIngredientEntry.measureValue, this.currentUnit);
      }
      
      this.formCocktail.ingredients.push({ 
        ingredientId: this.newIngredientEntry.ingredientId, 
        measureMl: measureMl 
      });
      this.newIngredientEntry = { ingredientId: 0, measureValue: 0 };
      this.isCountBasedIngredient = false;
      this.ingredientSearchFilter = '';
    }
  }

  removeIngredient(index: number): void {
    this.formCocktail.ingredients.splice(index, 1);
  }

  dropIngredient(event: CdkDragDrop<CocktailIngredient[]>): void {
    moveItemInArray(this.formCocktail.ingredients, event.previousIndex, event.currentIndex);
  }

  addStep(): void {
    if (this.newStep.trim()) {
      this.formCocktail.steps.push(this.newStep);
      this.newStep = '';
    }
  }

  removeStep(index: number): void {
    this.formCocktail.steps.splice(index, 1);
  }

  dropStep(event: CdkDragDrop<string[]>): void {
    moveItemInArray(this.formCocktail.steps, event.previousIndex, event.currentIndex);
  }

  addTag(): void {
    const tagToAdd = this.customTag.trim() || this.newTag.trim();
    if (tagToAdd && !this.formCocktail.tags.includes(tagToAdd)) {
      this.formCocktail.tags.push(tagToAdd);
      this.newTag = '';
      this.customTag = '';
    }
  }

  removeTag(index: number): void {
    this.formCocktail.tags.splice(index, 1);
  }
  
  addGlassware(): void {
    const glasswareToAdd = this.customGlassware.trim() || this.newGlassware.trim();
    if (glasswareToAdd && !this.formCocktail.glasswareTypes.includes(glasswareToAdd)) {
      this.formCocktail.glasswareTypes.push(glasswareToAdd);
      this.newGlassware = '';
      this.customGlassware = '';
    }
  }
  
  removeGlassware(index: number): void {
    this.formCocktail.glasswareTypes.splice(index, 1);
  }
  
  addIce(): void {
    const iceToAdd = this.customIce.trim() || this.newIce.trim();
    if (iceToAdd && !this.formCocktail.iceTypes.includes(iceToAdd)) {
      this.formCocktail.iceTypes.push(iceToAdd);
      this.newIce = '';
      this.customIce = '';
    }
  }
  
  removeIce(index: number): void {
    this.formCocktail.iceTypes.splice(index, 1);
  }

  getIngredientName(ingredientId: number): string {
    const ingredient = this.ingredients.find(i => i.id === ingredientId);
    return ingredient ? ingredient.name : 'Unknown';
  }

  formatMeasure(measureMl: number): string {
    return this.measureService.formatMeasure(measureMl, this.currentUnit);
  }

  setUnit(unit: MeasureUnit): void {
    this.unitChanged.emit(unit);
  }

  onOpenIngredientModal(): void {
    this.openIngredientModal.emit();
  }
  
  get availableBaseCocktails(): Cocktail[] {
    // Filter out current cocktail and any that are variations of this cocktail
    const currentId = this.cocktail?.id;
    if (!currentId && !this.isEditMode) {
      // For new cocktails, return all cocktails
      return this.allCocktails;
    }
    
    // Find all cocktails that are variations of the current one (descendants)
    const descendants = this.getDescendants(currentId);
    
    return this.allCocktails.filter(c => {
      // Exclude current cocktail
      if (c.id === currentId) return false;
      // Exclude descendants to prevent circular references
      if (c.id && descendants.has(c.id)) return false;
      return true;
    });
  }
  
  get filteredAvailableBaseCocktails(): Cocktail[] {
    const available = this.availableBaseCocktails;
    
    if (!this.variationSearchFilter.trim()) {
      return available;
    }
    
    // Use fuzzy search to filter cocktails by name
    const results = this.fuzzySearchService.search(
      this.variationSearchFilter,
      available,
      cocktail => cocktail.name
    );
    return results.map(r => r.item);
  }
  
  private getDescendants(cocktailId?: number): Set<number> {
    const descendants = new Set<number>();
    if (!cocktailId) return descendants;
    
    // Find all cocktails that have this one as their base (direct children)
    const children = this.allCocktails.filter(c => c.variationOfId === cocktailId);
    
    for (const child of children) {
      if (child.id) {
        descendants.add(child.id);
        // Recursively add descendants of this child
        const childDescendants = this.getDescendants(child.id);
        childDescendants.forEach(id => descendants.add(id));
      }
    }
    
    return descendants;
  }

  onClose(): void {
    this.resetForm();
    this.closed.emit();
  }

  onSubmit(): void {
    if (this.formCocktail.name && this.formCocktail.ingredients.length > 0) {
      this.cocktailSaved.emit({ ...this.formCocktail });
      this.resetForm();
    }
  }

  private resetForm(): void {
    this.formCocktail = this.getEmptyCocktail();
    this.newIngredientEntry = { ingredientId: 0, measureValue: 0 };
    this.isCountBasedIngredient = false;
    this.ingredientSearchFilter = '';
    this.variationSearchFilter = '';
    this.newStep = '';
    this.newTag = '';
    this.customTag = '';
    this.newGlassware = '';
    this.customGlassware = '';
    this.newIce = '';
    this.customIce = '';
  }
}
