import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { ApiService } from '../../../services/api.service';
import { Cocktail } from '../../../models/models';

@Component({
  selector: 'app-visitor-cocktail-list',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './visitor-cocktail-list.component.html',
  styleUrls: ['./visitor-cocktail-list.component.css']
})
export class VisitorCocktailListComponent implements OnInit {
  cocktails: Cocktail[] = [];
  filteredCocktails: Cocktail[] = [];
  searchTerm: string = '';
  loading: boolean = false;
  error: string | null = null;

  constructor(
    private apiService: ApiService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadCocktails();
  }

  loadCocktails(): void {
    this.loading = true;
    this.error = null;
    
    // Load only available cocktails for visitors
    this.apiService.getAvailableCocktails().subscribe({
      next: (data) => {
        this.cocktails = data;
        this.filteredCocktails = data;
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
    return abv > 0 ? 'Alcoholic' : 'Non-Alcoholic';
  }

  getAlcoholClass(abv: number): string {
    return abv > 0 ? 'alcoholic' : 'non-alcoholic';
  }
}
