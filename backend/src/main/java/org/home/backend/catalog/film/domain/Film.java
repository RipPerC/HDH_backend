package org.home.backend.catalog.film.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name="FILM", schema = "DATA")
@SequenceGenerator(name = "film_id_sequence", sequenceName = "data.film_id_seq", allocationSize = 1)
public class Film {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "film_id_sequence")
    private long id;

    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    @Column(name = "TITLE")
    private String title;

    @Size(max = 255, message = "Director must not exceed 255 characters")
    @Column(name = "DIRECTOR")
    private String director;

    @NotNull(message = "Year is required")
    @Min(value = 1888, message = "Year must be 1888 or later")
    @Max(value = 2100, message = "Year must not exceed 2100")
    @Column(name = "YEAR")
    private Integer year;

    @Size(max = 255, message = "Audio must not exceed 255 characters")
    @Column(name = "AUDIO")
    private String audio;

    @Size(max = 255, message = "Subtitle must not exceed 255 characters")
    @Column(name = "SUBTITLE")
    private String subtitle;

    @Size(max = 255, message = "Format must not exceed 255 characters")
    @Column(name = "FORMAT")
    private String format;

    @Column(name = "DOWNLOADED")
    private Boolean downloaded;

    @CreationTimestamp
    @Column(name = "CREATION_DATE", updatable = false)
    private Instant creationDate;

    public Film (String title, String director, Integer year, String audio, String subtitle,
                 String format, Boolean downloaded) {
        this.title = title;
        this.director = director;
        this.year = year;
        this.audio = audio;
        this.subtitle = subtitle;
        this.format = format;
        this.downloaded = downloaded;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Film other)) return false;
        return id != 0 && id == other.id;
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
