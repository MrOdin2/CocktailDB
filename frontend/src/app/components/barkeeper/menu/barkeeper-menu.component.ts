import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../../services/auth.service';

@Component({
  selector: 'app-barkeeper-menu',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './barkeeper-menu.component.html',
  styleUrls: ['../barkeeper-shared.css', './barkeeper-menu.component.css']
})
export class BarkeeperMenuComponent {
  showOnlyAvailable = false;

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  logout(): void {
    this.authService.logout().subscribe({
      next: () => {
        this.router.navigate(['/login']);
      },
      error: (error: any) => {
        console.error('Logout error:', error);
        this.router.navigate(['/login']);
      }
    });
  }

  // Store preference in localStorage for other components to access
  toggleAvailableOnly(): void {
    localStorage.setItem('showOnlyAvailable', this.showOnlyAvailable.toString());
  }
}
