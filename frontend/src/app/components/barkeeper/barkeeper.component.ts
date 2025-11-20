import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { ApiService } from '../../services/api.service';
import { AuthService } from '../../services/auth.service';
import { Cocktail, Ingredient } from '../../models/models';

@Component({
  selector: 'app-barkeeper',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './barkeeper.component.html',
  styleUrls: ['./barkeeper.component.css']
})
export class BarkeeperComponent implements OnInit {
  currentView: 'menu' | 'alphabet' | 'cocktails' | 'available' | 'random' | 'ingredients' | 'recipe' = 'menu';
  cocktails: Cocktail[] = [];
  availableCocktails: Cocktail[] = [];
  ingredients: Ingredient[] = [];
  filteredCocktails: Cocktail[] = [];
  selectedCocktail: Cocktail | null = null;
  selectedLetter: string = '';
  isLoading = false;
  showOnlyAvailable = false;
  
  // Filter options for random cocktail
  filterAlcoholic: 'all' | 'alcoholic' | 'non-alcoholic' = 'all';
  
  alphabet = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ'.split('');

  constructor(
    private apiService: ApiService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadData();
  }

  loadData(): void {
    this.isLoading = true;
    this.apiService.getAllCocktails().subscribe({
      next: (cocktails: Cocktail[]) => {
        this.cocktails = cocktails;
        this.isLoading = false;
      },
      error: (error: any) => {
        console.error('Error loading cocktails:', error);
        this.isLoading = false;
      }
    });

    this.apiService.getAllIngredients().subscribe({
      next: (ingredients: Ingredient[]) => {
        this.ingredients = ingredients;
      },
      error: (error: any) => {
        console.error('Error loading ingredients:', error);
      }
    });

    this.apiService.getAvailableCocktails().subscribe({
      next: (cocktails: Cocktail[]) => {
        this.availableCocktails = cocktails;
      },
      error: (error: any) => {
        console.error('Error loading available cocktails:', error);
      }
    });
  }

  showMenu(): void {
    this.currentView = 'menu';
  }

  showAlphabetNav(): void {
    this.currentView = 'alphabet';
  }

  showAvailableCocktails(): void {
    this.currentView = 'available';
    this.filteredCocktails = this.availableCocktails;
  }

  showIngredients(): void {
    this.currentView = 'ingredients';
  }

  showRandomCocktail(): void {
    this.currentView = 'random';
  }

  selectLetter(letter: string): void {
    this.selectedLetter = letter;
    const baseList = this.showOnlyAvailable ? this.availableCocktails : this.cocktails;
    this.filteredCocktails = baseList.filter(c => 
      c.name.toUpperCase().startsWith(letter)
    );
    this.currentView = 'cocktails';
  }

  selectCocktail(cocktail: Cocktail): void {
    this.selectedCocktail = cocktail;
    this.currentView = 'recipe';
  }

  pickRandomCocktail(): void {
    let pool = this.showOnlyAvailable ? this.availableCocktails : this.cocktails;
    
    if (this.filterAlcoholic === 'alcoholic') {
      pool = pool.filter(c => this.isAlcoholic(c));
    } else if (this.filterAlcoholic === 'non-alcoholic') {
      pool = pool.filter(c => !this.isAlcoholic(c));
    }
    
    if (pool.length > 0) {
      const randomIndex = Math.floor(Math.random() * pool.length);
      this.selectCocktail(pool[randomIndex]);
    }
  }

  isAlcoholic(cocktail: Cocktail): boolean {
    return cocktail.ingredients.some(ci => {
      const ingredient = this.ingredients.find(i => i.id === ci.ingredientId);
      return ingredient && ingredient.abv > 0;
    });
  }

  toggleIngredientStock(ingredient: Ingredient): void {
    const updated = { ...ingredient, inStock: !ingredient.inStock };
    this.apiService.updateIngredient(ingredient.id!, updated).subscribe({
      next: () => {
        ingredient.inStock = !ingredient.inStock;
        // Reload available cocktails
        this.apiService.getAvailableCocktails().subscribe({
          next: (cocktails: Cocktail[]) => {
            this.availableCocktails = cocktails;
          }
        });
      },
      error: (error: any) => {
        console.error('Error updating ingredient:', error);
      }
    });
  }

  getIngredientName(ingredientId: number): string {
    const ingredient = this.ingredients.find(i => i.id === ingredientId);
    return ingredient ? ingredient.name : 'Unknown';
  }

  logout(): void {
    this.authService.logout().subscribe({
      next: () => {
        this.router.navigate(['/login']);
      }
    });
  }
}
