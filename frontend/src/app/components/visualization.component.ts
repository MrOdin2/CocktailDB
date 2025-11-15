import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NgxChartsModule, LegendPosition } from '@swimlane/ngx-charts';
import { Ingredient } from '../models/models';
import { ApiService } from '../services/api.service';

interface ChartData {
  name: string;
  value: number;
}

@Component({
  selector: 'app-visualization',
  imports: [CommonModule, NgxChartsModule],
  templateUrl: './visualization.component.html',
  styleUrls: ['./visualization.component.css']
})
export class VisualizationComponent implements OnInit {
  ingredients: Ingredient[] = [];
  
  // Active tab
  activeTab: 'ingredients' | 'cocktails' | 'trends' = 'ingredients';
  
  // Pie chart data
  pieChartData: ChartData[] = [];

  // Chart options
  view: [number, number] = [500, 400];
  showLegend = true;
  showLabels = true;
  isDoughnut = false;
  legendPosition: LegendPosition = LegendPosition.Below;

  colorScheme = 'vivid';

  constructor(private apiService: ApiService) {}

  ngOnInit(): void {
    this.loadIngredients();
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

  setActiveTab(tab: 'ingredients' | 'cocktails' | 'trends'): void {
    this.activeTab = tab;
  }
}
