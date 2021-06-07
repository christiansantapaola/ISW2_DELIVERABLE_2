package it.uniroma2.santapaola.christian.jira;

import java.time.LocalDate;

public class Release {
    private String name;
    private String id;
    private LocalDate releaseDate;
    private int noRelease;

    public Release(String name, String id, LocalDate releaseDate) {
        this.name = name;
        this.id = id;
        this.releaseDate = releaseDate;
        this.noRelease = 0;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public int getNoRelease() {
        return noRelease;
    }

    public void setNoRelease(int noRelease) {
        this.noRelease = noRelease;
    }


    @Override
    public String toString() {
        return "Release{" +
                "Name='" + name + '\'' +
                ", ID='" + id + '\'' +
                ", releaseDate=" + releaseDate +
                ", noRelease=" + noRelease +
                '}';
    }

    public int getReleaseDiff(Release other) {
        return this.getNoRelease() - other.getNoRelease();
    }
}
