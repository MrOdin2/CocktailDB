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
  
  // Barkeeper routes (lazy loaded, protected)
  { 
    path: 'barkeeper', 
    loadChildren: () => import('./components/barkeeper/barkeeper.routes').then(m => m.BARKEEPER_ROUTES),
    canActivate: [barkeeperGuard]
  },
  
  // Admin routes (lazy loaded, protected)
  { 
    path: 'admin', 
    loadComponent: () => import('./components/admin/admin.component').then(m => m.AdminComponent),
    loadChildren: () => import('./components/admin/admin.routes').then(m => m.ADMIN_ROUTES),
    canActivate: [adminGuard]
  },
  
  // Redirect old admin routes to new structure
  { path: 'ingredients', redirectTo: '/admin/ingredients', pathMatch: 'full' },
  { path: 'cocktails', redirectTo: '/admin/cocktails', pathMatch: 'full' },
  { path: 'visualizations', redirectTo: '/admin/visualizations', pathMatch: 'full' },
  { path: 'settings', redirectTo: '/admin/settings', pathMatch: 'full' }
];
