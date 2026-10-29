package org.home.backend.catalog.series.domain;

import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name="SERIE", schema = "DATA")
@SequenceGenerator(name = "serie_id_sequence", sequenceName = "data.serie_id_seq", allocationSize = 1)
public class Serie {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "serie_id_sequence")
    private long id;
    @Column(name = "TITLE")
    private String title;
    @Column(name = "YEAR")
    private Integer year;
    @Column(name = "DOWNLOADED")
    private Boolean downloaded;
    @Column(name = "WEB")
    private String web;
    @OneToMany(mappedBy = "serie")
    private Set<Episode> listEpisodes;

    public Serie () {
        //Nothing
    }

    public Serie (String title, Integer year, Boolean downloaded, String web) {
        this.title = title;
        this.year = year;
        this.downloaded = downloaded;
        this.web = web;
    }

    public Serie (long id, String title, Integer year, Boolean downloaded, String web, Set<Episode> listEpisodes) {
        this.id = id;
        this.title = title;
        this.year = year;
        this.downloaded = downloaded;
        this.web = web;
        this.listEpisodes = listEpisodes;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Boolean getDownloaded() {
        return downloaded;
    }

    public void setDownloaded(Boolean downloaded) {
        this.downloaded = downloaded;
    }

    public String getWeb() {
        return web;
    }

    public void setWeb(String web) {
        this.web = web;
    }

    public Set<Episode> getListEpisodes() {
        return listEpisodes;
    }

    public void setListEpisodes(Set<Episode> listEpisodes) {
        this.listEpisodes = listEpisodes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Serie other)) return false;
        return id != 0 && id == other.id;
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Serie {" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", year=" + year +
                ", downloaded=" + downloaded +
                ", web=" + web +
                '}';
    }
}
