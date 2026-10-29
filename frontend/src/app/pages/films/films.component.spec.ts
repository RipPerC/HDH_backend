import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of, throwError } from 'rxjs';
import { FilmsComponent } from './films.component';
import { FilmService } from 'src/app/services/film.service';
import { Film } from '../model/film.model';
import { FormsModule } from '@angular/forms';
import { NgxPaginationModule } from 'ngx-pagination';
import { RouterTestingModule } from '@angular/router/testing';

describe('FilmsComponent', () => {
  let component: FilmsComponent;
  let fixture: ComponentFixture<FilmsComponent>;
  let filmServiceSpy: jasmine.SpyObj<FilmService>;

  const mockFilms: Film[] = [
    { id: 1, title: 'Inception', year: 2010, downloaded: true },
    { id: 2, title: 'Avatar', year: 2009, downloaded: false },
    { id: 3, title: 'Interstellar', year: 2014, downloaded: true },
  ];

  beforeEach(async () => {
    filmServiceSpy = jasmine.createSpyObj('FilmService', [
      'loadFilms',
      'updateOrCreateFilm',
      'deleteFilm',
    ]);

    filmServiceSpy.loadFilms.and.returnValue(of(mockFilms));

    await TestBed.configureTestingModule({
      declarations: [FilmsComponent],
      imports: [FormsModule, NgxPaginationModule, RouterTestingModule],
      providers: [{ provide: FilmService, useValue: filmServiceSpy }],
    }).compileComponents();

    fixture = TestBed.createComponent(FilmsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create and load films on init', () => {
    expect(component).toBeTruthy();
    expect(component.allFilms.length).toBe(3);
    expect(component.total).toBe(3);
    expect(component.loading).toBeFalse();
  });

  it('should handle 204 null response safely without throwing error', () => {
    filmServiceSpy.loadFilms.and.returnValue(of(null));
    component.loadFilms();

    expect(component.allFilms).toEqual([]);
    expect(component.total).toBe(0);
    expect(component.loading).toBeFalse();
    expect(component.errorMessage).toBe('');
  });

  it('should handle HTTP error gracefully', () => {
    filmServiceSpy.loadFilms.and.returnValue(throwError(() => new Error('Server error')));
    component.loadFilms();

    expect(component.allFilms).toEqual([]);
    expect(component.total).toBe(0);
    expect(component.loading).toBeFalse();
    expect(component.errorMessage).toContain('Could not load films');
  });

  it('should search by title accurately without mutating master list destructively', () => {
    component.txtSearchByTitle = 'inter';
    component.searchByTitle();

    expect(component.allFilms.length).toBe(1);
    expect(component.allFilms[0].title).toBe('Interstellar');

    // Clearing search should restore original list
    component.txtSearchByTitle = '';
    component.searchByTitle();
    expect(component.allFilms.length).toBe(3);
  });

  it('should search by year accurately', () => {
    component.txtSearchByYear = '2009';
    component.searchByYear();

    expect(component.allFilms.length).toBe(1);
    expect(component.allFilms[0].title).toBe('Avatar');

    component.txtSearchByYear = '';
    component.searchByYear();
    expect(component.allFilms.length).toBe(3);
  });
});
