import { Component, OnDestroy, OnInit } from '@angular/core';
import Swal from 'sweetalert2';
import { delay } from 'rxjs';

import { Film } from '../model/film.model';
import { FilmService } from 'src/app/services/film.service';

const noSort = '../../../assets/images/sort/stay.png';
const up = '../../../assets/images/sort/up.png';
const down = '../../../assets/images/sort/down.png';

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
  public itemsPerPage = [5, 10, 50, 100];
  public sortYear: string = noSort;
  public sortTitle: string = noSort;
  public txtSearchByYear: string = '';
  public txtSearchByTitle: string = '';

  constructor(private filmService: FilmService) {}

  ngOnDestroy(): void {}

  ngOnInit(): void {
    this.loadFilms();
  }

  loadFilms(): void {
    this.loading = true;
    this.filmService.loadFilms().subscribe((response: any) => {
      this.allFilms = response;
      this.allFilmsAux = response;
      this.total = this.allFilms.length;
      this.loading = false;
    });
  }

  searchByTitle() {
    if (this.txtSearchByTitle.length === 0) {
      return (this.allFilms = this.allFilmsAux);
    }

    this.allFilms = this.allFilms.filter((obj) => {
      return obj.title
        .toLowerCase()
        .includes(this.txtSearchByTitle.toLowerCase());
    });

    this.total = this.allFilms.length;
    return this.allFilms;
  }

  searchByYear() {
    if (this.txtSearchByYear.length === 0) {
      return (this.allFilms = this.allFilmsAux);
    }

    this.allFilms = this.allFilms.filter((obj) => {
      return obj.year.toString().includes(this.txtSearchByYear);
    });

    this.total = this.allFilms.length;
    return this.allFilms;
  }

  editFilm(film: Film) {
    this.filmService.updateOrCreateFilm(film).subscribe((resp) => {
      Swal.fire({
        title: 'Updated!!',
        text: `Film: ${film.title}`,
        icon: 'success',
      });
      this.loadFilms();
    });
    this.loadFilms();
  }

  deleteFilm(film: Film) {
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
          .pipe(delay(100))
          .subscribe((resp) => {
            this.loadFilms();
            Swal.fire({
              title: 'Deleted!',
              text: `Film: ${film.title}`,
              icon: 'success',
            });
          });
      }
    });
  }

  downloaded(film: Film) {
    this.filmService.updateOrCreateFilm(film).subscribe((resp) => {
      Swal.fire({
        title: 'Updated!!',
        text: `Film: ${film.title}`,
        icon: 'success',
      });
      this.loadFilms();
    });
  }

  assignPerPage(size: any) {
    this.perPage = size;
    this.loadFilms();
  }

  sortByYear() {
    this.sortTitle = noSort;

    if (this.sortYear === noSort) {
      this.sortYear = up;
      return this.allFilms.sort(function (f, s) {
        return f.year - s.year;
      });
    } else if (this.sortYear === up) {
      this.sortYear = down;
      return this.allFilms.sort(function (f, s) {
        return s.year - f.year;
      });
    } else if (this.sortYear === down) {
      this.sortYear = noSort;
      this.txtSearchByYear = '';
      this.txtSearchByTitle = '';
      this.loadFilms();
    }
    return;
  }

  sortByTitle() {
    this.sortYear = noSort;

    if (this.sortTitle === noSort) {
      this.sortTitle = up;
      return this.allFilms.sort(function (f, s) {
        return f.title.localeCompare(s.title);
      });
    } else if (this.sortTitle === up) {
      this.sortTitle = down;
      return this.allFilms.sort(function (f, s) {
        return s.title.localeCompare(f.title);
      });
    } else if (this.sortTitle === down) {
      this.sortTitle = noSort;
      this.txtSearchByYear = '';
      this.txtSearchByTitle = '';
      this.loadFilms();
    }
    return;
  }
}
