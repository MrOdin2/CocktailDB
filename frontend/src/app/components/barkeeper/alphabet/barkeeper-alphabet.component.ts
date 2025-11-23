import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { ApiService } from '../../../services/api.service';
import { Cocktail } from '../../../models/models';

@Component({
  selector: 'app-barkeeper-alphabet',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './barkeeper-alphabet.component.html',
  styleUrls: ['../barkeeper-shared.css', './barkeeper-alphabet.component.css']
})
export class BarkeeperAlphabetComponent implements OnInit {
  alphabet = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ'.split('');
  cocktails: Cocktail[] = [];
  availableCocktails: Cocktail[] = [];
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

    this.apiService.getAvailableCocktails().subscribe({
      next: (cocktails: Cocktail[]) => {
        this.availableCocktails = cocktails;
      },
      error: (error: any) => {
        console.error('Error loading available cocktails:', error);
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
