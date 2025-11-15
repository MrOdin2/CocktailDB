import { Component, OnInit, ViewChild, ElementRef, AfterViewInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Chart, ChartConfiguration, registerables } from 'chart.js';
import * as d3 from 'd3';
import { Ingredient, IngredientType, Cocktail } from '../models/models';
import { ApiService } from '../services/api.service';
import { ModalComponent } from './modal.component';

// Register Chart.js components
Chart.register(...registerables);

// D3 types for force-directed graph
interface GraphNode extends d3.SimulationNodeDatum {
  id: number;
  name: string;
  type: string;
  group: number;
  radius: number;
  usageCount?: number;
}

interface GraphLink extends d3.SimulationLinkDatum<GraphNode> {
  source: number | GraphNode;
  target: number | GraphNode;
  value: number;
}

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
  @ViewChild('heatmapContainer') heatmapContainerRef!: ElementRef<HTMLDivElement>;
  @ViewChild('forceGraphContainer') forceGraphContainerRef!: ElementRef<HTMLDivElement>;
  
  private usageChart?: Chart;
  private typeChart?: Chart;

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
    this.createHeatmap();
    this.createForceDirectedGraph();
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

  private createHeatmap(): void {
    if (!this.heatmapContainerRef) return;

    const container = this.heatmapContainerRef.nativeElement;
    
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

  private createForceDirectedGraph(): void {
    if (!this.forceGraphContainerRef) return;

    const container = this.forceGraphContainerRef.nativeElement;
    
    // Clear any existing content
    d3.select(container).selectAll('*').remove();

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
    const svg = d3.select(container)
      .append('svg')
      .attr('width', width)
      .attr('height', height)
      .attr('viewBox', `0 0 ${width} ${height}`);

    // Color scale for node groups
    const color = d3.scaleOrdinal(d3.schemeCategory10);

    // Create force simulation
    const simulation = d3.forceSimulation<GraphNode>(nodes)
      .force('link', d3.forceLink<GraphNode, GraphLink>(links)
        .id(d => d.id)
        .distance(d => 100 - (d.value * 10)))
      .force('charge', d3.forceManyBody().strength(-300))
      .force('center', d3.forceCenter(width / 2, height / 2))
      .force('collision', d3.forceCollide<GraphNode>().radius(d => d.radius + 2));

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
      .call(d3.drag<SVGGElement, GraphNode>()
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
