import { Component, OnInit, ViewChild, ElementRef, AfterViewInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Chart, ChartConfiguration, registerables } from 'chart.js';
import { Ingredient, IngredientType, Cocktail } from '../models/models';
import { ApiService } from '../services/api.service';
import { ModalComponent } from './modal.component';

// Register Chart.js components
Chart.register(...registerables);

@Component({
    selector: 'app-ingredients',
    imports: [CommonModule, FormsModule, ModalComponent],
    templateUrl: './ingredients.component.html',
    styleUrls: ['./ingredients.component.css']
})
export class IngredientsComponent implements OnInit, AfterViewInit {
  ingredients: Ingredient[] = [];
  cocktails: Cocktail[] = [];
  newIngredient: Ingredient = {
    name: '',
    type: IngredientType.SPIRIT,
    abv: 0,
    inStock: false
  };
  ingredientTypes = Object.values(IngredientType);
  editingIngredient: Ingredient | null = null;
  isModalOpen = false;
  
  // Tab control
  activeTab: 'data' | 'visualizations' = 'data';
  
  // Filter properties
  nameFilter = '';
  typeFilter = '';
  
  // Sort properties
  sortBy: 'name' | 'type' | 'abv' = 'name';
  sortDirection: 'asc' | 'desc' = 'asc';

  // Chart references
  @ViewChild('usageChart') usageChartRef!: ElementRef<HTMLCanvasElement>;
  @ViewChild('typeChart') typeChartRef!: ElementRef<HTMLCanvasElement>;
  @ViewChild('pairingChart') pairingChartRef!: ElementRef<HTMLCanvasElement>;
  
  private usageChart?: Chart;
  private typeChart?: Chart;
  private pairingChart?: Chart;

  constructor(private apiService: ApiService) {}

  ngOnInit(): void {
    this.loadIngredients();
    this.loadCocktails();
  }

  ngAfterViewInit(): void {
    // Charts will be initialized when switching to visualizations tab
  }

  loadIngredients(): void {
    this.apiService.getAllIngredients().subscribe({
      next: (data: Ingredient[]) => {
        this.ingredients = data;
        if (this.activeTab === 'visualizations') {
          this.initializeCharts();
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
        if (this.activeTab === 'visualizations') {
          this.initializeCharts();
        }
      },
      error: (error: any) => {
        console.error('Error loading cocktails:', error);
      }
    });
  }

  switchTab(tab: 'data' | 'visualizations'): void {
    this.activeTab = tab;
    if (tab === 'visualizations') {
      // Delay chart initialization to ensure canvas elements are rendered
      setTimeout(() => this.initializeCharts(), 100);
    }
  }
  
  get displayedIngredients(): Ingredient[] {
    let filtered = this.ingredients;
    
    // Apply filters
    if (this.nameFilter) {
      filtered = filtered.filter(ingredient => 
        ingredient.name.toLowerCase().includes(this.nameFilter.toLowerCase())
      );
    }
    
    if (this.typeFilter) {
      filtered = filtered.filter(ingredient => 
        ingredient.type === this.typeFilter
      );
    }
    
    // Apply sorting
    return filtered.sort((a, b) => {
      let comparison = 0;
      
      switch (this.sortBy) {
        case 'name':
          comparison = a.name.localeCompare(b.name);
          break;
        case 'type':
          comparison = a.type.localeCompare(b.type);
          break;
        case 'abv':
          comparison = a.abv - b.abv;
          break;
      }
      
      return this.sortDirection === 'asc' ? comparison : -comparison;
    });
  }
  
  setSortBy(field: 'name' | 'type' | 'abv'): void {
    if (this.sortBy === field) {
      // Toggle direction if clicking the same field
      this.sortDirection = this.sortDirection === 'asc' ? 'desc' : 'asc';
    } else {
      // Set new field and default to ascending
      this.sortBy = field;
      this.sortDirection = 'asc';
    }
  }
  
  clearFilters(): void {
    this.nameFilter = '';
    this.typeFilter = '';
  }

  openModal(): void {
    this.isModalOpen = true;
  }

  closeModal(): void {
    this.isModalOpen = false;
    this.resetNewIngredient();
  }

  createIngredient(): void {
    this.apiService.createIngredient(this.newIngredient).subscribe({
      next: () => {
        this.loadIngredients();
        this.closeModal();
      },
      error: (error: any) => {
        console.error('Error creating ingredient:', error);
      }
    });
  }

  updateIngredient(ingredient: Ingredient): void {
    if (ingredient.id) {
      this.apiService.updateIngredient(ingredient.id, ingredient).subscribe({
        next: () => {
          this.loadIngredients();
          this.editingIngredient = null;
        },
        error: (error: any) => {
          console.error('Error updating ingredient:', error);
        }
      });
    }
  }

  deleteIngredient(id: number | undefined): void {
    if (id && confirm('Are you sure you want to delete this ingredient?')) {
      this.apiService.deleteIngredient(id).subscribe({
        next: () => {
          this.loadIngredients();
        },
        error: (error: any) => {
          console.error('Error deleting ingredient:', error);
        }
      });
    }
  }

  toggleStock(ingredient: Ingredient): void {
    ingredient.inStock = !ingredient.inStock;
    this.updateIngredient(ingredient);
  }

  startEdit(ingredient: Ingredient): void {
    this.editingIngredient = { ...ingredient };
  }

  cancelEdit(): void {
    this.editingIngredient = null;
  }

  saveEdit(): void {
    if (this.editingIngredient) {
      this.updateIngredient(this.editingIngredient);
    }
  }

  resetNewIngredient(): void {
    this.newIngredient = {
      name: '',
      type: IngredientType.SPIRIT,
      abv: 0,
      inStock: false
    };
  }

  private initializeCharts(): void {
    if (this.ingredients.length === 0 || this.cocktails.length === 0) {
      return;
    }

    this.createUsageChart();
    this.createTypeDistributionChart();
    this.createIngredientPairingChart();
  }

  private createUsageChart(): void {
    if (!this.usageChartRef) return;

    // Destroy existing chart
    if (this.usageChart) {
      this.usageChart.destroy();
    }

    // Calculate ingredient usage frequency
    const usageMap = new Map<number, number>();
    this.cocktails.forEach(cocktail => {
      cocktail.ingredients.forEach(ing => {
        usageMap.set(ing.ingredientId, (usageMap.get(ing.ingredientId) || 0) + 1);
      });
    });

    // Get top 15 most used ingredients
    const sortedUsage = Array.from(usageMap.entries())
      .sort((a, b) => b[1] - a[1])
      .slice(0, 15);

    const labels = sortedUsage.map(([id]) => {
      const ingredient = this.ingredients.find(i => i.id === id);
      return ingredient?.name || 'Unknown';
    });

    const data = sortedUsage.map(([, count]) => count);

    const config: ChartConfiguration = {
      type: 'bar',
      data: {
        labels: labels,
        datasets: [{
          label: 'Times Used in Cocktails',
          data: data,
          backgroundColor: 'rgba(54, 162, 235, 0.6)',
          borderColor: 'rgba(54, 162, 235, 1)',
          borderWidth: 1
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: {
            display: true,
            position: 'top'
          },
          title: {
            display: true,
            text: 'Most Used Ingredients',
            font: {
              size: 16
            }
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

    this.usageChart = new Chart(this.usageChartRef.nativeElement, config);
  }

  private createTypeDistributionChart(): void {
    if (!this.typeChartRef) return;

    // Destroy existing chart
    if (this.typeChart) {
      this.typeChart.destroy();
    }

    // Calculate ingredient type distribution
    const typeMap = new Map<string, number>();
    this.ingredients.forEach(ingredient => {
      typeMap.set(ingredient.type, (typeMap.get(ingredient.type) || 0) + 1);
    });

    const labels = Array.from(typeMap.keys());
    const data = Array.from(typeMap.values());

    // Colors for different types
    const colors = [
      'rgba(255, 99, 132, 0.6)',
      'rgba(54, 162, 235, 0.6)',
      'rgba(255, 206, 86, 0.6)',
      'rgba(75, 192, 192, 0.6)',
      'rgba(153, 102, 255, 0.6)',
      'rgba(255, 159, 64, 0.6)',
      'rgba(199, 199, 199, 0.6)',
      'rgba(83, 102, 255, 0.6)',
      'rgba(255, 102, 178, 0.6)',
      'rgba(102, 255, 178, 0.6)'
    ];

    const config: ChartConfiguration = {
      type: 'doughnut',
      data: {
        labels: labels,
        datasets: [{
          label: 'Count',
          data: data,
          backgroundColor: colors.slice(0, labels.length),
          borderColor: colors.slice(0, labels.length).map(c => c.replace('0.6', '1')),
          borderWidth: 1
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: {
            display: true,
            position: 'right'
          },
          title: {
            display: true,
            text: 'Ingredient Distribution by Type',
            font: {
              size: 16
            }
          }
        }
      }
    };

    this.typeChart = new Chart(this.typeChartRef.nativeElement, config);
  }

  private createIngredientPairingChart(): void {
    if (!this.pairingChartRef) return;

    // Destroy existing chart
    if (this.pairingChart) {
      this.pairingChart.destroy();
    }

    // Calculate ingredient co-occurrences
    const pairingMap = new Map<string, number>();

    this.cocktails.forEach(cocktail => {
      const ingredientIds = cocktail.ingredients.map(i => i.ingredientId);
      
      // Find all pairs in this cocktail
      for (let i = 0; i < ingredientIds.length; i++) {
        for (let j = i + 1; j < ingredientIds.length; j++) {
          const id1 = ingredientIds[i];
          const id2 = ingredientIds[j];
          
          // Create a consistent key for the pair
          const key = id1 < id2 ? `${id1}-${id2}` : `${id2}-${id1}`;
          pairingMap.set(key, (pairingMap.get(key) || 0) + 1);
        }
      }
    });

    // Get top 10 most common pairings
    const sortedPairings = Array.from(pairingMap.entries())
      .sort((a, b) => b[1] - a[1])
      .slice(0, 10);

    const labels = sortedPairings.map(([key]) => {
      const [id1, id2] = key.split('-').map(Number);
      const ing1 = this.ingredients.find(i => i.id === id1)?.name || 'Unknown';
      const ing2 = this.ingredients.find(i => i.id === id2)?.name || 'Unknown';
      return `${ing1} + ${ing2}`;
    });

    const data = sortedPairings.map(([, count]) => count);

    const config: ChartConfiguration = {
      type: 'bar',
      data: {
        labels: labels,
        datasets: [{
          label: 'Times Used Together',
          data: data,
          backgroundColor: 'rgba(75, 192, 192, 0.6)',
          borderColor: 'rgba(75, 192, 192, 1)',
          borderWidth: 1
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        indexAxis: 'y',
        plugins: {
          legend: {
            display: true,
            position: 'top'
          },
          title: {
            display: true,
            text: 'Most Common Ingredient Pairings',
            font: {
              size: 16
            }
          }
        },
        scales: {
          x: {
            beginAtZero: true,
            ticks: {
              stepSize: 1
            }
          }
        }
      }
    };

    this.pairingChart = new Chart(this.pairingChartRef.nativeElement, config);
  }
}
