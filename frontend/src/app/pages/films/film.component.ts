import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Film } from '../model/film.model';
import { FilmService } from 'src/app/services/film.service';
import Swal from 'sweetalert2';
import { Router } from '@angular/router';

@Component({
  selector: 'app-film',
  templateUrl: './film.component.html',
  styleUrls: [],
})
export class FilmComponent implements OnInit {
  public filmForm!: FormGroup;

  constructor(
    private fb: FormBuilder,
    private filmService: FilmService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.newData();
  }

  newData(): void {
    this.filmForm = this.fb.group({
      year: ['', Validators.required],
      title: ['', Validators.required],
      downloaded: ['false'],
      audio: ['', Validators.maxLength(3)],
      subtitle: ['', Validators.maxLength(3)],
      format: [],
      director: [],
    });
  }

  saveFilm() {
    this.filmService
      .updateOrCreateFilm(this.filmForm.value)
      .subscribe((resp: any) => {
        Swal.fire('Created');
        this.newData();
      });
  }
}
