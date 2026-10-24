package org.home.backend.catalog.film.adapter.out.persistence;

import org.home.backend.catalog.film.domain.Film;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FilmJpaRepository extends JpaRepository<Film, Long> {
}
