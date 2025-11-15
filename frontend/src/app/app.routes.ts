import { Routes } from '@angular/router';
import { IngredientsComponent } from './components/ingredients.component';
import { CocktailsComponent } from './components/cocktails.component';
import { SettingsComponent } from './components/settings.component';
import { VisualizationsComponent } from './components/visualizations.component';

export const routes: Routes = [
  { path: '', redirectTo: '/cocktails', pathMatch: 'full' },
  { path: 'ingredients', component: IngredientsComponent },
  { path: 'cocktails', component: CocktailsComponent },
  { path: 'visualizations', component: VisualizationsComponent },
  { path: 'settings', component: SettingsComponent }
];
