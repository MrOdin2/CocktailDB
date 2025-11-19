import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Ingredient, Cocktail } from '../../../models/models';
import { ApiService } from '../../../services/api.service';
import { MostUsedIngredientsChartComponent } from './charts/most-used-ingredients-chart.component';
import { IngredientTypeDistributionChartComponent } from './charts/ingredient-type-distribution-chart.component';
import { IngredientPairingChartComponent } from './charts/ingredient-pairing-chart.component';
import { IngredientHeatmapComponent } from './charts/ingredient-heatmap.component';
import { IngredientNetworkGraphComponent } from './charts/ingredient-network-graph.component';

@Component({
  selector: 'app-ingredient-visualizations',
  imports: [
    CommonModule,
    MostUsedIngredientsChartComponent,
    IngredientTypeDistributionChartComponent,
    IngredientPairingChartComponent,
    IngredientHeatmapComponent,
    IngredientNetworkGraphComponent
  ],
  templateUrl: './ingredient-visualizations.component.html',
  styleUrls: ['./ingredient-visualizations.component.css']
})
export class IngredientVisualizationsComponent implements OnInit {
  ingredients: Ingredient[] = [];
  cocktails: Cocktail[] = [];
  isLoading = true;

  constructor(private apiService: ApiService) {}

  ngOnInit(): void {
    this.loadData();
  }

  private loadData(): void {
    let ingredientsLoaded = false;
    let cocktailsLoaded = false;

    this.apiService.getAllIngredients().subscribe({
      next: (data: Ingredient[]) => {
        this.ingredients = data;
        ingredientsLoaded = true;
        if (cocktailsLoaded) {
          this.isLoading = false;
        }
      },
      error: (error: any) => {
        console.error('Error loading ingredients:', error);
        this.isLoading = false;
      }
    });

    this.apiService.getAllCocktails().subscribe({
      next: (data: Cocktail[]) => {
        this.cocktails = data;
        cocktailsLoaded = true;
        if (ingredientsLoaded) {
          this.isLoading = false;
        }
      },
      error: (error: any) => {
        console.error('Error loading cocktails:', error);
        this.isLoading = false;
      }
    });
  }
}
