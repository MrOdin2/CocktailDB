import { Routes } from '@angular/router';
import { IngredientsComponent } from './components/ingredients/ingredients.component';
import { CocktailsComponent } from './components/cocktails/cocktails.component';
import { VisualizationComponent } from './components/visualization/visualization.component';
import { SettingsComponent } from './components/settings/settings.component';

export const routes: Routes = [
  { path: '', redirectTo: '/cocktails', pathMatch: 'full' },
  { path: 'ingredients', component: IngredientsComponent },
  { path: 'cocktails', component: CocktailsComponent },
  { path: 'visualizations', component: VisualizationComponent },
  { path: 'settings', component: SettingsComponent }
];
