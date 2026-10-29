package org.home.backend.catalog.film.application.port.out;

import org.home.backend.catalog.film.domain.Film;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

public interface FilmStore {

    List<Film> findAll();

    List<Film> findAll(Sort sort);

    Page<Film> findAll(Pageable pageable);

    Optional<Film> findById(long id);

    Film save(Film film);

    void deleteById(long id);
}
