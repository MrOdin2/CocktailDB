import { Component, Input, ViewChild, ElementRef, OnChanges, SimpleChanges, AfterViewInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Chart, ChartConfiguration, registerables } from 'chart.js';
import { Ingredient, Cocktail } from '../../../../models/models';

// Register Chart.js components
Chart.register(...registerables);

@Component({
  selector: 'app-ingredient-pairing-chart',
  imports: [CommonModule],
  template: `<canvas #chartCanvas></canvas>`,
  styles: [`
    :host {
      display: block;
      width: 100%;
      height: 100%;
    }
    canvas {
      max-height: 400px;
    }
  `]
})
export class IngredientPairingChartComponent implements AfterViewInit, OnChanges {
  @Input() ingredients: Ingredient[] = [];
  @Input() cocktails: Cocktail[] = [];
  @ViewChild('chartCanvas') chartRef!: ElementRef<HTMLCanvasElement>;
  
  private chart?: Chart;

  ngAfterViewInit(): void {
    this.createChart();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if ((changes['ingredients'] || changes['cocktails']) && this.chartRef) {
      this.createChart();
    }
  }

  private createChart(): void {
    if (!this.chartRef || this.ingredients.length === 0 || this.cocktails.length === 0) {
      return;
    }

    // Destroy existing chart
    if (this.chart) {
      this.chart.destroy();
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

    this.chart = new Chart(this.chartRef.nativeElement, config);
  }
}
