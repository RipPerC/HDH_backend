package org.home.backend.catalog.film.adapter.in.web;

import jakarta.validation.Valid;
import org.home.backend.catalog.film.application.FilmService;
import org.home.backend.catalog.film.domain.Film;
import org.home.backend.common.web.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1")
public class FilmController {

    private final FilmService filmService;

    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping("/film")
    public ResponseEntity<?> getAll(
            @RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "size", required = false) Integer size,
            @RequestParam(name = "sort", required = false) String sort) {
        if (page != null && size != null) {
            Sort sortObj = parseSort(sort);
            Pageable pageable = PageRequest.of(page, size, sortObj);
            Page<Film> filmsPage = filmService.getAllFilms(pageable);
            if (filmsPage.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return ResponseEntity.ok(filmsPage);
        }

        List<Film> films = filmService.getAllFilms();
        if (films.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return ResponseEntity.ok(films);
    }

    @GetMapping("/film/{id}")
    public ResponseEntity<Film> getById(@PathVariable("id") long id) {
        Film film = filmService.getById(id);
        if (film == null) {
            throw new ResourceNotFoundException("Film " + id + " was not found.");
        }
        return ResponseEntity.ok(film);
    }

    @PostMapping("/film")
    public ResponseEntity<Film> createFilm(@Valid @RequestBody Film film) {
        boolean newFilm = film.getId() == 0;
        Film saved = filmService.createOrUpdateFilm(film);
        if (saved == null) {
            saved = film;
        }
        if (newFilm) {
            return new ResponseEntity<>(saved, HttpStatus.CREATED);
        }
        return new ResponseEntity<>(saved, HttpStatus.ACCEPTED);
    }

    @PutMapping("/film/{id}")
    public ResponseEntity<Film> updateFilm(@PathVariable("id") long id, @Valid @RequestBody Film film) {
        if (filmService.getById(id) == null) {
            throw new ResourceNotFoundException("Film " + id + " was not found.");
        }
        film.setId(id);
        Film updated = filmService.createOrUpdateFilm(film);
        if (updated == null) {
            updated = film;
        }
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/film/{id}")
    public ResponseEntity<HttpStatus> deleteFilm(@PathVariable("id") long id) {
        if (filmService.getById(id) == null) {
            throw new ResourceNotFoundException("Film " + id + " was not found.");
        }
        filmService.deleteFilm(id);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    private Sort parseSort(String sort) {
        if (sort == null || sort.isBlank()) {
            return Sort.by(Sort.Order.asc("year").nullsLast(), Sort.Order.asc("title").nullsLast());
        }
        String[] parts = sort.split(",");
        String property = parts[0].trim();
        Sort.Direction direction = (parts.length > 1 && parts[1].trim().equalsIgnoreCase("desc"))
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;
        return Sort.by(new Sort.Order(direction, property).nullsLast());
    }
}
