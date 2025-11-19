import { Routes } from '@angular/router';
import { IngredientsComponent } from './components/ingredients/ingredients.component';
import { CocktailsComponent } from './components/cocktails/cocktails.component';
import { VisualizationComponent } from './components/visualization/visualization.component';
import { SettingsComponent } from './components/settings/settings.component';
import { LoginComponent } from './components/login/login.component';
import { authGuard } from './guards/auth.guard';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: '', redirectTo: '/cocktails', pathMatch: 'full' },
  { path: 'ingredients', component: IngredientsComponent, canActivate: [authGuard] },
  { path: 'cocktails', component: CocktailsComponent, canActivate: [authGuard] },
  { path: 'visualizations', component: VisualizationComponent, canActivate: [authGuard] },
  { path: 'settings', component: SettingsComponent, canActivate: [authGuard] }
];
