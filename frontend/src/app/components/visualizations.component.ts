import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { IngredientVisualizationsComponent } from './ingredient-visualizations.component';

@Component({
  selector: 'app-visualizations',
  imports: [CommonModule, IngredientVisualizationsComponent],
  templateUrl: './visualizations.component.html',
  styleUrls: ['./visualizations.component.css']
})
export class VisualizationsComponent {
  activeTab: 'ingredients' | 'cocktails' = 'ingredients';

  switchTab(tab: 'ingredients' | 'cocktails'): void {
    this.activeTab = tab;
  }
}
