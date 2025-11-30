import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { Subscription } from 'rxjs';
import { AuthService } from '../../services/auth.service';
import { MeasureService } from '../../services/measure.service';
import { Cocktail, Ingredient, MeasureUnit } from '../../models/models';
import {IngredientService} from "../../services/ingredient.service";
import {CocktailService} from "../../services/cocktail.service";

@Component({
  selector: 'app-barkeeper',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './barkeeper.component.html',
  styleUrls: ['./barkeeper.component.css']
})
export class BarkeeperComponent implements OnInit, OnDestroy {
  currentView: 'menu' | 'alphabet' | 'cocktails' | 'available' | 'random' | 'ingredients' | 'recipe' = 'menu';
  previousView: 'menu' | 'alphabet' | 'cocktails' | 'available' | 'random' | 'ingredients' = 'menu';
  cocktails: Cocktail[] = [];
  availableCocktails: Cocktail[] = [];
  ingredients: Ingredient[] = [];
  filteredCocktails: Cocktail[] = [];
  selectedCocktail: Cocktail | null = null;
  selectedLetter: string = '';
  isLoading = false;
  showOnlyAvailable = false;
  
  // Measurement unit
  currentUnit: MeasureUnit = MeasureUnit.ML;
  private unitSubscription?: Subscription;

  // Filter options for random cocktail
  filterAlcoholic: 'all' | 'alcoholic' | 'non-alcoholic' = 'all';
  filterBaseSpirit: string = 'all';  // Added for base spirit filtering
  availableBaseSpirits: string[] = [];  // Will be populated from cocktails

  alphabet = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ'.split('');

  // Performance optimization: Map for O(1) ingredient lookups
  private ingredientMap: Map<number, Ingredient> = new Map();

  constructor(
    private ingredientService: IngredientService,
    private cocktailService: CocktailService,
    private authService: AuthService,
    private measureService: MeasureService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadData();
    
    this.unitSubscription = this.measureService.getUnit().subscribe(unit => {
      this.currentUnit = unit;
    });
  }
  
  ngOnDestroy(): void {
    this.unitSubscription?.unsubscribe();
  }

  loadData(): void {
    this.isLoading = true;
    this.cocktailService.getAll().subscribe({
      next: (cocktails: Cocktail[]) => {
        this.cocktails = cocktails;
        // Extract unique base spirits
        const spiritsSet = new Set(cocktails.map(c => c.baseSpirit || 'none'));
        this.availableBaseSpirits = Array.from(spiritsSet).sort();
        this.isLoading = false;
      },
      error: (error: any) => {
        console.error('Error loading cocktails:', error);
        this.isLoading = false;
      }
    });

    this.ingredientService.getAll().subscribe({
      next: (ingredients: Ingredient[]) => {
        this.ingredients = ingredients;
        // Build ingredient map for O(1) lookups
        this.ingredientMap = new Map(ingredients.map(i => [i.id!, i]));
      },
      error: (error: any) => {
        console.error('Error loading ingredients:', error);
      }
    });

    this.cocktailService.getAvailable().subscribe({
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
    this.previousView = 'alphabet';
    this.currentView = 'cocktails';
  }

  selectCocktail(cocktail: Cocktail): void {
    this.selectedCocktail = cocktail;
    this.previousView = this.currentView as any;
    this.currentView = 'recipe';
  }

  goBackFromRecipe(): void {
    this.currentView = this.previousView;
  }

  pickRandomCocktail(): void {
    let pool = this.showOnlyAvailable ? this.availableCocktails : this.cocktails;

    // Filter by alcoholic/non-alcoholic
    if (this.filterAlcoholic === 'alcoholic') {
      pool = pool.filter(c => c.abv > 0);
    } else if (this.filterAlcoholic === 'non-alcoholic') {
      pool = pool.filter(c => c.abv === 0);
    }

    // Filter by base spirit
    if (this.filterBaseSpirit !== 'all') {
      pool = pool.filter(c => c.baseSpirit === this.filterBaseSpirit);
    }

    if (pool.length > 0) {
      const randomIndex = Math.floor(Math.random() * pool.length);
      this.selectCocktail(pool[randomIndex]);
    }
  }

  isAlcoholic(cocktail: Cocktail): boolean {
    return cocktail.ingredients.some(ci => {
      const ingredient = this.ingredientMap.get(ci.ingredientId);
      return ingredient && ingredient.abv > 0;
    });
  }

  toggleIngredientStock(ingredient: Ingredient): void {
    const updated = { ...ingredient, inStock: !ingredient.inStock };
    this.ingredientService.update(ingredient.id!, updated).subscribe({
      next: () => {
        ingredient.inStock = !ingredient.inStock;
        // Reload available cocktails
        this.cocktailService.getAvailable().subscribe({
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
    const ingredient = this.ingredientMap.get(ingredientId);
    return ingredient ? ingredient.name : 'Unknown';
  }
  
  formatMeasure(measureMl: number): string {
    return this.measureService.formatMeasure(measureMl, this.currentUnit);
  }

  logout(): void {
    this.authService.logout().subscribe({
      next: () => {
        this.router.navigate(['/login']);
      }
    });
  }
}
