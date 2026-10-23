package org.home.backend.catalog.film.adapter.in.web;

import org.home.backend.catalog.film.application.FilmService;
import org.home.backend.catalog.film.domain.Film;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/v1")
public class FilmController {

    private final FilmService filmService;

    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping("/film")
    public ResponseEntity<List<Film>> getAll() {

        try {

            List<Film> films = filmService.getAllFilms();

            if (films.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(films, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.<List<Film>>status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/film/{id}")
    public ResponseEntity<Film> getById(@PathVariable("id") long id) {

        try {

            Film film = filmService.getById(id);

            if (film == null) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(film, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.<Film>status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/film")
    public ResponseEntity<Film> createFilm(@RequestBody Film film) {
        boolean newFilm = film.getId() == 0;

        try {
            filmService.createOrUpdateFilm(film);

            if (newFilm) {
                return new ResponseEntity<>(film, HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>(film, HttpStatus.ACCEPTED);
            }
        } catch (Exception e) {
            return ResponseEntity.<Film>status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/film/{id}")
    public ResponseEntity<HttpStatus> deleteFilm(@PathVariable("id") long id) {
        try {

            filmService.deleteFilm(id);

            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
