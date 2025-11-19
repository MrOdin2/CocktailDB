import { Component, Input, OnChanges, SimpleChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NgxChartsModule, Color, ScaleType } from '@swimlane/ngx-charts';
import { Ingredient, Cocktail } from '../../../../models/models';

interface ChartData {
  name: string;
  value: number;
}

@Component({
    selector: 'app-ingredient-usage-by-type-chart',
    imports: [CommonModule, NgxChartsModule],
    template: `
        <div class="chart-container">
            @if (chartData.length > 0) {
                <ngx-charts-bar-vertical
                        [view]="view"
                        [results]="chartData"
                        [xAxis]="true"
                        [yAxis]="true"
                        [showXAxisLabel]="true"
                        [showYAxisLabel]="true"
                        [xAxisLabel]="'Ingredient Type'"
                        [yAxisLabel]="'Usage Count'"
                        [scheme]="colorScheme">
                </ngx-charts-bar-vertical>
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
export class IngredientUsageByTypeChartComponent implements OnChanges {
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
    // Calculate ingredient usage by type
    const typeUsageMap = new Map<string, number>();
    this.cocktails.forEach(cocktail => {
      cocktail.ingredients.forEach(ci => {
        const ingredient = this.ingredients.find(i => i.id === ci.ingredientId);
        if (ingredient) {
          typeUsageMap.set(ingredient.type, (typeUsageMap.get(ingredient.type) || 0) + 1);
        }
      });
    });

    this.chartData = Array.from(typeUsageMap.entries())
      .map(([type, count]) => ({ name: type, value: count }))
      .sort((a, b) => b.value - a.value);
  }
}
