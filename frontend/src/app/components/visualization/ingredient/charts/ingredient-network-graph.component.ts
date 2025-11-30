import { Component, Input, ViewChild, ElementRef, OnChanges, SimpleChanges, AfterViewInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { select } from 'd3-selection';
import { SimulationNodeDatum, SimulationLinkDatum, forceSimulation, forceLink, forceManyBody, forceCenter, forceCollide } from 'd3-force';
import { drag } from 'd3-drag';
import { scaleOrdinal } from 'd3-scale';
import { schemeCategory10 } from 'd3-scale-chromatic';
import { Ingredient, Cocktail } from '../../../../models/models';

// D3 types for force-directed graph
interface GraphNode extends SimulationNodeDatum {
  id: number;
  name: string;
  type: string;
  group: number;
  radius: number;
  usageCount?: number;
}

interface GraphLink extends SimulationLinkDatum<GraphNode> {
  source: number | GraphNode;
  target: number | GraphNode;
  value: number;
}

@Component({
    selector: 'app-ingredient-network-graph',
    imports: [CommonModule],
    template: `
        <div #graphContainer class="graph-container"></div>`,
    standalone: true,
    styles: [`
        :host {
            display: block;
            width: 100%;
            height: 100%;
        }

        .graph-container {
            width: 100%;
            min-height: 600px;
            position: relative;
            overflow: visible;
        }
    `]
})
export class IngredientNetworkGraphComponent implements AfterViewInit, OnChanges {
  @Input() ingredients: Ingredient[] = [];
  @Input() cocktails: Cocktail[] = [];
  @ViewChild('graphContainer') containerRef!: ElementRef<HTMLDivElement>;

  ngAfterViewInit(): void {
    this.createGraph();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if ((changes['ingredients'] || changes['cocktails']) && this.containerRef) {
      this.createGraph();
    }
  }

  private createGraph(): void {
    if (!this.containerRef || this.ingredients.length === 0 || this.cocktails.length === 0) {
      return;
    }

    const container = this.containerRef.nativeElement;
    
    // Clear any existing content
    select(container).selectAll('*').remove();

    // Calculate ingredient usage and co-occurrences
    const usageMap = new Map<number, number>();
    const pairingMap = new Map<string, number>();

    this.cocktails.forEach(cocktail => {
      const ids = cocktail.ingredients.map(i => i.ingredientId);
      ids.forEach(id => {
        usageMap.set(id, (usageMap.get(id) || 0) + 1);
      });
      
      // Find all pairs in this cocktail
      for (let i = 0; i < ids.length; i++) {
        for (let j = i + 1; j < ids.length; j++) {
          const id1 = ids[i];
          const id2 = ids[j];
          const key = id1 < id2 ? `${id1}-${id2}` : `${id2}-${id1}`;
          pairingMap.set(key, (pairingMap.get(key) || 0) + 1);
        }
      }
    });

    // Get top 20 most used ingredients
    const topIngredients = Array.from(usageMap.entries())
      .sort((a, b) => b[1] - a[1])
      .slice(0, 20);

    const topIngredientIds = new Set(topIngredients.map(([id]) => id));

    // Create type groups
    const typeGroups = new Map<string, number>();
    let groupCounter = 0;
    this.ingredients.forEach(ing => {
      if (!typeGroups.has(ing.type)) {
        typeGroups.set(ing.type, groupCounter++);
      }
    });

    // Create nodes
    const nodes: GraphNode[] = topIngredients.map(([id, count]) => {
      const ingredient = this.ingredients.find(i => i.id === id)!;
      return {
        id: id,
        name: ingredient.name,
        type: ingredient.type,
        group: typeGroups.get(ingredient.type) || 0,
        radius: 5 + (count * 2),
        usageCount: count
      };
    });

    // Create links (only for ingredients that have strong connections)
    const links: GraphLink[] = [];
    const minPairings = 2; // Minimum co-occurrences to show a link

    for (let i = 0; i < topIngredients.length; i++) {
      for (let j = i + 1; j < topIngredients.length; j++) {
        const id1 = topIngredients[i][0];
        const id2 = topIngredients[j][0];
        const key = id1 < id2 ? `${id1}-${id2}` : `${id2}-${id1}`;
        const value = pairingMap.get(key) || 0;
        
        if (value >= minPairings) {
          links.push({
            source: id1,
            target: id2,
            value: value
          });
        }
      }
    }

    // Setup dimensions
    const width = container.clientWidth || 800;
    const height = 600;

    // Create SVG
    const svg = select(container)
      .append('svg')
      .attr('width', width)
      .attr('height', height)
      .attr('viewBox', `0 0 ${width} ${height}`);

    // Color scale for node groups
    const color = scaleOrdinal(schemeCategory10);

    // Create force simulation
    const simulation = forceSimulation<GraphNode>(nodes)
      .force('link', forceLink<GraphNode, GraphLink>(links)
        .id(d => d.id)
        .distance(d => 100 - (d.value * 10)))
      .force('charge', forceManyBody().strength(-300))
      .force('center', forceCenter(width / 2, height / 2))
      .force('collision', forceCollide<GraphNode>().radius(d => d.radius + 2));

    // Create links
    const link = svg.append('g')
      .selectAll('line')
      .data(links)
      .enter()
      .append('line')
      .attr('stroke', '#999')
      .attr('stroke-opacity', 0.6)
      .attr('stroke-width', d => Math.sqrt(d.value));

    // Create nodes
    const node = svg.append('g')
      .selectAll('g')
      .data(nodes)
      .enter()
      .append('g')
      .call(drag<SVGGElement, GraphNode>()
        .on('start', dragstarted)
        .on('drag', dragged)
        .on('end', dragended) as any);

    // Add circles to nodes
    node.append('circle')
      .attr('r', d => d.radius)
      .attr('fill', d => color(d.group.toString()))
      .attr('stroke', '#fff')
      .attr('stroke-width', 2);

    // Add labels to nodes
    node.append('text')
      .text(d => d.name)
      .attr('x', 0)
      .attr('y', d => d.radius + 12)
      .attr('text-anchor', 'middle')
      .style('font-size', '10px')
      .style('font-weight', 'bold')
      .style('pointer-events', 'none');

    // Add title
    svg.append('text')
      .attr('x', width / 2)
      .attr('y', 30)
      .attr('text-anchor', 'middle')
      .style('font-size', '16px')
      .style('font-weight', 'bold')
      .text('Ingredient Relationship Network');

    // Add legend
    const legendData = Array.from(typeGroups.entries());
    const legend = svg.append('g')
      .attr('transform', `translate(20, 50)`);

    legend.selectAll('rect')
      .data(legendData)
      .enter()
      .append('rect')
      .attr('x', 0)
      .attr('y', (d, i) => i * 20)
      .attr('width', 12)
      .attr('height', 12)
      .attr('fill', d => color(d[1].toString()));

    legend.selectAll('text')
      .data(legendData)
      .enter()
      .append('text')
      .attr('x', 18)
      .attr('y', (d, i) => i * 20 + 10)
      .text(d => d[0])
      .style('font-size', '11px');

    // Create tooltip
    const tooltip = select(container)
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

    // Add tooltip interactions
    node.on('mouseover', function(event, d) {
        tooltip
          .style('visibility', 'visible')
          .html(`<strong>${d.name}</strong><br/>Type: ${d.type}<br/>Used in ${d.usageCount} cocktails`);
      })
      .on('mousemove', function(event) {
        tooltip
          .style('top', (event.pageY - 10) + 'px')
          .style('left', (event.pageX + 10) + 'px');
      })
      .on('mouseout', function() {
        tooltip.style('visibility', 'hidden');
      });

    // Update positions on tick
    simulation.on('tick', () => {
      link
        .attr('x1', d => (d.source as GraphNode).x || 0)
        .attr('y1', d => (d.source as GraphNode).y || 0)
        .attr('x2', d => (d.target as GraphNode).x || 0)
        .attr('y2', d => (d.target as GraphNode).y || 0);

      node.attr('transform', d => `translate(${d.x || 0},${d.y || 0})`);
    });

    // Drag functions
    function dragstarted(event: any, d: GraphNode) {
      if (!event.active) simulation.alphaTarget(0.3).restart();
      d.fx = d.x;
      d.fy = d.y;
    }

    function dragged(event: any, d: GraphNode) {
      d.fx = event.x;
      d.fy = event.y;
    }

    function dragended(event: any, d: GraphNode) {
      if (!event.active) simulation.alphaTarget(0);
      d.fx = null;
      d.fy = null;
    }
  }
}
