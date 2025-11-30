import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class SettingsService {
  private baseUrl = `${environment.apiUrl}/settings`;

  constructor(private http: HttpClient) {}

  getTheme(): Observable<{ theme: string }> {
    return this.http.get<{ theme: string }>(`${this.baseUrl}/theme`);
  }

  setTheme(theme: string): Observable<{ theme: string }> {
    return this.http.put<{ theme: string }>(`${this.baseUrl}/theme`, { theme }, { withCredentials: true });
  }
}

