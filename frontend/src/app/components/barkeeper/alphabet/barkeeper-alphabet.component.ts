import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { ApiService } from '../../../services/api.service';
import { TranslatePipe } from '../../../pipes/translate.pipe';
import { Cocktail, CocktailsWithSubstitutions } from '../../../models/models';

@Component({
  selector: 'app-barkeeper-alphabet',
  standalone: true,
  imports: [CommonModule, RouterModule, TranslatePipe],
  templateUrl: './barkeeper-alphabet.component.html',
  styleUrls: ['../barkeeper-shared.css', './barkeeper-alphabet.component.css']
})
export class BarkeeperAlphabetComponent implements OnInit {
  alphabet = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ'.split('');
  cocktails: Cocktail[] = [];
  availableCocktails: Cocktail[] = [];
  cocktailsWithSubstitutions: CocktailsWithSubstitutions | null = null;
  isLoading = false;

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

  selectLetter(letter: string): void {
    const showOnlyAvailable = localStorage.getItem('showOnlyAvailable') === 'true';
    this.router.navigate(['/barkeeper/cocktails'], { 
      queryParams: { letter, availableOnly: showOnlyAvailable } 
    });
  }
}
