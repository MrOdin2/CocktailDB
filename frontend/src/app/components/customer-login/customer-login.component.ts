import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-customer-login',
  imports: [CommonModule],
  template: `
    <div class="customer-login-container">
      <div class="customer-login-card">
        <h1>🍸 Welcome to CocktailDB</h1>
        <h2>Customer Authentication Required</h2>
        
        <div *ngIf="isAuthenticating" class="authenticating">
          <div class="spinner"></div>
          <p>Authenticating...</p>
        </div>
        
        <div *ngIf="errorMessage && !isAuthenticating" class="error-message">
          <p>{{ errorMessage }}</p>
          <p class="help-text">Please scan the QR code provided by the establishment.</p>
        </div>
        
        <div *ngIf="!isAuthenticating && !errorMessage" class="info-message">
          <p>This application requires customer authentication via QR code.</p>
          <p class="help-text">Please scan the QR code provided at the bar to access the cocktail menu.</p>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .customer-login-container {
      display: flex;
      justify-content: center;
      align-items: center;
      min-height: 100vh;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      padding: 20px;
    }

    .customer-login-card {
      background: white;
      border-radius: 12px;
      padding: 40px;
      box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
      max-width: 500px;
      text-align: center;
    }

    h1 {
      font-size: 2.5rem;
      margin-bottom: 10px;
      color: #333;
    }

    h2 {
      font-size: 1.5rem;
      margin-bottom: 30px;
      color: #666;
    }

    .authenticating {
      padding: 20px;
    }

    .spinner {
      border: 4px solid #f3f3f3;
      border-top: 4px solid #667eea;
      border-radius: 50%;
      width: 50px;
      height: 50px;
      animation: spin 1s linear infinite;
      margin: 0 auto 20px;
    }

    @keyframes spin {
      0% { transform: rotate(0deg); }
      100% { transform: rotate(360deg); }
    }

    .error-message {
      background-color: #fee;
      border: 1px solid #fcc;
      border-radius: 8px;
      padding: 20px;
      color: #c33;
    }

    .info-message {
      background-color: #e3f2fd;
      border: 1px solid #90caf9;
      border-radius: 8px;
      padding: 20px;
      color: #1976d2;
    }

    .help-text {
      margin-top: 15px;
      font-size: 0.95rem;
      color: #777;
    }

    p {
      margin: 10px 0;
      line-height: 1.6;
    }
  `]
})
export class CustomerLoginComponent implements OnInit {
  isAuthenticating = false;
  errorMessage = '';
  returnUrl = '/visitor';

  constructor(
    private authService: AuthService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    // Get return URL from query params
    this.returnUrl = this.route.snapshot.queryParams['returnUrl'] || '/visitor';
    
    // Check for token in URL parameters (QR code scan)
    const token = this.route.snapshot.queryParams['token'];
    
    if (token) {
      this.authenticateWithToken(token);
    } else {
      // Check if already authenticated
      if (this.authService.isCustomerAuthenticated()) {
        this.router.navigate([this.returnUrl]);
      }
    }
  }

  private authenticateWithToken(token: string): void {
    this.isAuthenticating = true;
    this.errorMessage = '';

    this.authService.authenticateCustomer(token).subscribe({
      next: (response) => {
        if (response.success) {
          // Authentication successful, redirect to return URL
          this.router.navigate([this.returnUrl]);
        } else {
          this.errorMessage = response.message || 'Authentication failed. Please try scanning the QR code again.';
          this.isAuthenticating = false;
        }
      },
      error: (error) => {
        this.errorMessage = error.error?.message || 'Authentication failed. Please try scanning the QR code again.';
        this.isAuthenticating = false;
      }
    });
  }
}
