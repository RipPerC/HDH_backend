package org.home.backend.catalog.film.application;

import org.home.backend.catalog.film.application.port.out.FilmStore;
import org.home.backend.catalog.film.domain.Film;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class FilmService {

    private final FilmStore filmStore;

    public FilmService(FilmStore filmStore) {
        this.filmStore = filmStore;
    }

    public List<Film> getAllFilms() {
        List<Film> films = new ArrayList<>(filmStore.findAll());
        films.sort(Comparator.comparingInt(Film::getYear).thenComparing(Film::getTitle));

        return films;
    }

    public Film getById(long id) {
        return filmStore.findById(id).orElse(null);
    }

    public void createOrUpdateFilm(Film film){
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

        filmStore.save(film);
    }

    public void deleteFilm(long id) {
        filmStore.deleteById(id);
    }
}
