import { Component, OnInit } from '@angular/core';
import { RouterOutlet, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { ThemeService } from './services/theme.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet, RouterLink],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  title = 'CocktailDB';

  constructor(private themeService: ThemeService) {}

  ngOnInit(): void {
    // Initialize theme on app startup
    this.themeService.setTheme(this.themeService.getCurrentTheme());
  }
}
