import { Component, Input, OnChanges, SimpleChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NgxChartsModule, LegendPosition, Color, ScaleType } from '@swimlane/ngx-charts';
import { Ingredient } from '../../../../models/models';

interface ChartData {
  name: string;
  value: number;
}

@Component({
    selector: 'app-ingredient-availability-chart',
    imports: [CommonModule, NgxChartsModule],
    template: `
        @if (chartData.length > 0) {
            <ngx-charts-pie-chart
                    [view]="view"
                    [results]="chartData"
                    [legend]="true"
                    [legendPosition]="legendPosition"
                    [labels]="true"
                    [doughnut]="isDoughnut"
                    [scheme]="colorScheme">
            </ngx-charts-pie-chart>
        } @else {
            <div class="no-data">
                <p>No ingredient data available. Add some ingredients to see statistics!</p>
            </div>
        }
    `,
    standalone: true,
    styles: [`
        :host {
            align-content: center;
            display: block;
            width: 100%;
            height: 100%;
        }

        .no-data {
            text-align: center;
            padding: 40px;
            color: #666;
        }
    `]
})
export class IngredientAvailabilityChartComponent implements OnChanges {
  @Input() ingredients: Ingredient[] = [];
  @Input() colorScheme: Color = {
    name: 'custom',
    selectable: true,
    group: ScaleType.Ordinal,
    domain: ['#4bc0c0', '#ff6384']
  };

  chartData: ChartData[] = [];
  view: [number, number] = [500, 400];
  legendPosition: LegendPosition = LegendPosition.Below;
  isDoughnut = false;

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['ingredients'] && this.ingredients.length > 0) {
      this.updateChartData();
    }
  }

  private updateChartData(): void {
    const inStock = this.ingredients.filter(i => i.inStock).length;
    const notInStock = this.ingredients.filter(i => !i.inStock).length;
    
    this.chartData = [
      { name: 'In Stock', value: inStock },
      { name: 'Not Available', value: notInStock }
    ];
  }
}
