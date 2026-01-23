import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { ApiService } from './api.service';

export type Theme = 'basic' | 'terminal-green' | 'cyberpunk' | 'amber';

@Injectable({
  providedIn: 'root'
})
export class ThemeService {
  private currentThemeSubject = new BehaviorSubject<Theme>('basic');
  currentTheme$ = this.currentThemeSubject.asObservable();

  constructor(private apiService: ApiService) {
    // Load theme from backend
    this.loadThemeFromBackend();
  }

  private loadThemeFromBackend(): void {
    this.apiService.getTheme().subscribe({
      next: (response) => {
        const theme = response.theme as Theme;
        this.currentThemeSubject.next(theme);
        this.applyTheme(theme);
      },
      error: (error) => {
        console.error('Failed to load theme from backend, using default:', error);
        this.currentThemeSubject.next('basic');
        this.applyTheme('basic');
      }
    });
  }

  setTheme(theme: Theme): void {
    // Save theme to backend
    this.apiService.setTheme(theme).subscribe({
      next: (response) => {
        const savedTheme = response.theme as Theme;
        this.currentThemeSubject.next(savedTheme);
        this.applyTheme(savedTheme);
      },
      error: (error) => {
        console.error('Failed to save theme to backend:', error);
      }
    });
  }

  private applyTheme(theme: Theme): void {
    // Use daisyUI's data-theme attribute for theme switching
    const htmlElement = document.documentElement;
    htmlElement.setAttribute('data-theme', theme);
  }

  getCurrentTheme(): Theme {
    return this.currentThemeSubject.value;
  }
}
