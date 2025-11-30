import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable, shareReplay, tap} from 'rxjs';
import {Ingredient} from '../models/models';
import {environment} from '../../environments/environment';

@Injectable({
    providedIn: 'root'
})
export class IngredientService {
    private baseUrl = `${environment.apiUrl}/ingredients`;
    private cache$?: Observable<Ingredient[]>;

    constructor(private http: HttpClient) {
    }

    getAll(forceRefresh: boolean = false): Observable<Ingredient[]> {
        if (!this.cache$ || forceRefresh) {
            this.cache$ = this.http.get<Ingredient[]>(this.baseUrl, {withCredentials: true}).pipe(
                shareReplay({bufferSize: 1, refCount: true})
            );
        }
        return this.cache$;
    }

    getById(id: number): Observable<Ingredient> {
        return this.http.get<Ingredient>(`${this.baseUrl}/${id}`, {withCredentials: true});
    }

    create(ingredient: Ingredient): Observable<Ingredient> {
        return this.http.post<Ingredient>(this.baseUrl, ingredient, {withCredentials: true}).pipe(
            tap(() => this.invalidateCache())
        );
    }

    update(id: number, ingredient: Ingredient): Observable<Ingredient> {
        return this.http.put<Ingredient>(`${this.baseUrl}/${id}`, ingredient, {withCredentials: true}).pipe(
            tap(() => this.invalidateCache())
        );
    }

    delete(id: number): Observable<void> {
        return this.http.delete<void>(`${this.baseUrl}/${id}`, {withCredentials: true}).pipe(
            tap(() => this.invalidateCache())
        );
    }

    getInStock(): Observable<Ingredient[]> {
        return this.http.get<Ingredient[]>(`${this.baseUrl}/in-stock`, {withCredentials: true});
    }

    invalidateCache(): void {
        this.cache$ = undefined;
    }
}

