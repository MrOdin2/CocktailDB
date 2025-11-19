import { Component, OnInit, OnDestroy, ElementRef, ViewChild, AfterViewInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Ingredient, Cocktail, IngredientType } from '../../../models/models';
import { ApiService } from '../../../services/api.service';
import { ThemeService, Theme } from '../../../services/theme.service';
import { Subscription } from 'rxjs';
import * as d3 from 'd3';

interface GraphNode extends d3.SimulationNodeDatum {
  id: string;
  name: string;
  group: string;
  ingredientCount: number;
  tags: string[];
  spirits: string[];
  x?: number;
  y?: number;
  fx?: number | null;
  fy?: number | null;
}

interface GraphLink extends d3.SimulationLinkDatum<GraphNode> {
  source: string | GraphNode;
  target: string | GraphNode;
  value: number;
  sharedIngredients: number;
}

@Component({
  selector: 'app-cocktail-statistics',
  imports: [CommonModule, FormsModule],
  templateUrl: './cocktail-statistics.component.html',
  styleUrls: ['./cocktail-statistics.component.css']
})
export class CocktailStatisticsComponent implements OnInit, OnDestroy, AfterViewInit {
  @ViewChild('graphContainer', { static: false }) graphContainer?: ElementRef;
  
  ingredients: Ingredient[] = [];
  cocktails: Cocktail[] = [];
  
  // Graph data
  graphNodes: GraphNode[] = [];
  graphLinks: GraphLink[] = [];
  
  // Graph controls
  groupBy: 'none' | 'spirit' | 'tags' | 'ingredient-count' = 'none';
  minSharedIngredients: number = 1;
  showLabels: boolean = true;
  
  // D3 simulation
  private simulation?: d3.Simulation<GraphNode, GraphLink>;
  private svg?: d3.Selection<SVGSVGElement, unknown, null, undefined>;
  private g?: d3.Selection<SVGGElement, unknown, null, undefined>;
  private zoom?: d3.ZoomBehavior<SVGSVGElement, unknown>;

  private themeSubscription?: Subscription;

  constructor(
    private apiService: ApiService,
    private themeService: ThemeService
  ) {}

  ngOnInit(): void {
    this.loadIngredients();
    this.loadCocktails();
    
    // Subscribe to theme changes
    this.themeSubscription = this.themeService.currentTheme$.subscribe(theme => {
      this.updateGraphColors();
    });
  }

  ngAfterViewInit(): void {
    // Graph will be initialized when data is loaded
    if (this.graphNodes.length > 0) {
      setTimeout(() => this.initializeGraph(), 100);
    }
  }

  ngOnDestroy(): void {
    if (this.themeSubscription) {
      this.themeSubscription.unsubscribe();
    }
    if (this.simulation) {
      this.simulation.stop();
    }
  }

  loadIngredients(): void {
    this.apiService.getAllIngredients().subscribe({
      next: (data: Ingredient[]) => {
        this.ingredients = data;
        if (this.cocktails.length > 0) {
          this.buildGraphData();
          this.tryInitializeGraph();
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
          this.buildGraphData();
          this.tryInitializeGraph();
        }
      },
      error: (error: any) => {
        console.error('Error loading cocktails:', error);
      }
    });
  }

  private tryInitializeGraph(): void {
    setTimeout(() => {
      if (this.graphContainer && this.graphNodes.length > 0 && !this.svg) {
        this.initializeGraph();
      }
    }, 100);
  }

  buildGraphData(): void {
    if (!this.cocktails.length || !this.ingredients.length) {
      return;
    }

    // Create nodes for each cocktail
    this.graphNodes = this.cocktails.map(cocktail => {
      const spirits = this.getSpiritsForCocktail(cocktail);
      return {
        id: cocktail.id!.toString(),
        name: cocktail.name,
        group: this.determineGroup(cocktail, spirits),
        ingredientCount: cocktail.ingredients.length,
        tags: cocktail.tags || [],
        spirits: spirits
      };
    });

    // Create links between cocktails that share ingredients
    this.graphLinks = [];
    for (let i = 0; i < this.cocktails.length; i++) {
      for (let j = i + 1; j < this.cocktails.length; j++) {
        const sharedCount = this.countSharedIngredients(
          this.cocktails[i],
          this.cocktails[j]
        );
        
        if (sharedCount >= this.minSharedIngredients) {
          this.graphLinks.push({
            source: this.cocktails[i].id!.toString(),
            target: this.cocktails[j].id!.toString(),
            value: sharedCount,
            sharedIngredients: sharedCount
          });
        }
      }
    }
  }

  private getSpiritsForCocktail(cocktail: Cocktail): string[] {
    const spirits: string[] = [];
    cocktail.ingredients.forEach(ci => {
      const ingredient = this.ingredients.find(i => i.id === ci.ingredientId);
      if (ingredient && ingredient.type === IngredientType.SPIRIT) {
        spirits.push(ingredient.name);
      }
    });
    return spirits;
  }

  private determineGroup(cocktail: Cocktail, spirits: string[]): string {
    switch (this.groupBy) {
      case 'spirit':
        return spirits.length > 0 ? spirits[0] : 'No Spirit';
      case 'tags':
        return cocktail.tags && cocktail.tags.length > 0 ? cocktail.tags[0] : 'Untagged';
      case 'ingredient-count':
        if (cocktail.ingredients.length <= 3) return 'Simple (1-3)';
        if (cocktail.ingredients.length <= 6) return 'Medium (4-6)';
        return 'Complex (7+)';
      default:
        return 'All Cocktails';
    }
  }

  private countSharedIngredients(c1: Cocktail, c2: Cocktail): number {
    const ingredients1 = new Set(c1.ingredients.map(ci => ci.ingredientId));
    const ingredients2 = new Set(c2.ingredients.map(ci => ci.ingredientId));
    
    let count = 0;
    ingredients1.forEach(id => {
      if (ingredients2.has(id)) {
        count++;
      }
    });
    return count;
  }

  onGroupByChange(): void {
    this.buildGraphData();
    if (this.svg) {
      this.renderGraph();
    }
  }

  onMinSharedChange(): void {
    this.buildGraphData();
    if (this.svg) {
      this.renderGraph();
    }
  }

  onLabelsToggle(): void {
    if (this.svg) {
      this.renderGraph();
    }
  }

  initializeGraph(): void {
    if (!this.graphContainer || !this.graphNodes.length) {
      return;
    }

    const element = this.graphContainer.nativeElement;
    const rect = element.getBoundingClientRect();
    const width = rect.width || 800;
    const height = 600;

    // Clear existing SVG
    d3.select(element).selectAll('*').remove();

    // Create SVG
    this.svg = d3.select(element)
      .append('svg')
      .attr('width', '100%')
      .attr('height', height)
      .attr('viewBox', [0, 0, width, height]);

    // Add zoom behavior
    this.zoom = d3.zoom<SVGSVGElement, unknown>()
      .scaleExtent([0.1, 4])
      .on('zoom', (event: d3.D3ZoomEvent<SVGSVGElement, unknown>) => {
        this.g!.attr('transform', event.transform.toString());
      });

    this.svg.call(this.zoom);

    // Create container group for zoomable content
    this.g = this.svg.append('g');

    this.renderGraph();
  }

  private renderGraph(): void {
    if (!this.g || !this.svg) {
      return;
    }

    const width = +(this.svg.attr('viewBox')?.split(',')[2] || 800);
    const height = +(this.svg.attr('viewBox')?.split(',')[3] || 600);

    // Clear existing elements
    this.g.selectAll('*').remove();

    // Create color scale based on groups
    const groups = Array.from(new Set(this.graphNodes.map(n => n.group)));
    const colorScale = d3.scaleOrdinal<string>()
      .domain(groups)
      .range(this.getGraphColors(groups.length));

    // Create force simulation
    this.simulation = d3.forceSimulation<GraphNode>(this.graphNodes)
      .force('link', d3.forceLink<GraphNode, GraphLink>(this.graphLinks)
        .id((d: GraphNode) => d.id)
        .distance((d: GraphLink) => 150 - d.value * 10))
      .force('charge', d3.forceManyBody().strength(-300))
      .force('center', d3.forceCenter(width / 2, height / 2))
      .force('collision', d3.forceCollide().radius(30));

    // Create links
    const link = this.g.append('g')
      .selectAll('line')
      .data(this.graphLinks)
      .join('line')
      .attr('stroke', 'var(--text-secondary, #999)')
      .attr('stroke-opacity', 0.6)
      .attr('stroke-width', (d: GraphLink) => Math.sqrt(d.value) * 2);

    // Create nodes
    const node = this.g.append('g')
      .selectAll('circle')
      .data(this.graphNodes)
      .join('circle')
      .attr('r', (d: GraphNode) => 8 + d.ingredientCount * 2)
      .attr('fill', (d: GraphNode) => colorScale(d.group))
      .attr('stroke', 'var(--card-bg, #fff)')
      .attr('stroke-width', 2)
      .style('cursor', 'pointer')
      .call(this.drag() as any);

    // Add hover effects
    node.on('mouseover', (event: any, d: GraphNode) => {
      d3.select(event.currentTarget)
        .attr('stroke-width', 4)
        .attr('stroke', 'var(--primary-color, #5a9)');
    }).on('mouseout', (event: any, d: GraphNode) => {
      d3.select(event.currentTarget)
        .attr('stroke-width', 2)
        .attr('stroke', 'var(--card-bg, #fff)');
    });

    // Add tooltips
    node.append('title')
      .text((d: GraphNode) => `${d.name}\nIngredients: ${d.ingredientCount}\n${d.spirits.length > 0 ? 'Spirits: ' + d.spirits.join(', ') : ''}\n${d.tags.length > 0 ? 'Tags: ' + d.tags.join(', ') : ''}`);

    // Add labels if enabled
    let labels: any;
    if (this.showLabels) {
      labels = this.g.append('g')
        .selectAll('text')
        .data(this.graphNodes)
        .join('text')
        .text((d: GraphNode) => d.name)
        .attr('font-size', '12px')
        .attr('dx', 15)
        .attr('dy', 4)
        .attr('fill', 'var(--text-color, #333)')
        .style('pointer-events', 'none');
    }

    // Update positions on simulation tick
    this.simulation.on('tick', () => {
      link
        .attr('x1', (d: GraphLink) => (d.source as GraphNode).x || 0)
        .attr('y1', (d: GraphLink) => (d.source as GraphNode).y || 0)
        .attr('x2', (d: GraphLink) => (d.target as GraphNode).x || 0)
        .attr('y2', (d: GraphLink) => (d.target as GraphNode).y || 0);

      node
        .attr('cx', (d: GraphNode) => d.x || 0)
        .attr('cy', (d: GraphNode) => d.y || 0);

      if (labels) {
        labels
          .attr('x', (d: GraphNode) => d.x || 0)
          .attr('y', (d: GraphNode) => d.y || 0);
      }
    });
  }

  private drag(): any {
    const self = this;
    
    function dragstarted(event: d3.D3DragEvent<Element, GraphNode, GraphNode>, d: GraphNode) {
      if (!event.active && self.simulation) self.simulation.alphaTarget(0.3).restart();
      d.fx = d.x;
      d.fy = d.y;
    }

    function dragged(event: d3.D3DragEvent<Element, GraphNode, GraphNode>, d: GraphNode) {
      d.fx = event.x;
      d.fy = event.y;
    }

    function dragended(event: d3.D3DragEvent<Element, GraphNode, GraphNode>, d: GraphNode) {
      if (!event.active && self.simulation) self.simulation.alphaTarget(0);
      d.fx = null;
      d.fy = null;
    }

    return d3.drag<Element, GraphNode>()
      .on('start', dragstarted)
      .on('drag', dragged)
      .on('end', dragended);
  }

  private getGraphColors(count: number): string[] {
    const theme = this.themeService.getCurrentTheme();
    
    switch (theme) {
      case 'terminal-green':
        return this.generateColorPalette('#00ff00', count);
      case 'cyberpunk':
        return this.generateColorPalette('#00ffff', count);
      case 'amber':
        return this.generateColorPalette('#ffb000', count);
      default:
        return this.generateColorPalette('#5a9', count);
    }
  }

  private generateColorPalette(baseColor: string, count: number): string[] {
    const colors: string[] = [];
    const hsl = d3.hsl(baseColor);
    
    for (let i = 0; i < count; i++) {
      const newHue = (hsl.h + (i * 360 / count)) % 360;
      colors.push(d3.hsl(newHue, hsl.s, hsl.l).toString());
    }
    
    return colors;
  }

  private updateGraphColors(): void {
    if (this.svg && this.graphNodes.length > 0) {
      this.renderGraph();
    }
  }
}
