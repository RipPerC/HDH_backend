import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { FilmService } from './film.service';
import { Film } from '../pages/model/film.model';

describe('FilmService', () => {
  let service: FilmService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [FilmService],
    });
    service = TestBed.inject(FilmService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should load films via GET', () => {
    const dummyFilms: Film[] = [
      { id: 1, title: 'Film 1', year: 2020, downloaded: true },
      { id: 2, title: 'Film 2', year: 2021, downloaded: false },
    ];

    service.loadFilms().subscribe((films) => {
      expect(films?.length).toBe(2);
      expect(films).toEqual(dummyFilms);
    });

    const req = httpMock.expectOne((r) => r.url.endsWith('/film'));
    expect(req.request.method).toBe('GET');
    req.flush(dummyFilms);
  });

  it('should delete a film with clean URL without trailing space', () => {
    const film: Film = { id: 42, title: 'Delete Me', year: 2022, downloaded: false };

    service.deleteFilm(film).subscribe();

    const req = httpMock.expectOne((r) => r.url.endsWith('/film/42'));
    expect(req.request.method).toBe('DELETE');
    expect(req.request.url.endsWith('/film/42')).toBeTrue();
    expect(req.request.url.includes('/42 ')).toBeFalse();
    req.flush(null);
  });

  it('should update an existing film via PUT when id is present', () => {
    const film: Film = { id: 5, title: 'Existing', year: 2020, downloaded: true };

    service.updateOrCreateFilm(film).subscribe((res) => {
      expect(res.title).toBe('Existing');
    });

    const req = httpMock.expectOne((r) => r.url.endsWith('/film/5'));
    expect(req.request.method).toBe('PUT');
    req.flush(film);
  });

  it('should create a new film via POST when id is undefined', () => {
    const film: Film = { title: 'New Film', year: 2024, downloaded: false };

    service.updateOrCreateFilm(film).subscribe((res) => {
      expect(res.id).toBe(10);
    });

    const req = httpMock.expectOne((r) => r.url.endsWith('/film'));
    expect(req.request.method).toBe('POST');
    req.flush({ ...film, id: 10 });
  });
});
