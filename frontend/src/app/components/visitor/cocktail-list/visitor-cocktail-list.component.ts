import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { ApiService } from '../../../services/api.service';
import { TranslateService } from '../../../services/translate.service';
import { TranslatePipe } from '../../../pipes/translate.pipe';
import { StockUpdateService } from '../../../services/stock-update.service';
import { Cocktail, CocktailsWithSubstitutions } from '../../../models/models';

@Component({
  selector: 'app-visitor-cocktail-list',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule, TranslatePipe],
  templateUrl: './visitor-cocktail-list.component.html',
  styleUrls: ['./visitor-cocktail-list.component.css']
})
export class VisitorCocktailListComponent implements OnInit, OnDestroy {
  cocktails: Cocktail[] = [];
  filteredCocktails: Cocktail[] = [];
  searchTerm: string = '';
  loading: boolean = false;
  error: string | null = null;
  private cleanupStockUpdates?: () => void;

  // Substitution tracking
  cocktailsWithSubstitutes: Set<number> = new Set();
  cocktailsWithAlternatives: Set<number> = new Set();

  constructor(
    private apiService: ApiService,
    private router: Router,
    private translateService: TranslateService,
    private stockUpdateService: StockUpdateService,
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
    this.loading = true;
    this.error = null;
    
    // Load available cocktails with substitutions for visitors
    this.apiService.getAvailableCocktailsWithSubstitutions().subscribe({
      next: (data: CocktailsWithSubstitutions) => {
        // Combine all categories
        this.cocktails = [
          ...data.exact,
          ...data.withSubstitutes,
          ...data.withAlternatives
        ];
        this.filteredCocktails = this.cocktails;
        
        // Track which cocktails use substitutes/alternatives
        this.cocktailsWithSubstitutes.clear();
        this.cocktailsWithAlternatives.clear();
        
        data.withSubstitutes.forEach(c => {
          if (c.id) this.cocktailsWithSubstitutes.add(c.id);
        });
        
        data.withAlternatives.forEach(c => {
          if (c.id) this.cocktailsWithAlternatives.add(c.id);
        });
        
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading cocktails:', err);
        this.error = 'Failed to load cocktails. Please try again.';
        this.loading = false;
      }
    });
  }

  onSearch(): void {
    if (!this.searchTerm.trim()) {
      this.filteredCocktails = this.cocktails;
      return;
    }

    const search = this.searchTerm.toLowerCase().trim();
    this.filteredCocktails = this.cocktails.filter(cocktail => {
      // Search in name
      if (cocktail.name.toLowerCase().includes(search)) {
        return true;
      }
      // Search in base spirit
      if (cocktail.baseSpirit.toLowerCase().includes(search)) {
        return true;
      }
      // Search in tags
      if (cocktail.tags.some(tag => tag.toLowerCase().includes(search))) {
        return true;
      }
      return false;
    });
  }

  clearSearch(): void {
    this.searchTerm = '';
    this.filteredCocktails = this.cocktails;
  }

  viewRecipe(cocktailId: number | undefined): void {
    if (cocktailId) {
      this.router.navigate(['/visitor/recipe', cocktailId]);
    }
  }

  goBack(): void {
    this.router.navigate(['/visitor']);
  }

  getAlcoholType(abv: number): string {
    if (abv === 0) {
      return this.translateService.translate('visitor.cocktailList.nonAlcoholic');
    } else if (abv <= 10) {
      return this.translateService.translate('visitor.cocktailList.lowAlcohol');
    }
    return this.translateService.translate('visitor.cocktailList.alcoholic');
  }

  getAlcoholClass(abv: number): string {
    return abv > 0 ? 'alcoholic' : 'non-alcoholic';
  }

  usesSubstitutes(cocktail: Cocktail): boolean {
    return cocktail.id ? this.cocktailsWithSubstitutes.has(cocktail.id) : false;
  }

  usesAlternatives(cocktail: Cocktail): boolean {
    return cocktail.id ? this.cocktailsWithAlternatives.has(cocktail.id) : false;
  }
}
