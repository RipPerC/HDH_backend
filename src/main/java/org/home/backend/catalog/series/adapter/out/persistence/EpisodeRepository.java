package org.home.backend.catalog.series.adapter.out.persistence;

import org.home.backend.catalog.series.domain.Episode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EpisodeRepository extends JpaRepository<Episode, Long> {
}
