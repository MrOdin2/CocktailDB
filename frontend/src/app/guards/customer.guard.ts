import { inject } from '@angular/core';
import { Router, CanActivateFn } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { map, take, catchError } from 'rxjs/operators';
import { of } from 'rxjs';

export const customerGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  // Check if customer is authenticated
  if (authService.isCustomerAuthenticated()) {
    return true;
  }

  // Check for token in URL parameters (QR code redirect)
  const token = route.queryParams['token'];
  if (token) {
    // Authenticate with token and return observable
    return authService.authenticateCustomer(token).pipe(
      take(1),
      map(response => {
        if (response.success) {
          // Remove token from URL
          router.navigate([state.url.split('?')[0]]);
          return true;
        } else {
          router.navigate(['/customer-login']);
          return false;
        }
      }),
      catchError(() => {
        router.navigate(['/customer-login']);
        return of(false);
      })
    );
  }

  // No customer authentication, redirect to customer login
  router.navigate(['/customer-login'], { queryParams: { returnUrl: state.url } });
  return false;
};
