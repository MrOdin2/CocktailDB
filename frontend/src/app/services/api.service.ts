import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Ingredient, Cocktail } from '../models/models';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  private baseUrl = environment.apiUrl;

  constructor(private http: HttpClient) {}

  // Ingredient endpoints
  getAllIngredients(): Observable<Ingredient[]> {
    return this.http.get<Ingredient[]>(`${this.baseUrl}/ingredients`, { withCredentials: true });
  }

  getIngredientById(id: number): Observable<Ingredient> {
    return this.http.get<Ingredient>(`${this.baseUrl}/ingredients/${id}`, { withCredentials: true });
  }

  createIngredient(ingredient: Ingredient): Observable<Ingredient> {
    return this.http.post<Ingredient>(`${this.baseUrl}/ingredients`, ingredient, { withCredentials: true });
  }

  updateIngredient(id: number, ingredient: Ingredient): Observable<Ingredient> {
    return this.http.put<Ingredient>(`${this.baseUrl}/ingredients/${id}`, ingredient, { withCredentials: true });
  }

  deleteIngredient(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/ingredients/${id}`, { withCredentials: true });
  }

  getInStockIngredients(): Observable<Ingredient[]> {
    return this.http.get<Ingredient[]>(`${this.baseUrl}/ingredients/in-stock`, { withCredentials: true });
  }

  // Cocktail endpoints
  getAllCocktails(): Observable<Cocktail[]> {
    return this.http.get<Cocktail[]>(`${this.baseUrl}/cocktails`, { withCredentials: true });
  }

  getCocktailById(id: number): Observable<Cocktail> {
    return this.http.get<Cocktail>(`${this.baseUrl}/cocktails/${id}`, { withCredentials: true });
  }

  createCocktail(cocktail: Cocktail): Observable<Cocktail> {
    return this.http.post<Cocktail>(`${this.baseUrl}/cocktails`, cocktail, { withCredentials: true });
  }

  updateCocktail(id: number, cocktail: Cocktail): Observable<Cocktail> {
    return this.http.put<Cocktail>(`${this.baseUrl}/cocktails/${id}`, cocktail, { withCredentials: true });
  }

  deleteCocktail(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/cocktails/${id}`, { withCredentials: true });
  }

  getAvailableCocktails(): Observable<Cocktail[]> {
    return this.http.get<Cocktail[]>(`${this.baseUrl}/cocktails/available`, { withCredentials: true });
  }

  searchCocktails(name?: string, spirit?: string, tags?: string[]): Observable<Cocktail[]> {
    let params: any = {};
    if (name) params.name = name;
    if (spirit) params.spirit = spirit;
    if (tags && tags.length > 0) params.tags = tags;
    
    return this.http.get<Cocktail[]>(`${this.baseUrl}/cocktails/search`, { params, withCredentials: true });
  }
}
