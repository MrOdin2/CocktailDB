import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

export type Theme = 'terminal-green' | 'cyberpunk' | 'amber';

@Injectable({
  providedIn: 'root'
})
export class ThemeService {
  private currentThemeSubject = new BehaviorSubject<Theme>('terminal-green');
  currentTheme$ = this.currentThemeSubject.asObservable();

  constructor() {
    // Load saved theme from localStorage
    const savedTheme = localStorage.getItem('cocktaildb-theme') as Theme;
    if (savedTheme) {
      this.setTheme(savedTheme);
    }
  }

  setTheme(theme: Theme): void {
    this.currentThemeSubject.next(theme);
    localStorage.setItem('cocktaildb-theme', theme);
    this.applyTheme(theme);
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
      case 'terminal-green':
        return '1-terminal-green';
      case 'cyberpunk':
        return '2-cyberpunk';
      case 'amber':
        return '3-amber';
      default:
        return '1-terminal-green';
    }
  }

  getCurrentTheme(): Theme {
    return this.currentThemeSubject.value;
  }
}
