import { Routes } from '@angular/router';
import { IngredientsComponent } from './components/ingredients.component';
import { CocktailsComponent } from './components/cocktails.component';
import { VisualizationComponent } from './components/visualization.component';
import { SettingsComponent } from './components/settings.component';

export const routes: Routes = [
  { path: '', redirectTo: '/cocktails', pathMatch: 'full' },
  { path: 'ingredients', component: IngredientsComponent },
  { path: 'cocktails', component: CocktailsComponent },
  { path: 'visualization', component: VisualizationComponent },
  { path: 'settings', component: SettingsComponent }
];
