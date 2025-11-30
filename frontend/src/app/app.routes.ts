import { Routes } from '@angular/router';
import { VisitorMenuComponent } from './components/visitor/menu/visitor-menu.component';
import { VisitorCocktailListComponent } from './components/visitor/cocktail-list/visitor-cocktail-list.component';
import { VisitorRecipeComponent } from './components/visitor/recipe/visitor-recipe.component';
import { VisitorRandomPickerComponent } from './components/visitor/random-picker/visitor-random-picker.component';
import { VisitorCategoriesComponent } from './components/visitor/categories/visitor-categories.component';
import { LoginComponent } from './components/login/login.component';
import { adminGuard } from './guards/admin.guard';
import { barkeeperGuard } from './guards/barkeeper.guard';

export const routes: Routes = [
  // Visitor routes (public, no authentication required)
  { path: '', redirectTo: '/visitor', pathMatch: 'full' },
  { path: 'visitor', component: VisitorMenuComponent },
  { path: 'visitor/cocktails', component: VisitorCocktailListComponent },
  { path: 'visitor/recipe/:id', component: VisitorRecipeComponent },
  { path: 'visitor/random', component: VisitorRandomPickerComponent },
  { path: 'visitor/categories', component: VisitorCategoriesComponent },
  
  // Login
  { path: 'login', component: LoginComponent },
  
  // Barkeeper routes (protected, lazy loaded)
  {
    path: 'barkeeper/menu',
    loadComponent: () => import('./components/barkeeper/menu/barkeeper-menu.component').then(m => m.BarkeeperMenuComponent),
    canActivate: [barkeeperGuard]
  },
  {
    path: 'barkeeper/alphabet',
    loadComponent: () => import('./components/barkeeper/alphabet/barkeeper-alphabet.component').then(m => m.BarkeeperAlphabetComponent),
    canActivate: [barkeeperGuard]
  },
  {
    path: 'barkeeper/cocktails',
    loadComponent: () => import('./components/barkeeper/cocktail-list/barkeeper-cocktail-list.component').then(m => m.BarkeeperCocktailListComponent),
    canActivate: [barkeeperGuard]
  },
  {
    path: 'barkeeper/recipe/:id',
    loadComponent: () => import('./components/barkeeper/recipe/barkeeper-recipe.component').then(m => m.BarkeeperRecipeComponent),
    canActivate: [barkeeperGuard]
  },
  {
    path: 'barkeeper/random',
    loadComponent: () => import('./components/barkeeper/random-picker/barkeeper-random-picker.component').then(m => m.BarkeeperRandomPickerComponent),
    canActivate: [barkeeperGuard]
  },
  {
    path: 'barkeeper/stock',
    loadComponent: () => import('./components/barkeeper/stock-management/barkeeper-stock-management.component').then(m => m.BarkeeperStockManagementComponent),
    canActivate: [barkeeperGuard]
  },
  { path: 'barkeeper', redirectTo: '/barkeeper/menu', pathMatch: 'full' },
  
  // Admin routes (protected, lazy loaded)
  {
    path: 'ingredients',
    loadComponent: () => import('./components/ingredients/ingredients.component').then(m => m.IngredientsComponent),
    canActivate: [adminGuard]
  },
  {
    path: 'cocktails',
    loadComponent: () => import('./components/cocktails/cocktails.component').then(m => m.CocktailsComponent),
    canActivate: [adminGuard]
  },
  {
    path: 'visualizations',
    loadComponent: () => import('./components/visualization/visualization.component').then(m => m.VisualizationComponent),
    canActivate: [adminGuard]
  },
  {
    path: 'settings',
    loadComponent: () => import('./components/settings/settings.component').then(m => m.SettingsComponent),
    canActivate: [adminGuard]
  }
];
