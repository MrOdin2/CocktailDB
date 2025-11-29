import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { IngredientVisualizationsComponent } from './visualization/more-vis/ingredient-visualizations.component';
import { TranslatePipe } from '../pipes/translate.pipe';

@Component({
  selector: 'app-visualizations',
  imports: [CommonModule, IngredientVisualizationsComponent, TranslatePipe],
  templateUrl: './visualizations.component.html',
  styleUrls: ['../admin-shared.css', './visualizations.component.css']
})
export class VisualizationsComponent {
  activeTab: 'ingredients' | 'cocktails' = 'ingredients';

  switchTab(tab: 'ingredients' | 'cocktails'): void {
    this.activeTab = tab;
  }
}
