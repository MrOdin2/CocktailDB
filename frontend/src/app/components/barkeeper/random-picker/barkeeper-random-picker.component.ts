import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { ApiService } from '../../../services/api.service';
import { TranslatePipe } from '../../../pipes/translate.pipe';
import { Cocktail } from '../../../models/models';

@Component({
  selector: 'app-barkeeper-random-picker',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule, TranslatePipe],
  templateUrl: './barkeeper-random-picker.component.html',
  styleUrls: ['../barkeeper-shared.css', './barkeeper-random-picker.component.css']
})
export class BarkeeperRandomPickerComponent implements OnInit {
  cocktails: Cocktail[] = [];
  availableCocktails: Cocktail[] = [];
  availableBaseSpirits: string[] = [];
  isLoading = false;
  
  filterAlcoholic: 'all' | 'alcoholic' | 'non-alcoholic' = 'all';
  filterBaseSpirit: string = 'all';

  constructor(
    private apiService: ApiService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadCocktails();
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

    this.apiService.getAvailableCocktails().subscribe({
      next: (cocktails: Cocktail[]) => {
        this.availableCocktails = cocktails;
      },
      error: (error: any) => {
        console.error('Error loading available cocktails:', error);
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
