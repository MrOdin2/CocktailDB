import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable, shareReplay, tap} from 'rxjs';
import {Cocktail} from '../models/models';
import {environment} from '../../environments/environment';

@Injectable({
    providedIn: 'root'
})
export class CocktailService {
    private baseUrl = `${environment.apiUrl}/cocktails`;
    private cache$?: Observable<Cocktail[]>;

    constructor(private http: HttpClient) {
    }

    getAll(forceRefresh: boolean = false): Observable<Cocktail[]> {
        if (!this.cache$ || forceRefresh) {
        return this.http.get<Cocktail[]>(this.baseUrl, {withCredentials: true}).pipe(
            shareReplay({bufferSize: 1, refCount: true})
        );}
        return this.cache$;
    }

    getById(id: number): Observable<Cocktail> {
        return this.http.get<Cocktail>(`${this.baseUrl}/${id}`, {withCredentials: true});
    }

    create(cocktail: Cocktail): Observable<Cocktail> {
        return this.http.post<Cocktail>(this.baseUrl, cocktail, {withCredentials: true}).pipe(
            tap(() => this.invalidateCache())
        );
    }

    update(id: number, cocktail: Cocktail): Observable<Cocktail> {
        return this.http.put<Cocktail>(`${this.baseUrl}/${id}`, cocktail, {withCredentials: true}).pipe(
            tap(() => this.invalidateCache())
        );
    }

    delete(id: number): Observable<void> {
        return this.http.delete<void>(`${this.baseUrl}/${id}`, {withCredentials: true}).pipe(
            tap(() => this.invalidateCache())
        );
    }

    getAvailable(): Observable<Cocktail[]> {
        return this.http.get<Cocktail[]>(`${this.baseUrl}/available`);
    }

    search(name?: string, spirit?: string, tags?: string[]): Observable<Cocktail[]> {
        let params: any = {};
        if (name) params.name = name;
        if (spirit) params.spirit = spirit;
        if (tags && tags.length > 0) params.tags = tags;

        return this.http.get<Cocktail[]>(`${this.baseUrl}/search`, {params, withCredentials: true});
    }

    invalidateCache(): void {
        this.cache$ = undefined;
    }
}

