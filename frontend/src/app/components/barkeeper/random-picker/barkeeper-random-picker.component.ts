import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { ApiService } from '../../../services/api.service';
import { TranslatePipe } from '../../../pipes/translate.pipe';
import { StockUpdateService } from '../../../services/stock-update.service';
import { Cocktail, CocktailsWithSubstitutions } from '../../../models/models';

@Component({
  selector: 'app-barkeeper-random-picker',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule, TranslatePipe],
  templateUrl: './barkeeper-random-picker.component.html',
  styleUrls: ['../barkeeper-shared.css', './barkeeper-random-picker.component.css']
})
export class BarkeeperRandomPickerComponent implements OnInit, OnDestroy {
  cocktails: Cocktail[] = [];
  availableCocktails: Cocktail[] = [];
  cocktailsWithSubstitutions: CocktailsWithSubstitutions | null = null;
  availableBaseSpirits: string[] = [];
  isLoading = false;
  
  filterAlcoholic: 'all' | 'alcoholic' | 'non-alcoholic' = 'all';
  filterBaseSpirit: string = 'all';

  private cleanupStockUpdates?: () => void;

  constructor(
    private apiService: ApiService,
    private stockUpdateService: StockUpdateService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadCocktails();
    this.cleanupStockUpdates = this.stockUpdateService.subscribeToUpdates(() => {
      this.loadCocktails();
    });
  }

  ngOnDestroy(): void {
    this.cleanupStockUpdates?.();
  }

  loadCocktails(): void {
    this.isLoading = true;
    
    this.apiService.getAllCocktails().subscribe({
      next: (cocktails: Cocktail[]) => {
        this.cocktails = cocktails;
        const spiritsSet = new Set(cocktails.map(c => c.baseSpirit || 'none'));
        this.availableBaseSpirits = Array.from(spiritsSet).sort();
        this.isLoading = false;
      },
      error: (error: any) => {
        console.error('Error loading cocktails:', error);
        this.isLoading = false;
      }
    });

    // Load cocktails with substitution info
    this.apiService.getAvailableCocktailsWithSubstitutions().subscribe({
      next: (result: CocktailsWithSubstitutions) => {
        this.cocktailsWithSubstitutions = result;
        // Combine all available cocktails (exact, with substitutes, with alternatives)
        this.availableCocktails = [
          ...result.exact,
          ...result.withSubstitutes,
          ...result.withAlternatives
        ];
      },
      error: (error: any) => {
        console.error('Error loading available cocktails with substitutions:', error);
      }
    });
  }

  pickRandomCocktail(): void {
    const showOnlyAvailable = localStorage.getItem('showOnlyAvailable') === 'true';
    let pool = showOnlyAvailable ? this.availableCocktails : this.cocktails;
    
    if (this.filterAlcoholic === 'alcoholic') {
      pool = pool.filter(c => c.abv > 0);
    } else if (this.filterAlcoholic === 'non-alcoholic') {
      pool = pool.filter(c => c.abv === 0);
    }
    
    if (this.filterBaseSpirit !== 'all') {
      pool = pool.filter(c => c.baseSpirit === this.filterBaseSpirit);
    }
    
    if (pool.length > 0) {
      const randomIndex = Math.floor(Math.random() * pool.length);
      const selectedCocktail = pool[randomIndex];
      this.router.navigate(['/barkeeper/recipe', selectedCocktail.id]);
    }
  }
}
