import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { Cocktail } from '../../../models/models';
import {CocktailService} from "../../../services/cocktail.service";

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
    private cocktailService: CocktailService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadCocktails();
  }

  loadCocktails(): void {
    this.isLoading = true;
    
    this.cocktailService.getAll().subscribe({
      next: (cocktails: Cocktail[]) => {
        this.cocktails = cocktails;
        this.isLoading = false;
      },
      error: (error: any) => {
        console.error('Error loading cocktails:', error);
        this.isLoading = false;
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

  selectLetter(letter: string): void {
    const showOnlyAvailable = localStorage.getItem('showOnlyAvailable') === 'true';
    this.router.navigate(['/barkeeper/cocktails'], { 
      queryParams: { letter, availableOnly: showOnlyAvailable } 
    });
  }
}
