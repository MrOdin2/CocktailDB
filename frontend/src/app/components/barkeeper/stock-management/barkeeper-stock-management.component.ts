import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { IngredientService } from '../../../services/ingredient.service';
import { Ingredient } from '../../../models/models';

@Component({
  selector: 'app-barkeeper-stock-management',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './barkeeper-stock-management.component.html',
  styleUrls: ['../barkeeper-shared.css', './barkeeper-stock-management.component.css']
})
export class BarkeeperStockManagementComponent implements OnInit {
  ingredients: Ingredient[] = [];
  isLoading = false;

  constructor(private ingredientService: IngredientService) {}

  ngOnInit(): void {
    this.loadIngredients();
  }

  loadIngredients(): void {
    this.isLoading = true;
    this.ingredientService.getAll().subscribe({
      next: (ingredients: Ingredient[]) => {
        this.ingredients = ingredients;
        this.isLoading = false;
      },
      error: (error: any) => {
        console.error('Error loading ingredients:', error);
        this.isLoading = false;
      }
    });
  }

  // Get ingredients that are in stock
  get inStockIngredients(): Ingredient[] {
    return this.ingredients.filter(i => i.inStock);
  }

  // Get ingredients that are out of stock
  get outOfStockIngredients(): Ingredient[] {
    return this.ingredients.filter(i => !i.inStock);
  }

  toggleIngredientStock(ingredient: Ingredient): void {
    const updated = { ...ingredient, inStock: !ingredient.inStock };
    this.ingredientService.update(ingredient.id!, updated).subscribe({
      next: () => {
        ingredient.inStock = !ingredient.inStock;
      },
      error: (error: any) => {
        console.error('Error updating ingredient:', error);
      }
    });
  }
}
