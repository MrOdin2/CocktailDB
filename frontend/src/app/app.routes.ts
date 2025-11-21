import { Routes } from '@angular/router';
import { IngredientsComponent } from './components/ingredients/ingredients.component';
import { CocktailsComponent } from './components/cocktails/cocktails.component';
import { VisualizationComponent } from './components/visualization/visualization.component';
import { SettingsComponent } from './components/settings/settings.component';
import { LoginComponent } from './components/login/login.component';
import { BarkeeperMenuComponent } from './components/barkeeper/menu/barkeeper-menu.component';
import { BarkeeperAlphabetComponent } from './components/barkeeper/alphabet/barkeeper-alphabet.component';
import { BarkeeperCocktailListComponent } from './components/barkeeper/cocktail-list/barkeeper-cocktail-list.component';
import { BarkeeperRecipeComponent } from './components/barkeeper/recipe/barkeeper-recipe.component';
import { BarkeeperRandomPickerComponent } from './components/barkeeper/random-picker/barkeeper-random-picker.component';
import { BarkeeperStockManagementComponent } from './components/barkeeper/stock-management/barkeeper-stock-management.component';
import { adminGuard } from './guards/admin.guard';
import { barkeeperGuard } from './guards/barkeeper.guard';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: '', redirectTo: '/login', pathMatch: 'full' },
  { path: 'barkeeper/menu', component: BarkeeperMenuComponent, canActivate: [barkeeperGuard] },
  { path: 'barkeeper/alphabet', component: BarkeeperAlphabetComponent, canActivate: [barkeeperGuard] },
  { path: 'barkeeper/cocktails', component: BarkeeperCocktailListComponent, canActivate: [barkeeperGuard] },
  { path: 'barkeeper/recipe/:id', component: BarkeeperRecipeComponent, canActivate: [barkeeperGuard] },
  { path: 'barkeeper/random', component: BarkeeperRandomPickerComponent, canActivate: [barkeeperGuard] },
  { path: 'barkeeper/stock', component: BarkeeperStockManagementComponent, canActivate: [barkeeperGuard] },
  { path: 'barkeeper', redirectTo: '/barkeeper/menu', pathMatch: 'full' },
  { path: 'ingredients', component: IngredientsComponent, canActivate: [adminGuard] },
  { path: 'cocktails', component: CocktailsComponent, canActivate: [adminGuard] },
  { path: 'visualizations', component: VisualizationComponent, canActivate: [adminGuard] },
  { path: 'settings', component: SettingsComponent, canActivate: [adminGuard] }
];
