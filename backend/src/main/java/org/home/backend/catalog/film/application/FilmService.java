package org.home.backend.catalog.film.application;

import org.home.backend.catalog.film.application.port.out.FilmStore;
import org.home.backend.catalog.film.domain.Film;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FilmService {

    private final FilmStore filmStore;

    public FilmService(FilmStore filmStore) {
        this.filmStore = filmStore;
    }

    public List<Film> getAllFilms() {
        return filmStore.findAll(Sort.by(Sort.Order.asc("year").nullsLast(), Sort.Order.asc("title").nullsLast()));
    }

    public Page<Film> getAllFilms(Pageable pageable) {
        return filmStore.findAll(pageable);
    }

    public Film getById(long id) {
        return filmStore.findById(id).orElse(null);
    }

    public Film createOrUpdateFilm(Film film) {
        if (film.getDownloaded() == null) {
            film.setDownloaded(false);
        }
        if (film.getAudio() != null) {
            film.setAudio(film.getAudio().toUpperCase());
        }
        if (film.getSubtitle() != null) {
            film.setSubtitle(film.getSubtitle().toUpperCase());
        }
        if (film.getFormat() != null) {
            film.setFormat(film.getFormat().toLowerCase());
        }

        return filmStore.save(film);
    }

    public void deleteFilm(long id) {
        filmStore.deleteById(id);
    }
}
