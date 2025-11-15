import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { IngredientStatisticsComponent } from './ingredient-statistics.component';
import { CocktailStatisticsComponent } from './cocktail-statistics.component';
import { TrendsAnalysisComponent } from './trends-analysis.component';

@Component({
  selector: 'app-visualization',
  imports: [
    CommonModule, 
    IngredientStatisticsComponent, 
    CocktailStatisticsComponent, 
    TrendsAnalysisComponent
  ],
  templateUrl: './visualization.component.html',
  styleUrls: ['./visualization.component.css']
})
export class VisualizationComponent {
  // Active tab
  activeTab: 'ingredients' | 'cocktails' | 'trends' = 'ingredients';

  setActiveTab(tab: 'ingredients' | 'cocktails' | 'trends'): void {
    this.activeTab = tab;
  }
}
