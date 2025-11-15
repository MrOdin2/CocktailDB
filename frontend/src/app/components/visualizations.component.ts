import { Component, OnInit, AfterViewInit, ViewChild, ElementRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Chart, ChartConfiguration, registerables } from 'chart.js';
import { ApiService } from '../services/api.service';
import { Cocktail, Ingredient, IngredientType } from '../models/models';

// Register Chart.js components
Chart.register(...registerables);

@Component({
  selector: 'app-visualizations',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './visualizations.component.html',
  styleUrls: ['./visualizations.component.css']
})
export class VisualizationsComponent implements OnInit, AfterViewInit {
  @ViewChild('ingredientTypeChart') ingredientTypeChartRef!: ElementRef<HTMLCanvasElement>;
  @ViewChild('stockStatusChart') stockStatusChartRef!: ElementRef<HTMLCanvasElement>;
  
  ingredients: Ingredient[] = [];
  cocktails: Cocktail[] = [];
  
  ingredientTypeChart?: Chart;
  stockStatusChart?: Chart;
  
  isLoading = true;
  errorMessage = '';

  constructor(private apiService: ApiService) {}

  ngOnInit(): void {
    this.loadData();
  }

  ngAfterViewInit(): void {
    // Charts will be initialized after data is loaded
  }

  loadData(): void {
    this.isLoading = true;
    this.errorMessage = '';
    
    Promise.all([
      this.apiService.getAllIngredients().toPromise(),
      this.apiService.getAllCocktails().toPromise()
    ]).then(([ingredients, cocktails]) => {
      this.ingredients = ingredients || [];
      this.cocktails = cocktails || [];
      this.isLoading = false;
      this.initializeCharts();
    }).catch(error => {
      console.error('Error loading data:', error);
      this.errorMessage = 'Failed to load data for visualizations';
      this.isLoading = false;
    });
  }

  initializeCharts(): void {
    this.createIngredientTypeChart();
    this.createStockStatusChart();
  }

  createIngredientTypeChart(): void {
    if (!this.ingredientTypeChartRef) return;

    const ctx = this.ingredientTypeChartRef.nativeElement.getContext('2d');
    if (!ctx) return;

    // Count ingredients by type
    const typeCounts = new Map<string, number>();
    this.ingredients.forEach(ingredient => {
      const type = ingredient.type || 'OTHER';
      typeCounts.set(type, (typeCounts.get(type) || 0) + 1);
    });

    const config: ChartConfiguration = {
      type: 'bar',
      data: {
        labels: Array.from(typeCounts.keys()),
        datasets: [{
          label: 'Number of Ingredients',
          data: Array.from(typeCounts.values()),
          backgroundColor: [
            'rgba(255, 99, 132, 0.7)',
            'rgba(54, 162, 235, 0.7)',
            'rgba(255, 206, 86, 0.7)',
            'rgba(75, 192, 192, 0.7)',
            'rgba(153, 102, 255, 0.7)',
            'rgba(255, 159, 64, 0.7)',
            'rgba(199, 199, 199, 0.7)',
            'rgba(83, 102, 255, 0.7)',
            'rgba(255, 99, 255, 0.7)',
            'rgba(99, 255, 132, 0.7)'
          ],
          borderColor: [
            'rgba(255, 99, 132, 1)',
            'rgba(54, 162, 235, 1)',
            'rgba(255, 206, 86, 1)',
            'rgba(75, 192, 192, 1)',
            'rgba(153, 102, 255, 1)',
            'rgba(255, 159, 64, 1)',
            'rgba(199, 199, 199, 1)',
            'rgba(83, 102, 255, 1)',
            'rgba(255, 99, 255, 1)',
            'rgba(99, 255, 132, 1)'
          ],
          borderWidth: 1
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          title: {
            display: true,
            text: 'Ingredients by Type'
          },
          legend: {
            display: false
          }
        },
        scales: {
          y: {
            beginAtZero: true,
            ticks: {
              stepSize: 1
            }
          }
        }
      }
    };

    this.ingredientTypeChart = new Chart(ctx, config);
  }

  createStockStatusChart(): void {
    if (!this.stockStatusChartRef) return;

    const ctx = this.stockStatusChartRef.nativeElement.getContext('2d');
    if (!ctx) return;

    // Count ingredients by stock status
    const inStock = this.ingredients.filter(i => i.inStock).length;
    const outOfStock = this.ingredients.filter(i => !i.inStock).length;

    const config: ChartConfiguration = {
      type: 'doughnut',
      data: {
        labels: ['In Stock', 'Out of Stock'],
        datasets: [{
          data: [inStock, outOfStock],
          backgroundColor: [
            'rgba(75, 192, 192, 0.7)',
            'rgba(255, 99, 132, 0.7)'
          ],
          borderColor: [
            'rgba(75, 192, 192, 1)',
            'rgba(255, 99, 132, 1)'
          ],
          borderWidth: 1
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          title: {
            display: true,
            text: 'Stock Status'
          },
          legend: {
            position: 'bottom'
          }
        }
      }
    };

    this.stockStatusChart = new Chart(ctx, config);
  }

  refreshCharts(): void {
    this.loadData();
  }

  // Destroy charts when component is destroyed to prevent memory leaks
  ngOnDestroy(): void {
    if (this.ingredientTypeChart) {
      this.ingredientTypeChart.destroy();
    }
    if (this.stockStatusChart) {
      this.stockStatusChart.destroy();
    }
  }
}
