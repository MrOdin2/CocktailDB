import { Component, OnInit } from '@angular/core';
import { RouterOutlet, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';

import { ThemeService } from './services/theme.service';
import { AuthService } from './services/auth.service';
import { TranslatePipe } from './pipes/translate.pipe';

@Component({
    selector: 'app-root',
    imports: [RouterOutlet, RouterLink, CommonModule, TranslatePipe],
    templateUrl: './app.component.html',
    styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  title = 'CocktailDB';
  isAuthenticated = false;
  userRole: string | undefined;

  constructor(
    private themeService: ThemeService,
    public authService: AuthService
  ) {}

  ngOnInit(): void {
    // Initialize theme on app startup
    this.themeService.setTheme(this.themeService.getCurrentTheme());
    
    // Subscribe to auth status
    this.authService.authStatus$.subscribe(status => {
      this.isAuthenticated = status.authenticated;
      this.userRole = status.role;
    });
  }
  
  logout(): void {
    this.authService.logout().subscribe();
  }

  isAdmin(): boolean {
    return this.userRole === 'ADMIN';
  }

  isBarkeeper(): boolean {
    return this.userRole === 'BARKEEPER';
  }
}
