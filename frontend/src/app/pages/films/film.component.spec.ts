import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';
import { FilmComponent } from './film.component';
import { FilmService } from 'src/app/services/film.service';

describe('FilmComponent', () => {
  let component: FilmComponent;
  let fixture: ComponentFixture<FilmComponent>;
  let filmServiceSpy: jasmine.SpyObj<FilmService>;

  beforeEach(async () => {
    filmServiceSpy = jasmine.createSpyObj('FilmService', ['updateOrCreateFilm']);

    await TestBed.configureTestingModule({
      declarations: [FilmComponent],
      imports: [ReactiveFormsModule, RouterTestingModule],
      providers: [{ provide: FilmService, useValue: filmServiceSpy }],
    }).compileComponents();

    fixture = TestBed.createComponent(FilmComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create and initialize form with boolean downloaded', () => {
    expect(component).toBeTruthy();
    expect(component.filmForm).toBeDefined();
    expect(component.filmForm.get('downloaded')?.value).toBe(false);
  });

  it('should be invalid when required fields are empty', () => {
    component.filmForm.patchValue({
      title: '',
      year: '',
    });
    expect(component.filmForm.valid).toBeFalse();
  });

  it('should not call filmService if form is invalid on save', () => {
    component.filmForm.patchValue({
      title: '',
      year: '',
    });
    component.saveFilm();

    expect(filmServiceSpy.updateOrCreateFilm).not.toHaveBeenCalled();
  });

  it('should call filmService with valid form data', () => {
    filmServiceSpy.updateOrCreateFilm.and.returnValue(
      of({ id: 1, title: 'Valid Title', year: 2022, downloaded: false })
    );

    component.filmForm.patchValue({
      title: 'Valid Title',
      year: 2022,
      downloaded: false,
    });
    component.saveFilm();

    expect(filmServiceSpy.updateOrCreateFilm).toHaveBeenCalled();
  });
});
