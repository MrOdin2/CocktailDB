import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NgxChartsModule, LegendPosition, Color, ScaleType } from '@swimlane/ngx-charts';
import { Ingredient } from '../models/models';
import { ApiService } from '../services/api.service';
import { ThemeService, Theme } from '../services/theme.service';
import { Subscription } from 'rxjs';

interface ChartData {
  name: string;
  value: number;
}

@Component({
  selector: 'app-ingredient-statistics',
  imports: [CommonModule, NgxChartsModule],
  templateUrl: './ingredient-statistics.component.html',
  styleUrls: ['./ingredient-statistics.component.css']
})
export class IngredientStatisticsComponent implements OnInit, OnDestroy {
  ingredients: Ingredient[] = [];
  pieChartData: ChartData[] = [];

  // Chart options
  view: [number, number] = [500, 400];
  legendPosition: LegendPosition = LegendPosition.Below;
  isDoughnut = false;

  colorScheme: Color = {
    name: 'custom',
    selectable: true,
    group: ScaleType.Ordinal,
    domain: ['#4bc0c0', '#ff6384']
  };

  private themeSubscription?: Subscription;

  constructor(
    private apiService: ApiService,
    private themeService: ThemeService
  ) {}

  ngOnInit(): void {
    this.loadIngredients();
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
        this.colorScheme = {
          name: 'terminal-green',
          selectable: true,
          group: ScaleType.Ordinal,
          domain: ['#00ff00', '#008800']
        };
        break;
      case 'cyberpunk':
        this.colorScheme = {
          name: 'cyberpunk',
          selectable: true,
          group: ScaleType.Ordinal,
          domain: ['#00ffff', '#ff00ff']
        };
        break;
      case 'amber':
        this.colorScheme = {
          name: 'amber',
          selectable: true,
          group: ScaleType.Ordinal,
          domain: ['#ffb000', '#ff8800']
        };
        break;
      case 'basic':
      default:
        this.colorScheme = {
          name: 'basic',
          selectable: true,
          group: ScaleType.Ordinal,
          domain: ['#5a9', '#e67']
        };
        break;
    }
  }

  loadIngredients(): void {
    this.apiService.getAllIngredients().subscribe({
      next: (data: Ingredient[]) => {
        this.ingredients = data;
        this.updateChartData();
      },
      error: (error: any) => {
        console.error('Error loading ingredients:', error);
      }
    });
  }

  updateChartData(): void {
    const inStock = this.ingredients.filter(i => i.inStock).length;
    const notInStock = this.ingredients.filter(i => !i.inStock).length;
    
    this.pieChartData = [
      { name: 'In Stock', value: inStock },
      { name: 'Not Available', value: notInStock }
    ];
  }

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
    if (this.totalIngredients === 0) return 0;
    return (this.inStockCount / this.totalIngredients) * 100;
  }

  onSelect(event: any): void {
    console.log('Chart selection:', event);
  }
}
