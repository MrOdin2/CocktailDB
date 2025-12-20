import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Ingredient, IngredientType } from '../../../../models/models';
import { ModalComponent } from '../../../util/modal.component';
import { TranslatePipe } from '../../../../pipes/translate.pipe';

@Component({
  selector: 'app-ingredient-modal',
  standalone: true,
  imports: [FormsModule, ModalComponent, TranslatePipe],
  templateUrl: './ingredient-modal.component.html',
  styleUrls: ['./ingredient-modal.component.css']
})
export class IngredientModalComponent {
  @Input() isOpen = false;
  @Input() ingredientTypes: IngredientType[] = Object.values(IngredientType);
  
  @Output() closed = new EventEmitter<void>();
  @Output() ingredientCreated = new EventEmitter<Ingredient>();

  newIngredient: Ingredient = {
    name: '',
    type: IngredientType.SPIRIT,
    abv: 0,
    inStock: false
  };

  onClose(): void {
    this.resetForm();
    this.closed.emit();
  }

  onSubmit(): void {
    if (this.newIngredient.name && this.newIngredient.type !== undefined) {
      this.ingredientCreated.emit({ ...this.newIngredient });
      this.resetForm();
    }
  }

  private resetForm(): void {
    this.newIngredient = {
      name: '',
      type: IngredientType.SPIRIT,
      abv: 0,
      inStock: false
    };
  }
}
