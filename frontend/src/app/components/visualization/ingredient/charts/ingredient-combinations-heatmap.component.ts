import { Component, Input, OnChanges, SimpleChanges, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import * as d3 from 'd3';
import { Ingredient, Cocktail } from '../../../../models/models';
import { ThemeService, Theme } from '../../../../services/theme.service';
import { Subscription } from 'rxjs';

interface IngredientPair {
  ingredient1: string;
  ingredient2: string;
  count: number;
}

@Component({
    selector: 'app-ingredient-combinations-heatmap',
    imports: [CommonModule],
    template: `
        <div class="heatmap-container" #heatmapContainer></div>`,
    standalone: true,
    styles: [`
        :host {
            display: block;
            width: 100%;
            height: 100%;
        }

        .heatmap-container {
            width: 100%;
            min-height: 400px;
            position: relative;
        }
    `]
})
export class IngredientCombinationsHeatmapComponent implements OnChanges, OnDestroy {
  @Input() ingredients: Ingredient[] = [];
  @Input() cocktails: Cocktail[] = [];

  private ingredientPairs: IngredientPair[] = [];
  private svg?: d3.Selection<SVGSVGElement, unknown, null, undefined>;
  private themeSubscription?: Subscription;

  constructor(private themeService: ThemeService) {
    // Subscribe to theme changes
    this.themeSubscription = this.themeService.currentTheme$.subscribe(() => {
      if (this.ingredientPairs.length > 0) {
        this.initializeHeatmap();
      }
    });
  }

  ngOnChanges(changes: SimpleChanges): void {
    if ((changes['ingredients'] || changes['cocktails']) && this.ingredients.length > 0 && this.cocktails.length > 0) {
      this.calculateIngredientPairs();
      setTimeout(() => this.initializeHeatmap(), 100);
    }
  }

  ngOnDestroy(): void {
    if (this.themeSubscription) {
      this.themeSubscription.unsubscribe();
    }
  }

  private calculateIngredientPairs(): void {
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

  private initializeHeatmap(): void {
    if (!this.ingredientPairs.length) {
      return;
    }

    // Query the element directly
    const element = document.querySelector('app-ingredient-combinations-heatmap .heatmap-container');
    if (!element) {
      return;
    }
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

    // Add labels with better contrast
    g.selectAll('text.pair-label')
      .data(this.ingredientPairs)
      .join('text')
      .attr('class', 'pair-label')
      .attr('y', (d, i) => (yScale(i.toString()) || 0) + yScale.bandwidth() / 2)
      .attr('x', -5)
      .attr('text-anchor', 'end')
      .attr('dominant-baseline', 'middle')
      .attr('font-size', '11px')
      .attr('font-weight', '500')
      .style('fill', this.getThemeTextColor())
      .style('paint-order', 'stroke')
      .style('stroke', this.getThemeBackgroundColor())
      .style('stroke-width', '3px')
      .style('stroke-linecap', 'butt')
      .style('stroke-linejoin', 'miter')
      .text(d => `${d.ingredient1} + ${d.ingredient2}`);

    // Add count labels on bars with better contrast
    g.selectAll('text.count-label')
      .data(this.ingredientPairs)
      .join('text')
      .attr('class', 'count-label')
      .attr('y', (d, i) => (yScale(i.toString()) || 0) + yScale.bandwidth() / 2)
      .attr('x', d => Math.max((d.count / maxCount) * innerWidth + 5, 20))
      .attr('text-anchor', 'start')
      .attr('dominant-baseline', 'middle')
      .attr('font-size', '11px')
      .attr('font-weight', 'bold')
      .style('fill', this.getThemeTextColor())
      .style('paint-order', 'stroke')
      .style('stroke', this.getThemeBackgroundColor())
      .style('stroke-width', '3px')
      .style('stroke-linecap', 'butt')
      .style('stroke-linejoin', 'miter')
      .text(d => d.count);

    // Add title with theme-aware colors
    this.svg.append('text')
      .attr('x', width / 2)
      .attr('y', 15)
      .attr('text-anchor', 'middle')
      .attr('font-size', '14px')
      .attr('font-weight', 'bold')
      .style('fill', this.getThemeTextColor())
      .text('Top Ingredient Combinations');
  }

  private getThemeTextColor(): string {
    const theme = this.themeService.getCurrentTheme();
    
    switch (theme) {
      case 'terminal-green':
        return '#00ff00';
      case 'cyberpunk':
        return '#00ffff';
      case 'amber':
        return '#ffb000';
      default:
        return '#333333';
    }
  }

  private getThemeBackgroundColor(): string {
    const theme = this.themeService.getCurrentTheme();
    
    switch (theme) {
      case 'terminal-green':
        return '#000000';
      case 'cyberpunk':
        return '#0a0e27';
      case 'amber':
        return '#000000';
      default:
        return '#ffffff';
    }
  }

  private getHeatmapColorInterpolator(): (t: number) => string {
    const theme = this.themeService.getCurrentTheme();
    
    switch (theme) {
      case 'terminal-green':
        return d3.interpolateGreens;
      case 'cyberpunk':
        return d3.interpolatePurples;
      case 'amber':
        return d3.interpolateOranges;
      default:
        return d3.interpolateBlues;
    }
  }
}
