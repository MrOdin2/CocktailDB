import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';
import { tap } from 'rxjs/operators';
import { environment } from '../../environments/environment';

export type Theme = 'basic' | 'terminal-green' | 'cyberpunk' | 'amber';

interface ThemeResponse {
  theme: string;
}

@Injectable({
  providedIn: 'root'
})
export class ThemeService {
  private baseUrl = environment.apiUrl;
  private currentThemeSubject = new BehaviorSubject<Theme>('basic');
  currentTheme$ = this.currentThemeSubject.asObservable();

  constructor(private http: HttpClient) {
    // Load theme from backend on initialization
    this.loadThemeFromBackend();
  }

  private loadThemeFromBackend(): void {
    this.http.get<ThemeResponse>(`${this.baseUrl}/settings/theme`).subscribe({
      next: (response) => {
        const theme = response.theme as Theme;
        this.currentThemeSubject.next(theme);
        this.applyTheme(theme);
      },
      error: (err) => {
        console.error('Failed to load theme from backend, using default:', err);
        // Fallback to basic theme on error
        this.applyTheme('basic');
      }
    });
  }

  setTheme(theme: Theme): Observable<ThemeResponse> {
    // Save theme to backend
    return this.http.put<ThemeResponse>(`${this.baseUrl}/settings/theme`, { theme }).pipe(
      tap((response) => {
        const savedTheme = response.theme as Theme;
        this.currentThemeSubject.next(savedTheme);
        this.applyTheme(savedTheme);
      })
    );
  }

  private applyTheme(theme: Theme): void {
    // Remove existing theme stylesheets
    const existingTheme = document.getElementById('theme-stylesheet');
    if (existingTheme) {
      existingTheme.remove();
    }

    // Create and append new theme stylesheet
    const link = document.createElement('link');
    link.id = 'theme-stylesheet';
    link.rel = 'stylesheet';
    link.href = `styles-theme${this.getThemeFileName(theme)}.css`;
    document.head.appendChild(link);
  }

  private getThemeFileName(theme: Theme): string {
    switch (theme) {
      case 'basic':
        return '0-basic';
      case 'terminal-green':
        return '1-terminal-green';
      case 'cyberpunk':
        return '2-cyberpunk';
      case 'amber':
        return '3-amber';
      default:
        return '0-basic';
    }
  }

  getCurrentTheme(): Theme {
    return this.currentThemeSubject.value;
  }

  // Reload theme from backend (useful after theme changes)
  reloadTheme(): void {
    this.loadThemeFromBackend();
  }
}
