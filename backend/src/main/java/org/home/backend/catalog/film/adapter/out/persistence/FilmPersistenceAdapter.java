package org.home.backend.catalog.film.adapter.out.persistence;

import org.home.backend.catalog.film.application.port.out.FilmStore;
import org.home.backend.catalog.film.domain.Film;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class FilmPersistenceAdapter implements FilmStore {

    private final FilmJpaRepository filmJpaRepository;

    public FilmPersistenceAdapter(FilmJpaRepository filmJpaRepository) {
        this.filmJpaRepository = filmJpaRepository;
    }

    @Override
    public List<Film> findAll() {
        return filmJpaRepository.findAll();
    }

    @Override
    public Optional<Film> findById(long id) {
        return filmJpaRepository.findById(id);
    }

    @Override
    public Film save(Film film) {
        return filmJpaRepository.save(film);
    }

    @Override
    public void deleteById(long id) {
        filmJpaRepository.deleteById(id);
    }
}
