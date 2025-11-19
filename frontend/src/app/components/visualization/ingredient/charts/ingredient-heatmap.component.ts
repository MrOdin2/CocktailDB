import { Component, Input, ViewChild, ElementRef, OnChanges, SimpleChanges, AfterViewInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import * as d3 from 'd3';
import { Ingredient, Cocktail } from '../../../../models/models';

@Component({
  selector: 'app-ingredient-heatmap',
  imports: [CommonModule],
  template: `<div #heatmapContainer class="heatmap-container"></div>`,
  styles: [`
    :host {
      display: block;
      width: 100%;
      height: 100%;
    }
    .heatmap-container {
      width: 100%;
      min-height: 600px;
      position: relative;
      overflow: visible;
    }
  `]
})
export class IngredientHeatmapComponent implements AfterViewInit, OnChanges {
  @Input() ingredients: Ingredient[] = [];
  @Input() cocktails: Cocktail[] = [];
  @ViewChild('heatmapContainer') containerRef!: ElementRef<HTMLDivElement>;

  ngAfterViewInit(): void {
    this.createHeatmap();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if ((changes['ingredients'] || changes['cocktails']) && this.containerRef) {
      this.createHeatmap();
    }
  }

  private createHeatmap(): void {
    if (!this.containerRef || this.ingredients.length === 0 || this.cocktails.length === 0) {
      return;
    }

    const container = this.containerRef.nativeElement;
    
    // Clear any existing content
    d3.select(container).selectAll('*').remove();

    // Calculate ingredient co-occurrences
    const pairingMap = new Map<string, number>();
    const ingredientIds = new Set<number>();

    this.cocktails.forEach(cocktail => {
      const ids = cocktail.ingredients.map(i => i.ingredientId);
      ids.forEach(id => ingredientIds.add(id));
      
      // Find all pairs in this cocktail
      for (let i = 0; i < ids.length; i++) {
        for (let j = i + 1; j < ids.length; j++) {
          const id1 = ids[i];
          const id2 = ids[j];
          
          // Create a consistent key for the pair
          const key = id1 < id2 ? `${id1}-${id2}` : `${id2}-${id1}`;
          pairingMap.set(key, (pairingMap.get(key) || 0) + 1);
        }
      }
    });

    // Get top 15 most used ingredients for the heatmap
    const usageMap = new Map<number, number>();
    this.cocktails.forEach(cocktail => {
      cocktail.ingredients.forEach(ing => {
        usageMap.set(ing.ingredientId, (usageMap.get(ing.ingredientId) || 0) + 1);
      });
    });

    const topIngredients = Array.from(usageMap.entries())
      .sort((a, b) => b[1] - a[1])
      .slice(0, 15)
      .map(([id]) => id);

    const ingredientNames = topIngredients.map(id => 
      this.ingredients.find(i => i.id === id)?.name || 'Unknown'
    );

    // Create heatmap data matrix
    const heatmapData: { x: string; y: string; value: number }[] = [];
    
    for (let i = 0; i < topIngredients.length; i++) {
      for (let j = 0; j < topIngredients.length; j++) {
        if (i === j) {
          heatmapData.push({
            x: ingredientNames[j],
            y: ingredientNames[i],
            value: 0
          });
        } else {
          const id1 = topIngredients[i];
          const id2 = topIngredients[j];
          const key = id1 < id2 ? `${id1}-${id2}` : `${id2}-${id1}`;
          const value = pairingMap.get(key) || 0;
          heatmapData.push({
            x: ingredientNames[j],
            y: ingredientNames[i],
            value: value
          });
        }
      }
    }

    // Setup dimensions
    const margin = { top: 100, right: 50, bottom: 150, left: 150 };
    const cellSize = 35;
    const width = cellSize * topIngredients.length + margin.left + margin.right;
    const height = cellSize * topIngredients.length + margin.top + margin.bottom;

    // Create SVG
    const svg = d3.select(container)
      .append('svg')
      .attr('width', width)
      .attr('height', height)
      .append('g')
      .attr('transform', `translate(${margin.left},${margin.top})`);

    // Create scales
    const x = d3.scaleBand()
      .range([0, cellSize * topIngredients.length])
      .domain(ingredientNames)
      .padding(0.05);

    const y = d3.scaleBand()
      .range([0, cellSize * topIngredients.length])
      .domain(ingredientNames)
      .padding(0.05);

    const maxValue = d3.max(heatmapData, d => d.value) || 1;
    const colorScale = d3.scaleSequential(d3.interpolateBlues)
      .domain([0, maxValue]);

    // Add X axis
    svg.append('g')
      .attr('transform', `translate(0,0)`)
      .call(d3.axisTop(x).tickSize(0))
      .selectAll('text')
      .style('text-anchor', 'start')
      .attr('dx', '0.5em')
      .attr('dy', '-0.5em')
      .attr('transform', 'rotate(-45)')
      .style('font-size', '12px');

    // Add Y axis
    svg.append('g')
      .call(d3.axisLeft(y).tickSize(0))
      .selectAll('text')
      .style('font-size', '12px');

    // Create tooltip
    const tooltip = d3.select(container)
      .append('div')
      .style('position', 'absolute')
      .style('visibility', 'hidden')
      .style('background-color', 'rgba(0, 0, 0, 0.8)')
      .style('color', 'white')
      .style('padding', '8px')
      .style('border-radius', '4px')
      .style('font-size', '12px')
      .style('pointer-events', 'none')
      .style('z-index', '1000');

    // Add cells
    svg.selectAll()
      .data(heatmapData)
      .enter()
      .append('rect')
      .attr('x', d => x(d.x) || 0)
      .attr('y', d => y(d.y) || 0)
      .attr('width', x.bandwidth())
      .attr('height', y.bandwidth())
      .style('fill', d => d.value === 0 ? '#f0f0f0' : colorScale(d.value))
      .style('stroke', '#fff')
      .style('stroke-width', 1)
      .on('mouseover', function(event, d) {
        if (d.value > 0) {
          tooltip
            .style('visibility', 'visible')
            .html(`<strong>${d.y} + ${d.x}</strong><br/>Used together: ${d.value} times`);
        }
      })
      .on('mousemove', function(event) {
        tooltip
          .style('top', (event.pageY - 10) + 'px')
          .style('left', (event.pageX + 10) + 'px');
      })
      .on('mouseout', function() {
        tooltip.style('visibility', 'hidden');
      });

    // Add title
    svg.append('text')
      .attr('x', (cellSize * topIngredients.length) / 2)
      .attr('y', -60)
      .attr('text-anchor', 'middle')
      .style('font-size', '16px')
      .style('font-weight', 'bold')
      .text('Ingredient Combination Heatmap');
  }
}
