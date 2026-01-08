import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../../services/auth.service';
import { DomSanitizer, SafeUrl } from '@angular/platform-browser';

@Component({
  selector: 'app-qr-code',
  imports: [CommonModule],
  template: `
    <div class="qr-code-container">
      <h2>Customer QR Code</h2>
      <p class="description">
        Generate a QR code for customers to scan and access the cocktail menu. 
        The QR code is valid for 24 hours.
      </p>
      
      <div *ngIf="isLoading" class="loading">
        <div class="spinner"></div>
        <p>Generating QR code...</p>
      </div>
      
      <div *ngIf="errorMessage" class="error-message">
        {{ errorMessage }}
      </div>
      
      <div *ngIf="qrCodeUrl && !isLoading" class="qr-code-display">
        <div class="qr-code-image">
          <img [src]="qrCodeUrl" alt="Customer Authentication QR Code" />
        </div>
        <div class="qr-info">
          <p class="token-label">Authentication Link:</p>
          <div class="token-display">
            <input 
              type="text" 
              [value]="customerUrl" 
              readonly 
              #urlInput
            />
            <button (click)="copyToClipboard(urlInput)" class="btn-copy">
              {{ copiedMessage || 'Copy' }}
            </button>
          </div>
          <p class="help-text">
            Customers can scan this QR code or visit the link above to access the menu.
          </p>
        </div>
      </div>
      
      <div class="actions">
        <button (click)="generateQrCode()" class="btn-primary" [disabled]="isLoading">
          {{ qrCodeUrl ? 'Generate New QR Code' : 'Generate QR Code' }}
        </button>
        <button *ngIf="qrCodeUrl" (click)="printQrCode()" class="btn-secondary">
          Print QR Code
        </button>
      </div>
    </div>
  `,
  styles: [`
    .qr-code-container {
      max-width: 800px;
      margin: 0 auto;
      padding: 20px;
    }

    h2 {
      font-size: 2rem;
      margin-bottom: 10px;
      color: #333;
    }

    .description {
      color: #666;
      margin-bottom: 30px;
      line-height: 1.6;
    }

    .loading {
      text-align: center;
      padding: 40px;
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
      padding: 15px;
      color: #c33;
      margin-bottom: 20px;
    }

    .qr-code-display {
      background: white;
      border-radius: 12px;
      padding: 30px;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
      margin-bottom: 30px;
    }

    .qr-code-image {
      text-align: center;
      margin-bottom: 30px;
    }

    .qr-code-image img {
      max-width: 300px;
      width: 100%;
      height: auto;
      border: 4px solid #667eea;
      border-radius: 8px;
      padding: 10px;
      background: white;
    }

    .qr-info {
      margin-top: 20px;
    }

    .token-label {
      font-weight: 600;
      color: #333;
      margin-bottom: 10px;
    }

    .token-display {
      display: flex;
      gap: 10px;
      margin-bottom: 15px;
    }

    .token-display input {
      flex: 1;
      padding: 12px;
      border: 1px solid #ddd;
      border-radius: 6px;
      font-family: monospace;
      font-size: 0.9rem;
      background: #f8f8f8;
    }

    .btn-copy {
      padding: 12px 24px;
      background-color: #667eea;
      color: white;
      border: none;
      border-radius: 6px;
      cursor: pointer;
      font-weight: 600;
      transition: background-color 0.2s;
      white-space: nowrap;
    }

    .btn-copy:hover {
      background-color: #5568d3;
    }

    .btn-copy:active {
      background-color: #4451b8;
    }

    .help-text {
      color: #777;
      font-size: 0.9rem;
      line-height: 1.5;
    }

    .actions {
      display: flex;
      gap: 15px;
      justify-content: center;
    }

    .btn-primary, .btn-secondary {
      padding: 14px 28px;
      font-size: 1rem;
      font-weight: 600;
      border: none;
      border-radius: 8px;
      cursor: pointer;
      transition: all 0.2s;
    }

    .btn-primary {
      background-color: #667eea;
      color: white;
    }

    .btn-primary:hover:not(:disabled) {
      background-color: #5568d3;
      transform: translateY(-1px);
      box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
    }

    .btn-primary:disabled {
      background-color: #ccc;
      cursor: not-allowed;
    }

    .btn-secondary {
      background-color: white;
      color: #667eea;
      border: 2px solid #667eea;
    }

    .btn-secondary:hover {
      background-color: #f8f9ff;
      transform: translateY(-1px);
    }

    @media print {
      .actions, .description, .token-display, .help-text, h2 {
        display: none !important;
      }
      
      .qr-code-container {
        text-align: center;
      }
      
      .qr-code-display {
        box-shadow: none;
        border: none;
      }
    }
  `]
})
export class QrCodeComponent implements OnInit {
  isLoading = false;
  errorMessage = '';
  qrCodeUrl: SafeUrl | null = null;
  customerUrl = '';
  copiedMessage = '';
  private token = '';

  constructor(
    private authService: AuthService,
    private sanitizer: DomSanitizer
  ) {}

  ngOnInit(): void {
    // Optionally generate QR code on component load
  }

  generateQrCode(): void {
    this.isLoading = true;
    this.errorMessage = '';
    this.copiedMessage = '';

    this.authService.generateCustomerToken().subscribe({
      next: (response) => {
        if (response.success && response.token) {
          this.token = response.token;
          this.createQrCode(response.token);
        } else {
          this.errorMessage = response.message || 'Failed to generate QR code';
          this.isLoading = false;
        }
      },
      error: (error) => {
        this.errorMessage = error.error?.message || 'Failed to generate QR code. Please ensure you are logged in as admin.';
        this.isLoading = false;
      }
    });
  }

  private createQrCode(token: string): void {
    // Get the current domain
    const domain = window.location.origin;
    this.customerUrl = `${domain}/customer-login?token=${token}`;
    
    // Use a QR code API service to generate the QR code image
    // We'll use the public QR code API from qr-server.com
    const qrApiUrl = `https://api.qrserver.com/v1/create-qr-code/?size=300x300&data=${encodeURIComponent(this.customerUrl)}`;
    
    this.qrCodeUrl = this.sanitizer.bypassSecurityTrustUrl(qrApiUrl);
    this.isLoading = false;
  }

  copyToClipboard(input: HTMLInputElement): void {
    // Use modern Clipboard API instead of deprecated execCommand
    navigator.clipboard.writeText(input.value).then(() => {
      this.copiedMessage = 'Copied!';
      setTimeout(() => {
        this.copiedMessage = '';
      }, 2000);
    }).catch(() => {
      // Fallback for browsers that don't support Clipboard API
      input.select();
      try {
        document.execCommand('copy');
        this.copiedMessage = 'Copied!';
        setTimeout(() => {
          this.copiedMessage = '';
        }, 2000);
      } catch (err) {
        this.copiedMessage = 'Failed to copy';
        setTimeout(() => {
          this.copiedMessage = '';
        }, 2000);
      }
    });
  }

  printQrCode(): void {
    window.print();
  }
}
