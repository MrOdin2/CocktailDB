import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { Subscription } from 'rxjs';
import { ApiService } from '../../../services/api.service';
import { StockUpdateService } from '../../../services/stock-update.service';
import { TranslateService } from '../../../services/translate.service';
import { FuzzySearchService } from '../../../services/fuzzy-search.service';
import { TranslatePipe } from '../../../pipes/translate.pipe';
import { Cocktail, CocktailsWithSubstitutions } from '../../../models/models';

@Component({
  selector: 'app-barkeeper-cocktail-list',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule, TranslatePipe],
  templateUrl: './barkeeper-cocktail-list.component.html',
  styleUrls: ['../barkeeper-shared.css', './barkeeper-cocktail-list.component.css']
})
export class BarkeeperCocktailListComponent implements OnInit, OnDestroy {
  cocktails: Cocktail[] = [];
  filteredCocktails: Cocktail[] = [];
  availableCocktails: Cocktail[] = [];
  isLoading = false;
  letter: string = '';
  availableOnly: boolean = false;
  viewMode: 'letter' | 'available' = 'letter';

  // Substitution tracking
  cocktailsWithSubstitutes: Set<number> = new Set();
  cocktailsWithAlternatives: Set<number> = new Set();

  // Filter options for available cocktails
  filterBaseSpirit: string = 'all';
  filterTag: string = 'all';
  filterAbv: 'all' | 'low' | 'medium' | 'high' | 'non-alcoholic' = 'all';
  availableBaseSpirits: string[] = [];
  availableTags: string[] = [];
  showFilters: boolean = false;

  private cleanupStockUpdates?: () => void;
  private queryParamsSubscription?: Subscription;

  constructor(
    private apiService: ApiService,
    private stockUpdateService: StockUpdateService,
    private route: ActivatedRoute,
    private router: Router,
    private translateService: TranslateService,
    private fuzzySearchService: FuzzySearchService
  ) {}

  ngOnInit(): void {
    this.cleanupStockUpdates = this.stockUpdateService.subscribeToUpdates(() => {
      this.reloadCurrentView();
    });

    this.queryParamsSubscription = this.route.queryParams.subscribe(params => {
      this.letter = params['letter'] || '';
      this.availableOnly = params['availableOnly'] === 'true';
      
      if (params['mode'] === 'available') {
        this.viewMode = 'available';
        this.loadAvailableCocktails();
      } else {
        this.viewMode = 'letter';
        this.loadCocktails();
      }
    });
  }

  ngOnDestroy(): void {
    this.cleanupStockUpdates?.();
    this.queryParamsSubscription?.unsubscribe();
  }

  private reloadCurrentView(): void {
    if (this.viewMode === 'available') {
      this.loadAvailableCocktails();
    } else if (this.availableOnly) {
      this.loadCocktails();
    }
  }

  loadCocktails(): void {
    this.isLoading = true;
    
    if (this.availableOnly) {
      // Use substitution endpoint to include cocktails makeable with substitutes/alternatives
      this.apiService.getAvailableCocktailsWithSubstitutions().subscribe({
        next: (data: CocktailsWithSubstitutions) => {
          // Combine all categories
          this.cocktails = [
            ...data.exact,
            ...data.withSubstitutes,
            ...data.withAlternatives
          ];
          
          // Track which cocktails use substitutes/alternatives
          this.cocktailsWithSubstitutes.clear();
          this.cocktailsWithAlternatives.clear();
          
          data.withSubstitutes.forEach(c => {
            if (c.id) this.cocktailsWithSubstitutes.add(c.id);
          });
          
          data.withAlternatives.forEach(c => {
            if (c.id) this.cocktailsWithAlternatives.add(c.id);
          });
          
          this.filterByLetter();
          this.isLoading = false;
        },
        error: (error: any) => {
          console.error('Error loading cocktails:', error);
          this.isLoading = false;
        }
      });
    } else {
      // Load all cocktails
      this.apiService.getAllCocktails().subscribe({
        next: (cocktails: Cocktail[]) => {
          this.cocktails = cocktails;
          this.filterByLetter();
          this.isLoading = false;
        },
        error: (error: any) => {
          console.error('Error loading cocktails:', error);
          this.isLoading = false;
        }
      });
    }
  }

  loadAvailableCocktails(): void {
    this.isLoading = true;
    this.apiService.getAvailableCocktailsWithSubstitutions().subscribe({
      next: (data: CocktailsWithSubstitutions) => {
        // Combine all categories
        this.availableCocktails = [
          ...data.exact,
          ...data.withSubstitutes,
          ...data.withAlternatives
        ];
        
        // Track which cocktails use substitutes/alternatives
        this.cocktailsWithSubstitutes.clear();
        this.cocktailsWithAlternatives.clear();
        
        data.withSubstitutes.forEach(c => {
          if (c.id) this.cocktailsWithSubstitutes.add(c.id);
        });
        
        data.withAlternatives.forEach(c => {
          if (c.id) this.cocktailsWithAlternatives.add(c.id);
        });
        
        this.extractFilterOptions(this.availableCocktails);
        this.applyFilters();
        this.isLoading = false;
      },
      error: (error: any) => {
        console.error('Error loading available cocktails:', error);
        this.isLoading = false;
      }
    });
  }

  extractFilterOptions(cocktails: Cocktail[]): void {
    // Extract unique base spirits (use 'Other' for cocktails without a defined base spirit)
    const spiritsSet = new Set(cocktails.map(c => c.baseSpirit || 'Other'));
    this.availableBaseSpirits = Array.from(spiritsSet).sort();

    // Extract unique tags
    const tagsSet = new Set<string>();
    cocktails.forEach(c => {
      if (c.tags) {
        c.tags.forEach(tag => tagsSet.add(tag));
      }
    });
    this.availableTags = Array.from(tagsSet).sort();
  }

  applyFilters(): void {
    let result = [...this.availableCocktails];

    // Filter by base spirit (handle 'Other' for cocktails without defined base spirit)
    if (this.filterBaseSpirit !== 'all') {
      if (this.filterBaseSpirit === 'Other') {
        result = result.filter(c => !c.baseSpirit || c.baseSpirit === 'Other');
      } else {
        result = result.filter(c => c.baseSpirit === this.filterBaseSpirit);
      }
    }

    // Filter by tag
    if (this.filterTag !== 'all') {
      result = result.filter(c => c.tags && c.tags.includes(this.filterTag));
    }

    // Filter by ABV range
    switch (this.filterAbv) {
      case 'non-alcoholic':
        result = result.filter(c => c.abv === 0);
        break;
      case 'low':
        result = result.filter(c => c.abv > 0 && c.abv <= 10);
        break;
      case 'medium':
        result = result.filter(c => c.abv > 10 && c.abv <= 25);
        break;
      case 'high':
        result = result.filter(c => c.abv > 25);
        break;
    }

    this.filteredCocktails = result;
  }

  onFilterChange(): void {
    this.applyFilters();
  }

  toggleFilters(): void {
    this.showFilters = !this.showFilters;
  }

  clearFilters(): void {
    this.filterBaseSpirit = 'all';
    this.filterTag = 'all';
    this.filterAbv = 'all';
    this.applyFilters();
  }

  hasActiveFilters(): boolean {
    return this.filterBaseSpirit !== 'all' || 
           this.filterTag !== 'all' || 
           this.filterAbv !== 'all';
  }

  filterByLetter(): void {
    if (this.letter) {
      // Use fuzzy search for letter filtering to handle typos
      const results = this.fuzzySearchService.search(
        this.letter,
        this.cocktails,
        c => c.name,
        1, // Stricter threshold for letter filtering
        0.5 // Higher minimum score
      );
      this.filteredCocktails = results.map(r => r.item);
    } else {
      this.filteredCocktails = this.cocktails;
    }
  }

  selectCocktail(cocktail: Cocktail): void {
    this.router.navigate(['/barkeeper/recipe', cocktail.id]);
  }

  getBackRoute(): string {
    return this.viewMode === 'available' ? '/barkeeper/menu' : '/barkeeper/alphabet';
  }

  getTitle(): string {
    if (this.viewMode === 'available') {
      return this.translateService.translate('barkeeper.menu.availableCocktails');
    }
    return this.letter ? `${this.translateService.translate('common.cocktails')} - ${this.letter}` : this.translateService.translate('cocktails.allCocktails');
  }

  usesSubstitutes(cocktail: Cocktail): boolean {
    return cocktail.id ? this.cocktailsWithSubstitutes.has(cocktail.id) : false;
  }

  usesAlternatives(cocktail: Cocktail): boolean {
    return cocktail.id ? this.cocktailsWithAlternatives.has(cocktail.id) : false;
  }
}
