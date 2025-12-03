import { Component, Input, OnChanges, OnInit, SimpleChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NgxChartsModule, Color, ScaleType } from '@swimlane/ngx-charts';
import { Ingredient, Cocktail } from '../../../../models/models';
import { ApiService } from '../../../../services/api.service';

interface ChartData {
  name: string;
  series: { name: string; value: number }[];
}

@Component({
    selector: 'app-ingredient-unlock-potential-chart',
    imports: [CommonModule, NgxChartsModule],
    template: `
        <div class="chart-container">
            @if (chartData.length > 0) {
                <ngx-charts-bar-horizontal-2d
                        [view]="view"
                        [results]="chartData"
                        [xAxis]="true"
                        [yAxis]="true"
                        [showXAxisLabel]="true"
                        [showYAxisLabel]="true"
                        [xAxisLabel]="'New Recipes Unlocked'"
                        [yAxisLabel]="'Ingredient'"
                        [legend]="true"
                        [legendTitle]="''"
                        [scheme]="colorScheme">
                </ngx-charts-bar-horizontal-2d>
            }
            <div class="legend-info">
                <p><strong>Direct:</strong> Cocktails you can make exactly as written</p>
                <p><strong>With Substitutes:</strong> Additional cocktails available using substitutes or alternatives</p>
            </div>
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

        .legend-info {
            margin-top: 1rem;
            font-size: 0.85rem;
            color: var(--text-color, #666);
        }

        .legend-info p {
            margin: 0.25rem 0;
        }
    `]
})
export class IngredientUnlockPotentialChartComponent implements OnChanges, OnInit {
  @Input() ingredients: Ingredient[] = [];
  @Input() cocktails: Cocktail[] = [];
  @Input() colorScheme: Color = {
    name: 'custom',
    selectable: true,
    group: ScaleType.Ordinal,
    domain: ['#5aa454', '#3498db', '#e74c3c', '#f39c12', '#9b59b6', '#1abc9c', '#e67e22', '#34495e', '#16a085', '#27ae60', '#2980b9', '#8e44ad', '#2c3e50', '#c0392b', '#d35400']
  };

  chartData: ChartData[] = [];
  view: [number, number] = [700, 400];
  private impactData: { [ingredientId: number]: { first: number; second: number } } = {};

  constructor(private apiService: ApiService) {}

  ngOnInit(): void {
    this.loadImpactData();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if ((changes['ingredients'] || changes['cocktails']) && this.ingredients.length > 0) {
      this.updateChartData();
    }
  }

  private loadImpactData(): void {
    this.apiService.getIngredientImpact().subscribe({
      next: (data) => {
        this.impactData = data;
        this.updateChartData();
      },
      error: (error) => {
        console.error('Error loading ingredient impact data:', error);
        // Fall back to local calculation if API fails
        this.updateChartDataFallback();
      }
    });
  }

  private updateChartData(): void {
    if (Object.keys(this.impactData).length === 0 || this.ingredients.length === 0) {
      return;
    }

    const unavailableIngredients = this.ingredients.filter(i => !i.inStock);
    
    const unlockPotential = unavailableIngredients
      .map(ingredient => {
        const impact = this.impactData[ingredient.id!];
        if (!impact) return null;
        
        const directUnlock = impact.first;
        const withSubstitutes = impact.second - impact.first; // Additional cocktails with substitutes
        
        if (directUnlock === 0 && withSubstitutes === 0) return null;
        
        return {
          name: ingredient.name,
          series: [
            { name: 'Direct', value: directUnlock },
            { name: 'With Substitutes', value: withSubstitutes }
          ],
          totalValue: impact.second
        };
      })
      .filter((item): item is NonNullable<typeof item> => item !== null)
      .sort((a, b) => b.totalValue - a.totalValue)
      .slice(0, 10)
      .map(({ name, series }) => ({ name, series }));

    this.chartData = unlockPotential;
  }

  // Fallback method if API is not available
  private updateChartDataFallback(): void {
    const unavailableIngredients = this.ingredients.filter(i => !i.inStock);
    
    const unlockPotential = unavailableIngredients.map(ingredient => {
      const cocktailsWithIngredient = this.cocktails.filter(cocktail =>
        cocktail.ingredients.some(ci => ci.ingredientId === ingredient.id)
      );
      
      const unmakeableCocktails = cocktailsWithIngredient.filter(cocktail => {
        const otherIngredients = cocktail.ingredients.filter(ci => ci.ingredientId !== ingredient.id);
        return otherIngredients.every(ci => {
          const ing = this.ingredients.find(i => i.id === ci.ingredientId);
          return ing && ing.inStock;
        });
      });
      
      return {
        name: ingredient.name,
        series: [
          { name: 'Direct', value: unmakeableCocktails.length },
          { name: 'With Substitutes', value: 0 }
        ],
        totalValue: unmakeableCocktails.length
      };
    })
    .filter(item => item.totalValue > 0)
    .sort((a, b) => b.totalValue - a.totalValue)
    .slice(0, 10)
    .map(({ name, series }) => ({ name, series }));

    this.chartData = unlockPotential;
  }
}
