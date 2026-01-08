import { Routes } from '@angular/router';
import { VisitorMenuComponent } from './components/visitor/menu/visitor-menu.component';
import { VisitorCocktailListComponent } from './components/visitor/cocktail-list/visitor-cocktail-list.component';
import { VisitorRecipeComponent } from './components/visitor/recipe/visitor-recipe.component';
import { VisitorRandomPickerComponent } from './components/visitor/random-picker/visitor-random-picker.component';
import { VisitorCategoriesComponent } from './components/visitor/categories/visitor-categories.component';
import { LoginComponent } from './components/login/login.component';
import { CustomerLoginComponent } from './components/customer-login/customer-login.component';
import { adminGuard } from './guards/admin.guard';
import { barkeeperGuard } from './guards/barkeeper.guard';
import { customerGuard } from './guards/customer.guard';

export const routes: Routes = [
  // Customer login (no guard required)
  { path: 'customer-login', component: CustomerLoginComponent },
  
  // Visitor routes (require customer authentication)
  { path: '', redirectTo: '/visitor', pathMatch: 'full' },
  { path: 'visitor', component: VisitorMenuComponent, canActivate: [customerGuard] },
  { path: 'visitor/cocktails', component: VisitorCocktailListComponent, canActivate: [customerGuard] },
  { path: 'visitor/recipe/:id', component: VisitorRecipeComponent, canActivate: [customerGuard] },
  { path: 'visitor/random', component: VisitorRandomPickerComponent, canActivate: [customerGuard] },
  { path: 'visitor/categories', component: VisitorCategoriesComponent, canActivate: [customerGuard] },
  
  // Login (requires customer authentication first)
  { path: 'login', component: LoginComponent, canActivate: [customerGuard] },
  
  // Barkeeper routes (lazy loaded, protected by customer + barkeeper guards)
  { 
    path: 'barkeeper', 
    loadChildren: () => import('./components/barkeeper/barkeeper.routes').then(m => m.BARKEEPER_ROUTES),
    canActivate: [customerGuard, barkeeperGuard]
  },
  
  // Admin routes (lazy loaded, protected by customer + admin guards)
  { 
    path: 'admin', 
    loadComponent: () => import('./components/admin/admin.component').then(m => m.AdminComponent),
    loadChildren: () => import('./components/admin/admin.routes').then(m => m.ADMIN_ROUTES),
    canActivate: [customerGuard, adminGuard]
  },
  
  // Redirect old admin routes to new structure
  { path: 'ingredients', redirectTo: '/admin/ingredients', pathMatch: 'full' },
  { path: 'cocktails', redirectTo: '/admin/cocktails', pathMatch: 'full' },
  { path: 'visualizations', redirectTo: '/admin/visualizations', pathMatch: 'full' },
  { path: 'settings', redirectTo: '/admin/settings', pathMatch: 'full' }
];
