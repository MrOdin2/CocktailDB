import { Routes } from '@angular/router';
import { IngredientsComponent } from './components/ingredients/ingredients.component';
import { CocktailsComponent } from './components/cocktails/cocktails.component';
import { VisualizationComponent } from './components/visualization/visualization.component';
import { SettingsComponent } from './components/settings/settings.component';
import { LoginComponent } from './components/login/login.component';
import { BarkeeperComponent } from './components/barkeeper/barkeeper.component';
import { adminGuard } from './guards/admin.guard';
import { barkeeperGuard } from './guards/barkeeper.guard';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: '', redirectTo: '/login', pathMatch: 'full' },
  { path: 'barkeeper', component: BarkeeperComponent, canActivate: [barkeeperGuard] },
  { path: 'ingredients', component: IngredientsComponent, canActivate: [adminGuard] },
  { path: 'cocktails', component: CocktailsComponent, canActivate: [adminGuard] },
  { path: 'visualizations', component: VisualizationComponent, canActivate: [adminGuard] },
  { path: 'settings', component: SettingsComponent, canActivate: [adminGuard] }
];
