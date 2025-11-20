import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { environment } from '../../environments/environment';

export interface LoginRequest {
  password: string;
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

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private baseUrl = environment.apiUrl;
  private authStatusSubject = new BehaviorSubject<AuthStatusResponse>({ authenticated: false });
  public authStatus$ = this.authStatusSubject.asObservable();

  constructor(private http: HttpClient) {
    this.checkAuthStatus();
  }

  login(password: string): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(
      `${this.baseUrl}/auth/login`,
      { password },
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
}
