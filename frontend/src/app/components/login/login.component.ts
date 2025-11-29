import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { TranslateService } from '../../services/translate.service';
import { TranslatePipe } from '../../pipes/translate.pipe';

@Component({
  selector: 'app-login',
  imports: [CommonModule, FormsModule, TranslatePipe],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  password: string = '';
  selectedRole: string = 'ADMIN';
  errorMessage: string = '';
  isLoading: boolean = false;

  constructor(
    private authService: AuthService,
    private router: Router,
    private translateService: TranslateService
  ) {}

  getPlaceholder(): string {
    const role = this.selectedRole.toLowerCase();
    return this.translateService.translate('login.enterPassword', { role });
  }

  getLoginButtonText(): string {
    if (this.isLoading) {
      return this.translateService.translate('login.loggingIn');
    }
    return this.translateService.translate('login.loginAs', { role: this.selectedRole });
  }

  onSubmit(): void {
    if (!this.password) {
      this.errorMessage = 'Please enter a password';
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';

    this.authService.login(this.password, this.selectedRole).subscribe({
      next: (response) => {
        if (response.success) {
          // Navigate based on role
          if (response.role === 'BARKEEPER') {
            this.router.navigate(['/barkeeper']);
          } else {
            this.router.navigate(['/cocktails']);
          }
        } else {
          this.errorMessage = response.message || 'Login failed';
          this.isLoading = false;
        }
      },
      error: (error) => {
        this.errorMessage = error.error?.message || 'Login failed. Please check your password.';
        this.isLoading = false;
      }
    });
  }
}
