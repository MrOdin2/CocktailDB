import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Ingredient } from '../models/models';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class IngredientService {
  private baseUrl = `${environment.apiUrl}/ingredients`;

  constructor(private http: HttpClient) {}

  getAll(): Observable<Ingredient[]> {
    return this.http.get<Ingredient[]>(this.baseUrl, { withCredentials: true });
  }

  getById(id: number): Observable<Ingredient> {
    return this.http.get<Ingredient>(`${this.baseUrl}/${id}`, { withCredentials: true });
  }

  create(ingredient: Ingredient): Observable<Ingredient> {
    return this.http.post<Ingredient>(this.baseUrl, ingredient, { withCredentials: true });
  }

  update(id: number, ingredient: Ingredient): Observable<Ingredient> {
    return this.http.put<Ingredient>(`${this.baseUrl}/${id}`, ingredient, { withCredentials: true });
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`, { withCredentials: true });
  }

  getInStock(): Observable<Ingredient[]> {
    return this.http.get<Ingredient[]>(`${this.baseUrl}/in-stock`, { withCredentials: true });
  }
}

