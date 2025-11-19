import { Component, Input, ViewChild, ElementRef, OnChanges, SimpleChanges, AfterViewInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Chart, ChartConfiguration, registerables } from 'chart.js';
import { Ingredient, Cocktail } from '../../../../models/models';

// Register Chart.js components
Chart.register(...registerables);

@Component({
  selector: 'app-most-used-ingredients-chart',
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
export class MostUsedIngredientsChartComponent implements AfterViewInit, OnChanges {
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

    this.chart = new Chart(this.chartRef.nativeElement, config);
  }
}
