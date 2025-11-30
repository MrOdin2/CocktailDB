import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { CocktailService } from '../../../services/cocktail.service';
import { Cocktail } from '../../../models/models';

interface Category {
  id: string;
  name: string;
  icon: string;
  description: string;
  filter: (cocktail: Cocktail) => boolean;
}

@Component({
  selector: 'app-visitor-categories',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './visitor-categories.component.html',
  styleUrls: ['./visitor-categories.component.css']
})
export class VisitorCategoriesComponent implements OnInit {
  allCocktails: Cocktail[] = [];
  selectedCategory: Category | null = null;
  filteredCocktails: Cocktail[] = [];
  loading: boolean = false;
  error: string | null = null;

  categories: Category[] = [
    {
      id: 'alcoholic',
      name: 'Alcoholic',
      icon: '🍸',
      description: 'Cocktails with alcohol',
      filter: (c) => c.abv > 0
    },
    {
      id: 'non-alcoholic',
      name: 'Non-Alcoholic',
      icon: '🧃',
      description: 'Mocktails and alcohol-free drinks',
      filter: (c) => c.abv === 0
    },
    {
      id: 'strong',
      name: 'Strong',
      icon: '💪',
      description: 'High ABV cocktails (>20%)',
      filter: (c) => c.abv > 20
    },
    {
      id: 'light',
      name: 'Light',
      icon: '🌸',
      description: 'Low ABV cocktails (1-10%)',
      filter: (c) => c.abv > 0 && c.abv <= 10
    },
    {
      id: 'medium',
      name: 'Medium',
      icon: '⚖️',
      description: 'Medium ABV cocktails (11-20%)',
      filter: (c) => c.abv > 10 && c.abv <= 20
    }
  ];

  constructor(
    private cocktailService: CocktailService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadCocktails();
  }

  loadCocktails(): void {
    this.loading = true;
    this.error = null;

    this.cocktailService.getAvailable().subscribe({
      next: (cocktails) => {
        this.allCocktails = cocktails;
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading cocktails:', err);
        this.error = 'Failed to load cocktails. Please try again.';
        this.loading = false;
      }
    });
  }

  selectCategory(category: Category): void {
    this.selectedCategory = category;
    this.filteredCocktails = this.allCocktails.filter(category.filter);
  }

  getCategoryCount(category: Category): number {
    return this.allCocktails.filter(category.filter).length;
  }

  backToCategories(): void {
    this.selectedCategory = null;
    this.filteredCocktails = [];
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
