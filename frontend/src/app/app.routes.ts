import { Routes } from '@angular/router';
import { IngredientsComponent } from './components/ingredients.component';
import { CocktailsComponent } from './components/cocktails.component';
import { SettingsComponent } from './components/settings.component';

export const routes: Routes = [
  { path: '', redirectTo: '/cocktails', pathMatch: 'full' },
  { path: 'ingredients', component: IngredientsComponent },
  { path: 'cocktails', component: CocktailsComponent },
  { path: 'settings', component: SettingsComponent }
];
