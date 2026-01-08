import { HttpInterceptorFn } from '@angular/common/http';

export const customerTokenInterceptor: HttpInterceptorFn = (req, next) => {
  // Get customer token from localStorage
  const customerToken = localStorage.getItem('customerToken');
  
  // If token exists, clone the request and add the token header
  if (customerToken) {
    const clonedRequest = req.clone({
      setHeaders: {
        'X-Customer-Token': customerToken
      }
    });
    return next(clonedRequest);
  }
  
  // If no token, proceed with original request
  return next(req);
};
