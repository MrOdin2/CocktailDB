import { Component, Input, OnChanges, SimpleChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NgxChartsModule, Color, ScaleType } from '@swimlane/ngx-charts';
import { Ingredient, Cocktail } from '../../../../models/models';

interface ChartData {
  name: string;
  value: number;
}

@Component({
    selector: 'app-most-used-ingredients-ngx-chart',
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
                        [xAxisLabel]="'Usage Count'"
                        [yAxisLabel]="'Ingredient'"
                        [scheme]="colorScheme">
                </ngx-charts-bar-horizontal>
            }
        </div>
    `,
    standalone: true,
    styles: [`
        :host {
            display: flex;
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
export class MostUsedIngredientsNgxChartComponent implements OnChanges {
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
    // Calculate ingredient usage frequency
    const usageMap = new Map<number, number>();
    this.cocktails.forEach(cocktail => {
      cocktail.ingredients.forEach(ci => {
        usageMap.set(ci.ingredientId, (usageMap.get(ci.ingredientId) || 0) + 1);
      });
    });

    // Create chart data sorted by usage
    this.chartData = Array.from(usageMap.entries())
      .map(([ingredientId, count]) => {
        const ingredient = this.ingredients.find(i => i.id === ingredientId);
        return {
          name: ingredient?.name || 'Unknown',
          value: count
        };
      })
      .sort((a, b) => b.value - a.value)
      .slice(0, 20); // Top 15 ingredients
  }
}
