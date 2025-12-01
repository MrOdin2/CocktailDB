import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Ingredient, Cocktail } from '../../../models/models';
import { ApiService } from '../../../services/api.service';
import { ThemeService, Theme } from '../../../services/theme.service';
import { Subscription } from 'rxjs';
import { Color, ScaleType } from '@swimlane/ngx-charts';
import { IngredientAvailabilityChartComponent } from './charts/ingredient-availability-chart.component';
import { MostUsedIngredientsNgxChartComponent } from './charts/most-used-ingredients-ngx-chart.component';
import { IngredientUnlockPotentialChartComponent } from './charts/ingredient-unlock-potential-chart.component';
import { IngredientUsageByTypeChartComponent } from './charts/ingredient-usage-by-type-chart.component';
import { IngredientHeatmapComponent } from './charts/ingredient-heatmap.component';
import { IngredientNetworkGraphComponent } from './charts/ingredient-network-graph.component';

@Component({
    selector: 'app-ingredient-statistics',
    imports: [
        CommonModule,
        IngredientAvailabilityChartComponent,
        MostUsedIngredientsNgxChartComponent,
        IngredientUnlockPotentialChartComponent,
        IngredientUsageByTypeChartComponent,
        IngredientHeatmapComponent,
        IngredientNetworkGraphComponent
    ],
    templateUrl: './ingredient-statistics.component.html',
    standalone: true,
    styleUrls: ['./ingredient-statistics.component.css']
})
export class IngredientStatisticsComponent implements OnInit, OnDestroy {
  ingredients: Ingredient[] = [];
  cocktails: Cocktail[] = [];

  // Stats for display
  get totalIngredients(): number {
    return this.ingredients.length;
  }

  get inStockCount(): number {
    return this.ingredients.filter(i => i.inStock).length;
  }

  get notInStockCount(): number {
    return this.ingredients.filter(i => !i.inStock).length;
  }

  get stockPercentage(): number {
    return this.totalIngredients > 0 ? (this.inStockCount / this.totalIngredients) * 100 : 0;
  }

  // Color schemes for charts
  pieColorScheme: Color = {
    name: 'custom',
    selectable: true,
    group: ScaleType.Ordinal,
    domain: ['#4bc0c0', '#ff6384']
  };

  barColorScheme: Color = {
    name: 'custom',
    selectable: true,
    group: ScaleType.Ordinal,
    domain: ['#5aa454', '#e74c3c', '#3498db', '#f39c12', '#9b59b6', '#1abc9c', '#e67e22', '#34495e', '#16a085', '#27ae60', '#2980b9', '#8e44ad', '#2c3e50', '#c0392b', '#d35400']
  };

  private themeSubscription?: Subscription;

  constructor(
    private apiService: ApiService,
    private themeService: ThemeService
  ) {}

  ngOnInit(): void {
    this.loadIngredients();
    this.loadCocktails();
    this.updateColorScheme(this.themeService.getCurrentTheme());
    
    // Subscribe to theme changes
    this.themeSubscription = this.themeService.currentTheme$.subscribe(theme => {
      this.updateColorScheme(theme);
    });
  }

  ngOnDestroy(): void {
    if (this.themeSubscription) {
      this.themeSubscription.unsubscribe();
    }
  }

  private updateColorScheme(theme: Theme): void {
    switch (theme) {
      case 'terminal-green':
        this.pieColorScheme = {
          name: 'terminal-green',
          selectable: true,
          group: ScaleType.Ordinal,
          domain: ['#00ff00', '#00cc00']
        };
        this.barColorScheme = {
          name: 'terminal-green',
          selectable: true,
          group: ScaleType.Ordinal,
          domain: ['#00ff00', '#00dd00', '#00bb00', '#009900', '#00ff00', '#33ff33', '#00cc00', '#00aa00', '#008800', '#00ff44', '#00ee00', '#00bb33', '#00ff66', '#00dd33', '#00cc44']
        };
        break;
      case 'cyberpunk':
        this.pieColorScheme = {
          name: 'cyberpunk',
          selectable: true,
          group: ScaleType.Ordinal,
          domain: ['#00ffff', '#ff00ff']
        };
        this.barColorScheme = {
          name: 'cyberpunk',
          selectable: true,
          group: ScaleType.Ordinal,
          domain: ['#00ffff', '#ff00ff', '#00ff88', '#ff00aa', '#00ddff', '#ff44ff', '#00ffaa', '#ff00cc', '#33ffff', '#ff33ff', '#00eeee', '#ff66ff', '#00ccdd', '#ff00dd', '#00ffcc']
        };
        break;
      case 'amber':
        this.pieColorScheme = {
          name: 'amber',
          selectable: true,
          group: ScaleType.Ordinal,
          domain: ['#ffb000', '#ff9500']
        };
        this.barColorScheme = {
          name: 'amber',
          selectable: true,
          group: ScaleType.Ordinal,
          domain: ['#ffb000', '#ffa500', '#ff9500', '#ff8500', '#ffaa00', '#ffb033', '#ffa033', '#ff9033', '#ffb500', '#ffa000', '#ff9a00', '#ffb533', '#ffa533', '#ff9533', '#ffaa33']
        };
        break;
      case 'basic':
      default:
        this.pieColorScheme = {
          name: 'basic',
          selectable: true,
          group: ScaleType.Ordinal,
          domain: ['#5aa454', '#e67e22']
        };
        this.barColorScheme = {
          name: 'basic',
          selectable: true,
          group: ScaleType.Ordinal,
          domain: ['#5aa454', '#e74c3c', '#3498db', '#f39c12', '#9b59b6', '#1abc9c', '#e67e22', '#34495e', '#16a085', '#27ae60', '#2980b9', '#8e44ad', '#2c3e50', '#c0392b', '#d35400']
        };
        break;
    }
  }

  private loadIngredients(): void {
    this.apiService.getAllIngredients().subscribe({
      next: (data: Ingredient[]) => {
        this.ingredients = data;
      },
      error: (error: any) => {
        console.error('Error loading ingredients:', error);
      }
    });
  }

  private loadCocktails(): void {
    this.apiService.getAllCocktails().subscribe({
      next: (data: Cocktail[]) => {
        this.cocktails = data;
      },
      error: (error: any) => {
        console.error('Error loading cocktails:', error);
      }
    });
  }
}
