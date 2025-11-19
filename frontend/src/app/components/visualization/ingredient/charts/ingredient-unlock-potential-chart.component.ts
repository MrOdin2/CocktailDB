import { Component, Input, OnChanges, SimpleChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NgxChartsModule, Color, ScaleType } from '@swimlane/ngx-charts';
import { Ingredient, Cocktail } from '../../../../models/models';

interface ChartData {
  name: string;
  value: number;
}

@Component({
    selector: 'app-ingredient-unlock-potential-chart',
    imports: [CommonModule, NgxChartsModule],
    template: `
        <div class="chart-container">
            @if (chartData.length > 0) {
                <ngx-charts-bar-horizontal
                        [view]="view"
                        [results]="chartData"
                        [xAxis]="true"
                        [yAxis]="true"
                        [showXAxisLabel]="true"
                        [showYAxisLabel]="true"
                        [xAxisLabel]="'New Recipes Unlocked'"
                        [yAxisLabel]="'Ingredient'"
                        [scheme]="colorScheme">
                </ngx-charts-bar-horizontal>
            }
        </div>
    `,
    standalone: true,
    styles: [`
        :host {
            display: block;
            width: 100%;
            height: 100%;
        }

        .chart-container {
            width: 100%;
            height: 100%;
            min-height: 400px;
        }
    `]
})
export class IngredientUnlockPotentialChartComponent implements OnChanges {
  @Input() ingredients: Ingredient[] = [];
  @Input() cocktails: Cocktail[] = [];
  @Input() colorScheme: Color = {
    name: 'custom',
    selectable: true,
    group: ScaleType.Ordinal,
    domain: ['#5aa454', '#e74c3c', '#3498db', '#f39c12', '#9b59b6', '#1abc9c', '#e67e22', '#34495e', '#16a085', '#27ae60', '#2980b9', '#8e44ad', '#2c3e50', '#c0392b', '#d35400']
  };

  chartData: ChartData[] = [];
  view: [number, number] = [700, 400];

  ngOnChanges(changes: SimpleChanges): void {
    if ((changes['ingredients'] || changes['cocktails']) && this.ingredients.length > 0 && this.cocktails.length > 0) {
      this.updateChartData();
    }
  }

  private updateChartData(): void {
    // For each unavailable ingredient, count how many new recipes it would unlock
    const unavailableIngredients = this.ingredients.filter(i => !i.inStock);
    
    const unlockPotential = unavailableIngredients.map(ingredient => {
      // Find cocktails that contain this ingredient
      const cocktailsWithIngredient = this.cocktails.filter(cocktail =>
        cocktail.ingredients.some(ci => ci.ingredientId === ingredient.id)
      );
      
      // Count how many of those cocktails we can't currently make
      const unmakeableCocktails = cocktailsWithIngredient.filter(cocktail => {
        // Check if all ingredients except this one are in stock
        const otherIngredients = cocktail.ingredients.filter(ci => ci.ingredientId !== ingredient.id);
        return otherIngredients.every(ci => {
          const ing = this.ingredients.find(i => i.id === ci.ingredientId);
          return ing && ing.inStock;
        });
      });
      
      return {
        name: ingredient.name,
        value: unmakeableCocktails.length
      };
    })
    .filter(item => item.value > 0)
    .sort((a, b) => b.value - a.value)
    .slice(0, 10); // Top 10

    this.chartData = unlockPotential;
  }
}
