import { Pipe, PipeTransform, OnDestroy, ChangeDetectorRef } from '@angular/core';
import { TranslateService } from '../services/translate.service';
import { Subscription } from 'rxjs';

@Pipe({
  name: 'translate',
  pure: false
})
export class TranslatePipe implements PipeTransform, OnDestroy {
  private lastKey: string = '';
  private lastParams: { [key: string]: string | number } | undefined;
  private lastValue: string = '';
  private subscription: Subscription | null = null;

  constructor(
    private translateService: TranslateService,
    private cdr: ChangeDetectorRef
  ) {
    this.subscription = this.translateService.currentLanguage$.subscribe(() => {
      if (this.lastKey) {
        this.lastValue = this.translateService.translate(this.lastKey, this.lastParams);
        this.cdr.markForCheck();
      }
    });
  }

  transform(key: string, params?: { [key: string]: string | number }): string {
    if (!key) return '';
    
    // Check if we need to recalculate
    if (key !== this.lastKey || !this.paramsEqual(params, this.lastParams)) {
      this.lastKey = key;
      this.lastParams = params;
      this.lastValue = this.translateService.translate(key, params);
    }
    
    return this.lastValue;
  }

  private paramsEqual(
    a: { [key: string]: string | number } | undefined,
    b: { [key: string]: string | number } | undefined
  ): boolean {
    if (a === b) return true;
    if (!a || !b) return false;
    const keysA = Object.keys(a);
    const keysB = Object.keys(b);
    if (keysA.length !== keysB.length) return false;
    return keysA.every(key => a[key] === b[key]);
  }

  ngOnDestroy(): void {
    if (this.subscription) {
      this.subscription.unsubscribe();
    }
  }
}
