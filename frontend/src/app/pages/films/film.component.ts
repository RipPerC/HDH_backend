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
      year: ['', [Validators.required, Validators.min(1888), Validators.max(2100)]],
      title: ['', [Validators.required, Validators.maxLength(255)]],
      downloaded: [false],
      audio: ['', Validators.maxLength(3)],
      subtitle: ['', Validators.maxLength(3)],
      format: [''],
      director: [''],
    });
  }

  saveFilm(): void {
    if (this.filmForm.invalid) {
      this.filmForm.markAllAsTouched();
      return;
    }

    const formValue = this.filmForm.value;
    const filmToSave: Film = {
      ...formValue,
      year: Number(formValue.year),
      downloaded: formValue.downloaded === true || formValue.downloaded === 'true',
    };

    this.filmService.updateOrCreateFilm(filmToSave).subscribe({
      next: (resp) => {
        Swal.fire({
          title: 'Success!',
          text: `Film "${resp.title}" created successfully.`,
          icon: 'success',
        });
        this.newData();
      },
      error: (err) => {
        Swal.fire({
          title: 'Error',
          text: 'Failed to create film. Please check input values.',
          icon: 'error',
        });
      },
    });
  }
}
