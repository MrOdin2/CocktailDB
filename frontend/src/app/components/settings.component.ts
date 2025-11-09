import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ThemeService, Theme } from '../services/theme.service';

@Component({
  selector: 'app-settings',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './settings.component.html',
  styleUrls: ['./settings.component.css']
})
export class SettingsComponent implements OnInit {
  currentTheme: Theme = 'basic';

  themes = [
    {
      id: 'basic' as Theme,
      name: 'Basic',
      description: 'Professional and unremarkable appearance',
      preview: 'White background with standard dark gray text and minimal styling'
    },
    {
      id: 'terminal-green' as Theme,
      name: 'Terminal Green',
      description: 'Classic 80s monochrome green terminal',
      preview: 'Pure black background with bright green (#00ff00) text'
    },
    {
      id: 'cyberpunk' as Theme,
      name: 'Cyberpunk Magenta/Cyan',
      description: 'Neon cyberpunk aesthetic (RoboCop, Blade Runner, Matrix)',
      preview: 'Dark blue (#0a0e27) background with cyan and magenta neon accents'
    },
    {
      id: 'amber' as Theme,
      name: 'Amber Monitor',
      description: 'Vintage amber CRT monitor',
      preview: 'Dark brown (#1a0f00) background with warm amber/orange (#ffb000) text'
    }
  ];

  constructor(private themeService: ThemeService) {}

  ngOnInit(): void {
    this.themeService.currentTheme$.subscribe(theme => {
      this.currentTheme = theme;
    });
  }

  selectTheme(theme: Theme): void {
    this.themeService.setTheme(theme);
  }
}
