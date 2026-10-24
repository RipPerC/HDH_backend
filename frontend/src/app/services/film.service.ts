import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from 'src/environments/environment';
import { Film } from '../pages/model/film.model';

const base_url = environment.base_url;

@Injectable({
  providedIn: 'root',
})
export class FilmService {
  private url_films = `${base_url}/film`;

  constructor(private http: HttpClient) {}

  loadFilms() {
    return this.http.get(this.url_films);
  }

  deleteFilm(film: Film) {
    return this.http.delete(`${this.url_films}/${film.id} `);
  }

  updateOrCreateFilm(film: Film) {
    return this.http.post(this.url_films, film);
  }
}
