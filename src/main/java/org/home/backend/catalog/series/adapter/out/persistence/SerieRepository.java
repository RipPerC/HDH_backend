package org.home.backend.catalog.series.adapter.out.persistence;

import org.home.backend.catalog.series.domain.Serie;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SerieRepository extends JpaRepository<Serie, Long> {
}
