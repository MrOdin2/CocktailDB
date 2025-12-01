import { Routes } from '@angular/router';

export const BARKEEPER_ROUTES: Routes = [
  { 
    path: '', 
    redirectTo: 'menu', 
    pathMatch: 'full' 
  },
  { 
    path: 'menu', 
    loadComponent: () => import('./menu/barkeeper-menu.component').then(m => m.BarkeeperMenuComponent)
  },
  { 
    path: 'alphabet', 
    loadComponent: () => import('./alphabet/barkeeper-alphabet.component').then(m => m.BarkeeperAlphabetComponent)
  },
  { 
    path: 'cocktails', 
    loadComponent: () => import('./cocktail-list/barkeeper-cocktail-list.component').then(m => m.BarkeeperCocktailListComponent)
  },
  { 
    path: 'recipe/:id', 
    loadComponent: () => import('./recipe/barkeeper-recipe.component').then(m => m.BarkeeperRecipeComponent)
  },
  { 
    path: 'random', 
    loadComponent: () => import('./random-picker/barkeeper-random-picker.component').then(m => m.BarkeeperRandomPickerComponent)
  },
  { 
    path: 'stock', 
    loadComponent: () => import('./stock-management/barkeeper-stock-management.component').then(m => m.BarkeeperStockManagementComponent)
  }
];
