import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { Cocktail } from '../../../models/models';
import {CocktailService} from "../../../services/cocktail.service";

@Component({
  selector: 'app-barkeeper-cocktail-list',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './barkeeper-cocktail-list.component.html',
  styleUrls: ['../barkeeper-shared.css', './barkeeper-cocktail-list.component.css']
})
export class BarkeeperCocktailListComponent implements OnInit {
  cocktails: Cocktail[] = [];
  filteredCocktails: Cocktail[] = [];
  availableCocktails: Cocktail[] = [];
  isLoading = false;
  letter: string = '';
  availableOnly: boolean = false;
  viewMode: 'letter' | 'available' = 'letter';

  // Filter options for available cocktails
  filterBaseSpirit: string = 'all';
  filterTag: string = 'all';
  filterAbv: 'all' | 'low' | 'medium' | 'high' | 'non-alcoholic' = 'all';
  availableBaseSpirits: string[] = [];
  availableTags: string[] = [];
  showFilters: boolean = false;

  constructor(
    private cocktailService: CocktailService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
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

  loadCocktails(): void {
    this.isLoading = true;
    
    const cocktailsObservable = this.availableOnly 
      ? this.cocktailService.getAvailable()
      : this.cocktailService.getAll();

    cocktailsObservable.subscribe({
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

  loadAvailableCocktails(): void {
    this.isLoading = true;
    this.cocktailService.getAvailable().subscribe({
      next: (cocktails: Cocktail[]) => {
        this.availableCocktails = cocktails;
        this.extractFilterOptions(cocktails);
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
      this.filteredCocktails = this.cocktails.filter(c => 
        c.name.toUpperCase().startsWith(this.letter)
      );
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
      return 'Available Cocktails';
    }
    return this.letter ? `Cocktails - ${this.letter}` : 'All Cocktails';
  }
}
