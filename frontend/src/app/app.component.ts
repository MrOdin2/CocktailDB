import { Component, OnInit } from '@angular/core';
import { RouterOutlet } from '@angular/router';

import { ThemeService } from './services/theme.service';

@Component({
    selector: 'app-root',
    imports: [RouterOutlet],
    templateUrl: './app.component.html',
    styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  constructor(
    private themeService: ThemeService
  ) {}

  ngOnInit(): void {
    // Initialize theme on app startup
    this.themeService.setTheme(this.themeService.getCurrentTheme());
  }
}
