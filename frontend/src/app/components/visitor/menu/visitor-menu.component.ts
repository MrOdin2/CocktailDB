import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { Subscription } from 'rxjs';
import { TranslatePipe } from '../../../pipes/translate.pipe';
import { TranslateService, Language } from '../../../services/translate.service';

@Component({
  selector: 'app-visitor-menu',
  standalone: true,
  imports: [CommonModule, RouterModule, TranslatePipe],
  templateUrl: './visitor-menu.component.html',
  styleUrls: ['./visitor-menu.component.css']
})
export class VisitorMenuComponent implements OnInit, OnDestroy {
  currentLanguage: Language = 'en';
  private languageSubscription?: Subscription;

  constructor(
    private router: Router,
    private translateService: TranslateService
  ) {}

  ngOnInit(): void {
    this.languageSubscription = this.translateService.getLanguage().subscribe(lang => {
      this.currentLanguage = lang;
    });
  }

  ngOnDestroy(): void {
    this.languageSubscription?.unsubscribe();
  }

  toggleLanguage(): void {
    const newLang: Language = this.currentLanguage === 'en' ? 'de' : 'en';
    this.translateService.setLanguage(newLang);
  }

  getLanguageLabel(): string {
    return this.currentLanguage === 'en' ? 'DE' : 'EN';
  }
}
