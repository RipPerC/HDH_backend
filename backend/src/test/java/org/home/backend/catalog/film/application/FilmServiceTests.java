package org.home.backend.catalog.film.application;

import org.home.backend.catalog.film.application.port.out.FilmStore;
import org.home.backend.catalog.film.domain.Film;
import org.home.backend.catalog.series.domain.Episode;
import org.home.backend.catalog.series.domain.Serie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FilmServiceTests {

    @Mock
    private FilmStore filmStore;

    private FilmService filmService;

    @BeforeEach
    void setUp() {
        filmService = new FilmService(filmStore);
    }

    @Test
    void createOrUpdateFilmNormalizesAndReturnsSavedFilm() {
        Film input = new Film("Inception", "Nolan", 2010, "es", "en", "MKV", null);
        Film saved = new Film("Inception", "Nolan", 2010, "ES", "EN", "mkv", false);
        saved.setId(10L);

        when(filmStore.save(input)).thenReturn(saved);

        Film result = filmService.createOrUpdateFilm(input);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(10L);
        assertThat(input.getAudio()).isEqualTo("ES");
        assertThat(input.getSubtitle()).isEqualTo("EN");
        assertThat(input.getFormat()).isEqualTo("mkv");
        assertThat(input.getDownloaded()).isFalse();
        verify(filmStore).save(input);
    }

    @Test
    void getAllFilmsSortsWithNullSafeComparator() {
        Film f1 = new Film("Zorro", "Director", null, "EN", "ES", "mp4", false);
        Film f2 = new Film("Avatar", "Cameron", 2009, "EN", "ES", "mp4", true);

        when(filmStore.findAll(any(Sort.class))).thenReturn(List.of(f2, f1));

        List<Film> result = filmService.getAllFilms();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getTitle()).isEqualTo("Avatar");
        assertThat(result.get(1).getTitle()).isEqualTo("Zorro");
    }

    @Test
    void getAllFilmsWithPageableDelegatesToStore() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        when(filmStore.findAll(pageRequest)).thenReturn(new PageImpl<>(List.of()));

        filmService.getAllFilms(pageRequest);

        verify(filmStore).findAll(pageRequest);
    }

    @Test
    void entityToStringDoesNotCauseStackOverflow() {
        Serie serie = new Serie(1L, "Breaking Bad", 2008, true, "http://example.com", null);
        Episode episode = new Episode(100L, serie, 1, 1, "EN", "ES");
        serie.setListEpisodes(Set.of(episode));

        assertThat(serie.toString()).doesNotContain("Episode {");
        assertThat(episode.toString()).contains("serieId=1");
    }
}
