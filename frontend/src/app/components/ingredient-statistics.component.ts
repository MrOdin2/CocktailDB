import { Component, OnInit, OnDestroy, ViewChild, ElementRef, AfterViewInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NgxChartsModule, LegendPosition, Color, ScaleType } from '@swimlane/ngx-charts';
import { Ingredient, Cocktail } from '../models/models';
import { ApiService } from '../services/api.service';
import { ThemeService, Theme } from '../services/theme.service';
import { Subscription } from 'rxjs';
import * as d3 from 'd3';

interface ChartData {
  name: string;
  value: number;
}

interface IngredientPair {
  ingredient1: string;
  ingredient2: string;
  count: number;
}

@Component({
  selector: 'app-ingredient-statistics',
  imports: [CommonModule, NgxChartsModule],
  templateUrl: './ingredient-statistics.component.html',
  styleUrls: ['./ingredient-statistics.component.css']
})
export class IngredientStatisticsComponent implements OnInit, OnDestroy, AfterViewInit {
  @ViewChild('heatmapContainer', { static: false }) heatmapContainer?: ElementRef;
  
  ingredients: Ingredient[] = [];
  cocktails: Cocktail[] = [];
  pieChartData: ChartData[] = [];
  usageChartData: ChartData[] = [];
  typeUsageChartData: ChartData[] = [];
  ingredientPairs: IngredientPair[] = [];

  // Chart options
  view: [number, number] = [500, 400];
  barChartView: [number, number] = [700, 400];
  legendPosition: LegendPosition = LegendPosition.Below;
  isDoughnut = false;
  showXAxis = true;
  showYAxis = true;
  showXAxisLabel = true;
  showYAxisLabel = true;
  xAxisLabel = 'Ingredient';
  yAxisLabel = 'Usage Count';

  colorScheme: Color = {
    name: 'custom',
    selectable: true,
    group: ScaleType.Ordinal,
    domain: ['#4bc0c0', '#ff6384']
  };

  barColorScheme: Color = {
    name: 'custom',
    selectable: true,
    group: ScaleType.Ordinal,
    domain: ['#5aa454', '#a10a28', '#c7b42c', '#aae3f5']
  };

  private themeSubscription?: Subscription;
  private svg?: d3.Selection<SVGSVGElement, unknown, null, undefined>;

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

  ngAfterViewInit(): void {
    // Heatmap will be initialized when data is loaded
    if (this.ingredientPairs.length > 0) {
      setTimeout(() => this.initializeHeatmap(), 100);
    }
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
        this.barColorScheme = {
          name: 'terminal-green',
          selectable: true,
          group: ScaleType.Ordinal,
          domain: ['#00ff00', '#00cc00', '#009900', '#006600']
        };
        break;
      case 'cyberpunk':
        this.colorScheme = {
          name: 'cyberpunk',
          selectable: true,
          group: ScaleType.Ordinal,
          domain: ['#00ffff', '#ff00ff']
        };
        this.barColorScheme = {
          name: 'cyberpunk',
          selectable: true,
          group: ScaleType.Ordinal,
          domain: ['#00ffff', '#ff00ff', '#ffff00', '#00ff00']
        };
        break;
      case 'amber':
        this.colorScheme = {
          name: 'amber',
          selectable: true,
          group: ScaleType.Ordinal,
          domain: ['#ffb000', '#ff8800']
        };
        this.barColorScheme = {
          name: 'amber',
          selectable: true,
          group: ScaleType.Ordinal,
          domain: ['#ffb000', '#ff9500', '#ff7a00', '#ff5f00']
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
        this.barColorScheme = {
          name: 'basic',
          selectable: true,
          group: ScaleType.Ordinal,
          domain: ['#5aa454', '#a10a28', '#c7b42c', '#aae3f5']
        };
        break;
    }
    
    // Redraw heatmap if it exists
    if (this.svg && this.ingredientPairs.length > 0) {
      this.initializeHeatmap();
    }
  }

  loadIngredients(): void {
    this.apiService.getAllIngredients().subscribe({
      next: (data: Ingredient[]) => {
        this.ingredients = data;
        this.updateChartData();
        if (this.cocktails.length > 0) {
          this.updateUsageData();
        }
      },
      error: (error: any) => {
        console.error('Error loading ingredients:', error);
      }
    });
  }

  loadCocktails(): void {
    this.apiService.getAllCocktails().subscribe({
      next: (data: Cocktail[]) => {
        this.cocktails = data;
        if (this.ingredients.length > 0) {
          this.updateUsageData();
        }
      },
      error: (error: any) => {
        console.error('Error loading cocktails:', error);
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

  updateUsageData(): void {
    if (!this.ingredients.length || !this.cocktails.length) {
      return;
    }

    // Calculate ingredient usage frequency
    const usageMap = new Map<number, number>();
    this.cocktails.forEach(cocktail => {
      cocktail.ingredients.forEach(ci => {
        usageMap.set(ci.ingredientId, (usageMap.get(ci.ingredientId) || 0) + 1);
      });
    });

    // Create chart data sorted by usage
    this.usageChartData = Array.from(usageMap.entries())
      .map(([ingredientId, count]) => {
        const ingredient = this.ingredients.find(i => i.id === ingredientId);
        return {
          name: ingredient?.name || 'Unknown',
          value: count
        };
      })
      .sort((a, b) => b.value - a.value)
      .slice(0, 15); // Top 15 ingredients

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

    this.typeUsageChartData = Array.from(typeUsageMap.entries())
      .map(([type, count]) => ({ name: type, value: count }))
      .sort((a, b) => b.value - a.value);

    // Calculate ingredient co-occurrence (which ingredients are used together)
    this.calculateIngredientPairs();
    
    // Initialize heatmap after data is ready
    setTimeout(() => {
      if (this.heatmapContainer && this.ingredientPairs.length > 0) {
        this.initializeHeatmap();
      }
    }, 100);
  }

  calculateIngredientPairs(): void {
    const pairMap = new Map<string, number>();
    
    this.cocktails.forEach(cocktail => {
      const ingredientIds = cocktail.ingredients.map(ci => ci.ingredientId);
      
      // For each pair of ingredients in this cocktail
      for (let i = 0; i < ingredientIds.length; i++) {
        for (let j = i + 1; j < ingredientIds.length; j++) {
          const id1 = ingredientIds[i];
          const id2 = ingredientIds[j];
          const ingredient1 = this.ingredients.find(ing => ing.id === id1);
          const ingredient2 = this.ingredients.find(ing => ing.id === id2);
          
          if (ingredient1 && ingredient2) {
            // Create a consistent key for the pair
            const key = [ingredient1.name, ingredient2.name].sort().join('|');
            pairMap.set(key, (pairMap.get(key) || 0) + 1);
          }
        }
      }
    });

    // Convert to array and filter for pairs that appear at least twice
    this.ingredientPairs = Array.from(pairMap.entries())
      .map(([key, count]) => {
        const [name1, name2] = key.split('|');
        return { ingredient1: name1, ingredient2: name2, count };
      })
      .filter(pair => pair.count >= 2)
      .sort((a, b) => b.count - a.count)
      .slice(0, 20); // Top 20 pairs
  }

  initializeHeatmap(): void {
    if (!this.heatmapContainer || !this.ingredientPairs.length) {
      return;
    }

    const element = this.heatmapContainer.nativeElement;
    const width = 700;
    const height = Math.min(500, this.ingredientPairs.length * 25 + 60);
    const margin = { top: 20, right: 20, bottom: 60, left: 150 };

    // Clear existing SVG
    d3.select(element).selectAll('*').remove();

    // Create SVG
    this.svg = d3.select(element)
      .append('svg')
      .attr('width', '100%')
      .attr('height', height)
      .attr('viewBox', [0, 0, width, height]);

    const g = this.svg.append('g')
      .attr('transform', `translate(${margin.left},${margin.top})`);

    const innerWidth = width - margin.left - margin.right;
    const innerHeight = height - margin.top - margin.bottom;

    // Create scales
    const maxCount = d3.max(this.ingredientPairs, d => d.count) || 1;
    
    const colorScale = d3.scaleSequential()
      .domain([0, maxCount])
      .interpolator(this.getHeatmapColorInterpolator());

    const yScale = d3.scaleBand()
      .domain(this.ingredientPairs.map((d, i) => i.toString()))
      .range([0, innerHeight])
      .padding(0.1);

    // Draw bars
    g.selectAll('rect')
      .data(this.ingredientPairs)
      .join('rect')
      .attr('y', (d, i) => yScale(i.toString()) || 0)
      .attr('x', 0)
      .attr('width', d => (d.count / maxCount) * innerWidth)
      .attr('height', yScale.bandwidth())
      .attr('fill', d => colorScale(d.count))
      .attr('stroke', 'var(--card-bg, #fff)')
      .attr('stroke-width', 1)
      .append('title')
      .text(d => `${d.ingredient1} + ${d.ingredient2}: ${d.count} cocktails`);

    // Add labels
    g.selectAll('text.pair-label')
      .data(this.ingredientPairs)
      .join('text')
      .attr('class', 'pair-label')
      .attr('y', (d, i) => (yScale(i.toString()) || 0) + yScale.bandwidth() / 2)
      .attr('x', -5)
      .attr('text-anchor', 'end')
      .attr('dominant-baseline', 'middle')
      .attr('fill', 'var(--text-color, #333)')
      .attr('font-size', '11px')
      .text(d => `${d.ingredient1} + ${d.ingredient2}`);

    // Add count labels on bars
    g.selectAll('text.count-label')
      .data(this.ingredientPairs)
      .join('text')
      .attr('class', 'count-label')
      .attr('y', (d, i) => (yScale(i.toString()) || 0) + yScale.bandwidth() / 2)
      .attr('x', d => Math.max((d.count / maxCount) * innerWidth + 5, 20))
      .attr('text-anchor', 'start')
      .attr('dominant-baseline', 'middle')
      .attr('fill', 'var(--text-color, #333)')
      .attr('font-size', '11px')
      .attr('font-weight', 'bold')
      .text(d => d.count);

    // Add title
    this.svg.append('text')
      .attr('x', width / 2)
      .attr('y', 15)
      .attr('text-anchor', 'middle')
      .attr('fill', 'var(--text-color, #333)')
      .attr('font-size', '14px')
      .attr('font-weight', 'bold')
      .text('Top Ingredient Combinations');
  }

  private getHeatmapColorInterpolator(): (t: number) => string {
    const theme = this.themeService.getCurrentTheme();
    
    switch (theme) {
      case 'terminal-green':
        return d3.interpolateRgb('#003300', '#00ff00');
      case 'cyberpunk':
        return d3.interpolateRgb('#003333', '#00ffff');
      case 'amber':
        return d3.interpolateRgb('#332200', '#ffb000');
      default:
        return d3.interpolateRgb('#e8f5e9', '#2e7d32');
    }
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

  onBarSelect(event: any): void {
    console.log('Bar chart selection:', event);
  }
}
