import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { ApiService } from '../../../services/api.service';
import { TranslateService } from '../../../services/translate.service';
import { TranslatePipe } from '../../../pipes/translate.pipe';
import { StockUpdateService } from '../../../services/stock-update.service';
import { Cocktail } from '../../../models/models';

@Component({
  selector: 'app-visitor-random-picker',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule, TranslatePipe],
  templateUrl: './visitor-random-picker.component.html',
  styleUrls: ['./visitor-random-picker.component.css']
})
export class VisitorRandomPickerComponent implements OnInit, OnDestroy {
  availableCocktails: Cocktail[] = [];
  currentCocktail: Cocktail | null = null;
  loading: boolean = false;
  error: string | null = null;
  
  // Filters
  filterAlcoholic: string = 'all'; // 'all', 'alcoholic', 'non-alcoholic'
  filterSpirit: string = '';
  availableSpirits: string[] = [];

  private cleanupStockUpdates?: () => void;

  constructor(
    private apiService: ApiService,
    private router: Router,
    private translateService: TranslateService,
    private stockUpdateService: StockUpdateService,
  ) {}

  ngOnInit(): void {
    this.loadAvailableCocktails();
    this.cleanupStockUpdates = this.stockUpdateService.subscribeToUpdates(() => {
      this.loadAvailableCocktails();
    });
  }

  ngOnDestroy(): void {
    this.cleanupStockUpdates?.();
  }

  loadAvailableCocktails(): void {
    this.loading = true;
    this.error = null;

    this.apiService.getAvailableCocktails().subscribe({
      next: (cocktails) => {
        this.availableCocktails = cocktails;
        this.extractSpirits(cocktails);
        this.loading = false;
        
        // Auto-pick first random cocktail
        if (cocktails.length > 0) {
          this.pickRandom();
        }
      },
      error: (err) => {
        console.error('Error loading cocktails:', err);
        this.error = 'Failed to load cocktails. Please try again.';
        this.loading = false;
      }
    });
  }

  extractSpirits(cocktails: Cocktail[]): void {
    const spirits = new Set<string>();
    cocktails.forEach(cocktail => {
      if (cocktail.baseSpirit && cocktail.baseSpirit !== 'none') {
        spirits.add(cocktail.baseSpirit);
      }
    });
    this.availableSpirits = Array.from(spirits).sort();
  }

  pickRandom(): void {
    const filtered = this.getFilteredCocktails();
    
    if (filtered.length === 0) {
      this.currentCocktail = null;
      this.error = 'No cocktails match your filters.';
      return;
    }

    this.error = null;
    const randomIndex = Math.floor(Math.random() * filtered.length);
    this.currentCocktail = filtered[randomIndex];
  }

  getFilteredCocktails(): Cocktail[] {
    return this.availableCocktails.filter(cocktail => {
      // Filter by alcoholic type
      if (this.filterAlcoholic === 'alcoholic' && cocktail.abv === 0) {
        return false;
      }
      if (this.filterAlcoholic === 'non-alcoholic' && cocktail.abv > 0) {
        return false;
      }

      // Filter by spirit
      if (this.filterSpirit && cocktail.baseSpirit !== this.filterSpirit) {
        return false;
      }

      return true;
    });
  }

  viewRecipe(): void {
    if (this.currentCocktail?.id) {
      this.router.navigate(['/visitor/recipe', this.currentCocktail.id]);
    }
  }

  goBack(): void {
    this.router.navigate(['/visitor']);
  }

  getAlcoholType(abv: number): string {
    if (abv === 0) {
      return this.translateService.translate('visitor.cocktailList.nonAlcoholic');
    }
    return this.translateService.translate('visitor.cocktailList.alcoholic');
  }

  getAlcoholClass(abv: number): string {
    return abv > 0 ? 'alcoholic' : 'non-alcoholic';
  }

  resetFilters(): void {
    this.filterAlcoholic = 'all';
    this.filterSpirit = '';
    this.pickRandom();
  }
}
