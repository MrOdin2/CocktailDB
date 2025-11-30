import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Cocktail } from '../models/models';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class CocktailService {
  private baseUrl = `${environment.apiUrl}/cocktails`;

  constructor(private http: HttpClient) {}

  getAll(): Observable<Cocktail[]> {
    return this.http.get<Cocktail[]>(this.baseUrl, { withCredentials: true });
  }

  getById(id: number): Observable<Cocktail> {
    return this.http.get<Cocktail>(`${this.baseUrl}/${id}`, { withCredentials: true });
  }

  create(cocktail: Cocktail): Observable<Cocktail> {
    return this.http.post<Cocktail>(this.baseUrl, cocktail, { withCredentials: true });
  }

  update(id: number, cocktail: Cocktail): Observable<Cocktail> {
    return this.http.put<Cocktail>(`${this.baseUrl}/${id}`, cocktail, { withCredentials: true });
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`, { withCredentials: true });
  }

  getAvailable(): Observable<Cocktail[]> {
    return this.http.get<Cocktail[]>(`${this.baseUrl}/available`);
  }

  search(name?: string, spirit?: string, tags?: string[]): Observable<Cocktail[]> {
    let params: any = {};
    if (name) params.name = name;
    if (spirit) params.spirit = spirit;
    if (tags && tags.length > 0) params.tags = tags;

    return this.http.get<Cocktail[]>(`${this.baseUrl}/search`, { params, withCredentials: true });
  }
}

