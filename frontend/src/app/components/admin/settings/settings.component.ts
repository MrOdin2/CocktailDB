import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Subscription } from 'rxjs';
import { ThemeService, Theme } from '../../../services/theme.service';
import { MeasureService } from '../../../services/measure.service';
import { TranslateService, Language } from '../../../services/translate.service';
import { ApiService } from '../../../services/api.service';
import { TranslatePipe } from '../../../pipes/translate.pipe';
import { MeasureUnit } from '../../../models/models';

@Component({
    selector: 'app-settings',
    imports: [CommonModule, TranslatePipe],
    templateUrl: './settings.component.html',
    styleUrls: ['../admin-shared.css', './settings.component.css']
})
export class SettingsComponent implements OnInit, OnDestroy {
  currentTheme: Theme = 'basic';
  currentUnit: MeasureUnit = MeasureUnit.ML;
  currentLanguage: Language = 'en';
  customerAuthEnabled: boolean = true;
  isLoadingCustomerAuth: boolean = false;
  private unitSubscription?: Subscription;
  private languageSubscription?: Subscription;

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
  
  measureUnits = [
    {
      id: MeasureUnit.ML,
      name: 'Milliliters (ml)',
      description: 'Metric system - commonly used internationally',
      example: '30 ml, 60 ml'
    },
    {
      id: MeasureUnit.OZ,
      name: 'Ounces (oz)',
      description: 'Imperial system - commonly used in the US',
      example: '1 oz, 2 oz'
    },
    {
      id: MeasureUnit.CL,
      name: 'Centiliters (cl)',
      description: 'Metric system - commonly used in Europe',
      example: '3 cl, 6 cl'
    }
  ];

  constructor(
    private themeService: ThemeService,
    private measureService: MeasureService,
    private translateService: TranslateService,
    private apiService: ApiService
  ) {}

  languages = this.translateService.getAvailableLanguages();

  ngOnInit(): void {
    this.themeService.currentTheme$.subscribe(theme => {
      this.currentTheme = theme;
    });
    
    this.unitSubscription = this.measureService.getUnit().subscribe(unit => {
      this.currentUnit = unit;
    });

    this.languageSubscription = this.translateService.getLanguage().subscribe(lang => {
      this.currentLanguage = lang;
    });
    
    // Load customer auth status
    this.loadCustomerAuthStatus();
  }
  
  ngOnDestroy(): void {
    this.unitSubscription?.unsubscribe();
    this.languageSubscription?.unsubscribe();
  }

  selectTheme(theme: Theme): void {
    this.themeService.setTheme(theme);
  }
  
  selectUnit(unit: MeasureUnit): void {
    this.measureService.setUnit(unit);
  }

  selectLanguage(lang: Language): void {
    this.translateService.setLanguage(lang);
  }
  
  loadCustomerAuthStatus(): void {
    this.apiService.getCustomerAuthStatus().subscribe({
      next: (response) => {
        this.customerAuthEnabled = response.enabled;
      },
      error: (error) => {
        console.error('Failed to load customer auth status:', error);
      }
    });
  }
  
  toggleCustomerAuth(): void {
    this.isLoadingCustomerAuth = true;
    this.apiService.setCustomerAuthStatus(!this.customerAuthEnabled).subscribe({
      next: (response) => {
        this.customerAuthEnabled = response.enabled;
        this.isLoadingCustomerAuth = false;
      },
      error: (error) => {
        console.error('Failed to update customer auth status:', error);
        this.isLoadingCustomerAuth = false;
      }
    });
  }
}
