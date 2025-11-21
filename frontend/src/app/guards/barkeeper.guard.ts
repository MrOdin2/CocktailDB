import { inject } from '@angular/core';
import { Router, CanActivateFn } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { map, take } from 'rxjs/operators';

export const barkeeperGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  return authService.checkAuthStatus().pipe(
    take(1),
    map(status => {
      if (status.authenticated && status.role === 'BARKEEPER') {
        return true;
      } else if (status.authenticated) {
        // Authenticated but not barkeeper, redirect to cocktails
        router.navigate(['/cocktails']);
        return false;
      } else {
        router.navigate(['/login']);
        return false;
      }
    })
  );
};
