import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { en } from '../i18n/en';
import { de } from '../i18n/de';

export type Language = 'en' | 'de';

const STORAGE_KEY = 'cocktaildb-language';

type TranslationValue = string | { [key: string]: TranslationValue };

interface Translations {
  [key: string]: TranslationValue;
}

@Injectable({
  providedIn: 'root'
})
export class TranslateService {
  private translations: { [key in Language]: Translations } = {
    en,
    de
  };

  private currentLanguage = new BehaviorSubject<Language>('en');
  currentLanguage$ = this.currentLanguage.asObservable();

  constructor() {
    this.loadPreference();
  }

  private loadPreference(): void {
    const stored = localStorage.getItem(STORAGE_KEY);
    if (stored && (stored === 'en' || stored === 'de')) {
      this.currentLanguage.next(stored);
    } else {
      // Try to detect browser language
      const browserLang = navigator.language.split('-')[0];
      if (browserLang === 'de') {
        this.currentLanguage.next('de');
      }
    }
  }

  getLanguage(): Observable<Language> {
    return this.currentLanguage.asObservable();
  }

  getCurrentLanguage(): Language {
    return this.currentLanguage.getValue();
  }

  setLanguage(lang: Language): void {
    this.currentLanguage.next(lang);
    localStorage.setItem(STORAGE_KEY, lang);
  }

  /**
   * Translate a key to the current language
   * @param key Dot-notation key like 'common.back' or 'login.title'
   * @param params Optional parameters for interpolation, e.g., { name: 'John' }
   */
  translate(key: string, params?: { [key: string]: string | number }): string {
    const lang = this.currentLanguage.getValue();
    const value = this.getNestedValue(this.translations[lang], key);
    
    if (value === undefined) {
      // Fallback to English
      const fallback = this.getNestedValue(this.translations['en'], key);
      if (fallback === undefined) {
        console.warn(`Translation key not found: ${key}`);
        return key;
      }
      return this.interpolate(fallback, params);
    }
    
    return this.interpolate(value, params);
  }

  /**
   * Get translation observable that updates when language changes
   */
  get(key: string, params?: { [key: string]: string | number }): Observable<string> {
    return new Observable(subscriber => {
      const subscription = this.currentLanguage$.subscribe(() => {
        subscriber.next(this.translate(key, params));
      });
      return () => subscription.unsubscribe();
    });
  }

  private getNestedValue(obj: Translations, path: string): string | undefined {
    const keys = path.split('.');
    let current: TranslationValue | undefined = obj;
    
    for (const key of keys) {
      if (current && typeof current === 'object' && key in current) {
        current = (current as { [key: string]: TranslationValue })[key];
      } else {
        return undefined;
      }
    }
    
    return typeof current === 'string' ? current : undefined;
  }

  private interpolate(value: string, params?: { [key: string]: string | number }): string {
    if (!params) return value;
    
    let result = value;
    for (const [key, val] of Object.entries(params)) {
      result = result.replace(new RegExp(`{{\\s*${key}\\s*}}`, 'g'), String(val));
    }
    return result;
  }

  getAvailableLanguages(): { code: Language; name: string; nativeName: string }[] {
    return [
      { code: 'en', name: 'English', nativeName: 'English' },
      { code: 'de', name: 'German', nativeName: 'Deutsch' }
    ];
  }
}
