import { Routes } from '@angular/router';
import { IngredientsComponent } from './components/ingredients.component';
import { CocktailsComponent } from './components/cocktails.component';

export const routes: Routes = [
  { path: '', redirectTo: '/cocktails', pathMatch: 'full' },
  { path: 'ingredients', component: IngredientsComponent },
  { path: 'cocktails', component: CocktailsComponent }
];
