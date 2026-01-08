import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { environment } from '../../environments/environment';

export interface LoginRequest {
  password: string;
  role?: string;
}

export interface LoginResponse {
  success: boolean;
  message?: string;
  role?: string;
}

export interface AuthStatusResponse {
  authenticated: boolean;
  role?: string;
}

export interface CustomerAuthResponse {
  success: boolean;
  token?: string;
  message?: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private baseUrl = environment.apiUrl;
  private authStatusSubject = new BehaviorSubject<AuthStatusResponse>({ authenticated: false });
  public authStatus$ = this.authStatusSubject.asObservable();
  
  private customerAuthenticatedSubject = new BehaviorSubject<boolean>(false);
  public customerAuthenticated$ = this.customerAuthenticatedSubject.asObservable();

  constructor(private http: HttpClient) {
    this.checkCustomerAuth();
    this.checkAuthStatus();
  }

  // Customer Authentication Methods
  authenticateCustomer(token: string): Observable<CustomerAuthResponse> {
    return this.http.post<CustomerAuthResponse>(
      `${this.baseUrl}/auth/customer/authenticate?token=${token}`,
      {},
      { withCredentials: true }
    ).pipe(
      tap(response => {
        if (response.success && response.token) {
          localStorage.setItem('customerToken', response.token);
          this.customerAuthenticatedSubject.next(true);
        }
      })
    );
  }

  checkCustomerAuth(): void {
    const token = localStorage.getItem('customerToken');
    if (token) {
      this.http.get<CustomerAuthResponse>(
        `${this.baseUrl}/auth/customer/validate?token=${token}`,
        { withCredentials: true }
      ).subscribe({
        next: (response) => {
          if (response.success) {
            this.customerAuthenticatedSubject.next(true);
          } else {
            localStorage.removeItem('customerToken');
            this.customerAuthenticatedSubject.next(false);
          }
        },
        error: () => {
          localStorage.removeItem('customerToken');
          this.customerAuthenticatedSubject.next(false);
        }
      });
    } else {
      this.customerAuthenticatedSubject.next(false);
    }
  }

  isCustomerAuthenticated(): boolean {
    return this.customerAuthenticatedSubject.value;
  }

  generateCustomerToken(): Observable<CustomerAuthResponse> {
    return this.http.get<CustomerAuthResponse>(
      `${this.baseUrl}/auth/customer/generate-token`,
      { withCredentials: true }
    );
  }

  // Staff Authentication Methods (existing)
  login(password: string, role?: string): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(
      `${this.baseUrl}/auth/login`,
      { password, role },
      { withCredentials: true }
    ).pipe(
      tap(response => {
        if (response.success && response.role) {
          this.authStatusSubject.next({ authenticated: true, role: response.role });
        }
      })
    );
  }

  logout(): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(
      `${this.baseUrl}/auth/logout`,
      {},
      { withCredentials: true }
    ).pipe(
      tap(() => {
        this.authStatusSubject.next({ authenticated: false });
      })
    );
  }

  checkAuthStatus(): Observable<AuthStatusResponse> {
    return this.http.get<AuthStatusResponse>(
      `${this.baseUrl}/auth/status`,
      { withCredentials: true }
    ).pipe(
      tap(status => {
        this.authStatusSubject.next(status);
      })
    );
  }

  isAuthenticated(): boolean {
    return this.authStatusSubject.value.authenticated;
  }

  getRole(): string | undefined {
    return this.authStatusSubject.value.role;
  }

  isAdmin(): boolean {
    return this.authStatusSubject.value.role === 'ADMIN';
  }

  isBarkeeper(): boolean {
    return this.authStatusSubject.value.role === 'BARKEEPER';
  }
}
