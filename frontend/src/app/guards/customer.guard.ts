import { inject } from '@angular/core';
import { Router, CanActivateFn } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { ApiService } from '../services/api.service';
import { map, take, catchError, switchMap } from 'rxjs/operators';
import { of } from 'rxjs';

export const customerGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const apiService = inject(ApiService);
  const router = inject(Router);

  // First, check if customer authentication is enabled
  return apiService.getCustomerAuthStatus().pipe(
    take(1),
    switchMap(statusResponse => {
      // If customer auth is disabled, allow access
      if (!statusResponse.enabled) {
        return of(true);
      }

      // Customer auth is enabled, proceed with authentication checks
      // Check if customer is authenticated
      if (authService.isCustomerAuthenticated()) {
        return of(true);
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
      return of(false);
    }),
    catchError(() => {
      // If we can't check the status, assume it's enabled for security
      // and proceed with authentication checks
      if (authService.isCustomerAuthenticated()) {
        return of(true);
      }
      router.navigate(['/customer-login'], { queryParams: { returnUrl: state.url } });
      return of(false);
    })
  );
};
