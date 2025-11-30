import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { SettingsService } from './settings.service';

export type Theme = 'basic' | 'terminal-green' | 'cyberpunk' | 'amber';

@Injectable({
  providedIn: 'root'
})
export class ThemeService {
  private currentThemeSubject = new BehaviorSubject<Theme>('basic');
  currentTheme$ = this.currentThemeSubject.asObservable();

  constructor(private settingsService: SettingsService) {
    // Load theme from backend
    this.loadThemeFromBackend();
  }

  private loadThemeFromBackend(): void {
    this.settingsService.getTheme().subscribe({
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
    this.settingsService.setTheme(theme).subscribe({
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
}
