import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Ingredient, Cocktail, CocktailsWithSubstitutions } from '../models/models';
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
    return this.http.get<Cocktail[]>(`${this.baseUrl}/cocktails/available`);
  }

  getAvailableCocktailsWithSubstitutions(): Observable<CocktailsWithSubstitutions> {
    return this.http.get<CocktailsWithSubstitutions>(`${this.baseUrl}/cocktails/available-with-substitutions`);
  }

  searchCocktails(name?: string, spirit?: string, tags?: string[]): Observable<Cocktail[]> {
    let params: any = {};
    if (name) params.name = name;
    if (spirit) params.spirit = spirit;
    if (tags && tags.length > 0) params.tags = tags;
    
    return this.http.get<Cocktail[]>(`${this.baseUrl}/cocktails/search`, { params, withCredentials: true });
  }

  // Settings endpoints
  getTheme(): Observable<{ theme: string }> {
    return this.http.get<{ theme: string }>(`${this.baseUrl}/settings/theme`);
  }

  setTheme(theme: string): Observable<{ theme: string }> {
    return this.http.put<{ theme: string }>(`${this.baseUrl}/settings/theme`, { theme }, { withCredentials: true });
  }

  // CSV Import/Export endpoints
  exportCocktailsCsv(): Observable<Blob> {
    return this.http.get(`${this.baseUrl}/cocktails/export/csv`, { 
      responseType: 'blob',
      withCredentials: true 
    });
  }

  importCocktailsCsv(file: File): Observable<CsvImportResult> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post<CsvImportResult>(`${this.baseUrl}/cocktails/import/csv`, formData, { 
      withCredentials: true 
    });
  }

  exportIngredientsCsv(): Observable<Blob> {
    return this.http.get(`${this.baseUrl}/ingredients/export/csv`, { 
      responseType: 'blob',
      withCredentials: true 
    });
  }

  importIngredientsCsv(file: File): Observable<IngredientCsvImportResult> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post<IngredientCsvImportResult>(`${this.baseUrl}/ingredients/import/csv`, formData, { 
      withCredentials: true 
    });
  }
}

export interface CsvImportError {
  row: number;
  message: string;
  data: string;
}

export interface CsvImportResult {
  imported: Cocktail[];
  errors: CsvImportError[];
}

export interface IngredientCsvImportResult {
  imported: Ingredient[];
  errors: CsvImportError[];
}
