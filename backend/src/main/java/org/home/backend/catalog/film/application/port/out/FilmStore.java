package org.home.backend.catalog.film.application.port.out;

import org.home.backend.catalog.film.domain.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStore {

    List<Film> findAll();

    Optional<Film> findById(long id);

    Film save(Film film);

    void deleteById(long id);
}
