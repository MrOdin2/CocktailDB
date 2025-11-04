import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Ingredient, Cocktail } from '../models/models';

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  private baseUrl = 'http://localhost:8080/api';

  constructor(private http: HttpClient) {}

  // Ingredient endpoints
  getAllIngredients(): Observable<Ingredient[]> {
    return this.http.get<Ingredient[]>(`${this.baseUrl}/ingredients`);
  }

  getIngredientById(id: number): Observable<Ingredient> {
    return this.http.get<Ingredient>(`${this.baseUrl}/ingredients/${id}`);
  }

  createIngredient(ingredient: Ingredient): Observable<Ingredient> {
    return this.http.post<Ingredient>(`${this.baseUrl}/ingredients`, ingredient);
  }

  updateIngredient(id: number, ingredient: Ingredient): Observable<Ingredient> {
    return this.http.put<Ingredient>(`${this.baseUrl}/ingredients/${id}`, ingredient);
  }

  deleteIngredient(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/ingredients/${id}`);
  }

  getInStockIngredients(): Observable<Ingredient[]> {
    return this.http.get<Ingredient[]>(`${this.baseUrl}/ingredients/in-stock`);
  }

  // Cocktail endpoints
  getAllCocktails(): Observable<Cocktail[]> {
    return this.http.get<Cocktail[]>(`${this.baseUrl}/cocktails`);
  }

  getCocktailById(id: number): Observable<Cocktail> {
    return this.http.get<Cocktail>(`${this.baseUrl}/cocktails/${id}`);
  }

  createCocktail(cocktail: Cocktail): Observable<Cocktail> {
    return this.http.post<Cocktail>(`${this.baseUrl}/cocktails`, cocktail);
  }

  updateCocktail(id: number, cocktail: Cocktail): Observable<Cocktail> {
    return this.http.put<Cocktail>(`${this.baseUrl}/cocktails/${id}`, cocktail);
  }

  deleteCocktail(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/cocktails/${id}`);
  }

  getAvailableCocktails(): Observable<Cocktail[]> {
    return this.http.get<Cocktail[]>(`${this.baseUrl}/cocktails/available`);
  }

  searchCocktails(name?: string, spirit?: string, tags?: string[]): Observable<Cocktail[]> {
    let params: any = {};
    if (name) params.name = name;
    if (spirit) params.spirit = spirit;
    if (tags && tags.length > 0) params.tags = tags;
    
    return this.http.get<Cocktail[]>(`${this.baseUrl}/cocktails/search`, { params });
  }
}
