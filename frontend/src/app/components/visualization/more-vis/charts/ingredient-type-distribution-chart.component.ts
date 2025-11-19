import { Component, Input, ViewChild, ElementRef, OnChanges, SimpleChanges, AfterViewInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Chart, ChartConfiguration, registerables } from 'chart.js';
import { Ingredient } from '../../../../models/models';

// Register Chart.js components
Chart.register(...registerables);

@Component({
  selector: 'app-ingredient-type-distribution-chart',
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
export class IngredientTypeDistributionChartComponent implements AfterViewInit, OnChanges {
  @Input() ingredients: Ingredient[] = [];
  @ViewChild('chartCanvas') chartRef!: ElementRef<HTMLCanvasElement>;
  
  private chart?: Chart;

  ngAfterViewInit(): void {
    this.createChart();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['ingredients'] && this.chartRef) {
      this.createChart();
    }
  }

  private createChart(): void {
    if (!this.chartRef || this.ingredients.length === 0) {
      return;
    }

    // Destroy existing chart
    if (this.chart) {
      this.chart.destroy();
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

    this.chart = new Chart(this.chartRef.nativeElement, config);
  }
}
