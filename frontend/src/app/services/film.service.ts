import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { Film } from '../pages/model/film.model';

const base_url = environment.base_url;

@Injectable({
  providedIn: 'root',
})
export class FilmService {
  private url_films = `${base_url}/film`;

  constructor(private http: HttpClient) {}

  loadFilms(): Observable<Film[] | null> {
    return this.http.get<Film[] | null>(this.url_films);
  }

  getFilmById(id: number): Observable<Film> {
    return this.http.get<Film>(`${this.url_films}/${id}`);
  }

  deleteFilm(film: Film): Observable<void> {
    return this.http.delete<void>(`${this.url_films}/${film.id}`);
  }

  createFilm(film: Film): Observable<Film> {
    return this.http.post<Film>(this.url_films, film);
  }

  updateFilm(id: number, film: Film): Observable<Film> {
    return this.http.put<Film>(`${this.url_films}/${id}`, film);
  }

  updateOrCreateFilm(film: Film): Observable<Film> {
    if (film.id && film.id > 0) {
      return this.updateFilm(film.id, film);
    }
    return this.createFilm(film);
  }
}
