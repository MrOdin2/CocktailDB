import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Cocktail, Ingredient, MeasureUnit } from '../../../../models/models';
import { MeasureService } from '../../../../services/measure.service';
import { TranslatePipe } from '../../../../pipes/translate.pipe';

@Component({
  selector: 'app-cocktail-card',
  standalone: true,
  imports: [CommonModule, TranslatePipe],
  templateUrl: './cocktail-card.component.html',
  styleUrls: ['./cocktail-card.component.css']
})
export class CocktailCardComponent {
  @Input() cocktail!: Cocktail;
  @Input() ingredients: Ingredient[] = [];
  @Input() allCocktails: Cocktail[] = [];
  @Input() currentUnit: MeasureUnit = MeasureUnit.ML;
  
  @Output() edit = new EventEmitter<Cocktail>();
  @Output() delete = new EventEmitter<number>();

  constructor(private measureService: MeasureService) {}

  onEdit(): void {
    this.edit.emit(this.cocktail);
  }

  onDelete(): void {
    if (this.cocktail.id) {
      this.delete.emit(this.cocktail.id);
    }
  }

  getIngredientName(ingredientId: number): string {
    const ingredient = this.ingredients.find(i => i.id === ingredientId);
    return ingredient ? ingredient.name : 'Unknown';
  }
  
  getBaseCocktailName(baseCocktailId: number): string {
    const baseCocktail = this.allCocktails.find(c => c.id === baseCocktailId);
    return baseCocktail ? baseCocktail.name : 'Unknown';
  }

  formatMeasure(measureMl: number): string {
    return this.measureService.formatMeasure(measureMl, this.currentUnit);
  }
}
