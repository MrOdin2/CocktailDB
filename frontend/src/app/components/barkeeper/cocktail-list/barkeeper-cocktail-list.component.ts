import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { ApiService } from '../../../services/api.service';
import { Cocktail } from '../../../models/models';

@Component({
  selector: 'app-barkeeper-cocktail-list',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './barkeeper-cocktail-list.component.html',
  styleUrls: ['./barkeeper-cocktail-list.component.css']
})
export class BarkeeperCocktailListComponent implements OnInit {
  cocktails: Cocktail[] = [];
  filteredCocktails: Cocktail[] = [];
  availableCocktails: Cocktail[] = [];
  isLoading = false;
  letter: string = '';
  availableOnly: boolean = false;
  viewMode: 'letter' | 'available' = 'letter';

  constructor(
    private apiService: ApiService,
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
      ? this.apiService.getAvailableCocktails()
      : this.apiService.getAllCocktails();

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
    this.apiService.getAvailableCocktails().subscribe({
      next: (cocktails: Cocktail[]) => {
        this.filteredCocktails = cocktails;
        this.isLoading = false;
      },
      error: (error: any) => {
        console.error('Error loading available cocktails:', error);
        this.isLoading = false;
      }
    });
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
