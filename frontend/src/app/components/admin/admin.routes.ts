import { Routes } from '@angular/router';

export const ADMIN_ROUTES: Routes = [
  { 
    path: '', 
    redirectTo: 'cocktails', 
    pathMatch: 'full' 
  },
  { 
    path: 'ingredients', 
    loadComponent: () => import('./ingredients/ingredients.component').then(m => m.IngredientsComponent)
  },
  { 
    path: 'cocktails', 
    loadComponent: () => import('./cocktails/cocktails.component').then(m => m.CocktailsComponent)
  },
  { 
    path: 'visualizations', 
    loadComponent: () => import('./visualization/visualization.component').then(m => m.VisualizationComponent)
  },
  { 
    path: 'settings', 
    loadComponent: () => import('./settings/settings.component').then(m => m.SettingsComponent)
  },
  { 
    path: 'qr-code', 
    loadComponent: () => import('./qr-code/qr-code.component').then(m => m.QrCodeComponent)
  }
];
