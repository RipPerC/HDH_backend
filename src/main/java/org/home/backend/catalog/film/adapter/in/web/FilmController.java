package org.home.backend.catalog.film.adapter.in.web;

import org.home.backend.catalog.film.application.FilmService;
import org.home.backend.catalog.film.domain.Film;
import org.home.backend.common.web.ResourceNotFoundException;
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
        List<Film> films = filmService.getAllFilms();
        if (films.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(films, HttpStatus.OK);
    }

    @GetMapping("/film/{id}")
    public ResponseEntity<Film> getById(@PathVariable("id") long id) {
        Film film = filmService.getById(id);
        if (film == null) {
            throw new ResourceNotFoundException("Film " + id + " was not found.");
        }
        return new ResponseEntity<>(film, HttpStatus.OK);
    }

    @PostMapping("/film")
    public ResponseEntity<Film> createFilm(@RequestBody Film film) {
        boolean newFilm = film.getId() == 0;
        filmService.createOrUpdateFilm(film);
        if (newFilm) {
            return new ResponseEntity<>(film, HttpStatus.CREATED);
        }
        return new ResponseEntity<>(film, HttpStatus.ACCEPTED);
    }

    @DeleteMapping("/film/{id}")
    public ResponseEntity<HttpStatus> deleteFilm(@PathVariable("id") long id) {
        if (filmService.getById(id) == null) {
            throw new ResourceNotFoundException("Film " + id + " was not found.");
        }
        filmService.deleteFilm(id);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }
}
