import { Component, OnDestroy, OnInit } from '@angular/core';
import Swal from 'sweetalert2';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

import { Film } from '../model/film.model';
import { FilmService } from 'src/app/services/film.service';

const noSort = 'assets/images/sort/stay.png';
const up = 'assets/images/sort/up.png';
const down = 'assets/images/sort/down.png';

@Component({
  selector: 'app-films',
  templateUrl: './films.component.html',
  styleUrls: [],
})
export class FilmsComponent implements OnInit, OnDestroy {
  public allFilms: Film[] = [];
  public allFilmsAux: Film[] = [];
  public pages: number = 1;
  public perPage: number = 10;
  public total: number = 0;
  public loading: boolean = true;
  public errorMessage: string = '';
  public itemsPerPage = [5, 10, 50, 100];
  public sortYear: string = noSort;
  public sortTitle: string = noSort;
  public txtSearchByYear: string = '';
  public txtSearchByTitle: string = '';

  private destroy$ = new Subject<void>();

  constructor(private filmService: FilmService) {}

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  ngOnInit(): void {
    this.loadFilms();
  }

  loadFilms(): void {
    this.loading = true;
    this.errorMessage = '';
    this.filmService
      .loadFilms()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (response: Film[] | null) => {
          this.allFilms = response ?? [];
          this.allFilmsAux = [...this.allFilms];
          this.total = this.allFilms.length;
          this.loading = false;
        },
        error: () => {
          this.allFilms = [];
          this.allFilmsAux = [];
          this.total = 0;
          this.loading = false;
          this.errorMessage = 'Could not load films. Please verify server connection and credentials.';
        },
      });
  }

  searchByTitle(): Film[] {
    const term = this.txtSearchByTitle.trim().toLowerCase();
    if (!term) {
      this.allFilms = [...this.allFilmsAux];
      this.total = this.allFilms.length;
      return this.allFilms;
    }

    this.allFilms = this.allFilmsAux.filter((obj) =>
      obj.title ? obj.title.toLowerCase().includes(term) : false
    );
    this.total = this.allFilms.length;
    return this.allFilms;
  }

  searchByYear(): Film[] {
    const term = this.txtSearchByYear.trim();
    if (!term) {
      this.allFilms = [...this.allFilmsAux];
      this.total = this.allFilms.length;
      return this.allFilms;
    }

    this.allFilms = this.allFilmsAux.filter((obj) =>
      obj.year != null ? obj.year.toString().includes(term) : false
    );
    this.total = this.allFilms.length;
    return this.allFilms;
  }

  editFilm(film: Film): void {
    this.filmService
      .updateOrCreateFilm(film)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          Swal.fire({
            title: 'Updated!',
            text: `Film: ${film.title}`,
            icon: 'success',
          });
          this.loadFilms();
        },
        error: () => {
          Swal.fire({
            title: 'Error',
            text: 'Could not update film.',
            icon: 'error',
          });
        },
      });
  }

  deleteFilm(film: Film): void {
    Swal.fire({
      title: 'Are you sure?',
      text: `Film to remove: ${film.title}`,
      icon: 'question',
      showCancelButton: true,
      confirmButtonColor: '#3085d6',
      cancelButtonColor: '#d33',
      confirmButtonText: 'Yes, delete it!',
    }).then((result) => {
      if (result.isConfirmed) {
        this.filmService
          .deleteFilm(film)
          .pipe(takeUntil(this.destroy$))
          .subscribe({
            next: () => {
              this.loadFilms();
              Swal.fire({
                title: 'Deleted!',
                text: `Film: ${film.title}`,
                icon: 'success',
              });
            },
            error: () => {
              Swal.fire({
                title: 'Error',
                text: 'Could not delete film.',
                icon: 'error',
              });
            },
          });
      }
    });
  }

  downloaded(film: Film): void {
    this.filmService
      .updateOrCreateFilm(film)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          Swal.fire({
            title: 'Updated!',
            text: `Film: ${film.title}`,
            icon: 'success',
          });
          this.loadFilms();
        },
        error: () => {
          Swal.fire({
            title: 'Error',
            text: 'Could not update status.',
            icon: 'error',
          });
        },
      });
  }

  assignPerPage(size: any): void {
    this.perPage = Number(size);
    this.loadFilms();
  }

  sortByYear(): void {
    this.sortTitle = noSort;

    if (this.sortYear === noSort) {
      this.sortYear = up;
      this.allFilms.sort((f, s) => (f.year ?? 0) - (s.year ?? 0));
    } else if (this.sortYear === up) {
      this.sortYear = down;
      this.allFilms.sort((f, s) => (s.year ?? 0) - (f.year ?? 0));
    } else if (this.sortYear === down) {
      this.sortYear = noSort;
      this.txtSearchByYear = '';
      this.txtSearchByTitle = '';
      this.loadFilms();
    }
  }

  sortByTitle(): void {
    this.sortYear = noSort;

    if (this.sortTitle === noSort) {
      this.sortTitle = up;
      this.allFilms.sort((f, s) => (f.title ?? '').localeCompare(s.title ?? ''));
    } else if (this.sortTitle === up) {
      this.sortTitle = down;
      this.allFilms.sort((f, s) => (s.title ?? '').localeCompare(f.title ?? ''));
    } else if (this.sortTitle === down) {
      this.sortTitle = noSort;
      this.txtSearchByYear = '';
      this.txtSearchByTitle = '';
      this.loadFilms();
    }
  }
}
