package it.uniroma2.santapaola.christian.JiraSubSystem;

import java.time.LocalDate;

public class Release implements Comparable<Release> {
    private String name;
    private String ID;
    private LocalDate releaseDate;
    private int noRelease;

    public Release(String name, String ID, LocalDate releaseDate) {
        this.name = name;
        this.ID = ID;
        this.releaseDate = releaseDate;
        this.noRelease = 0;
    }

    public String getName() {
        return name;
    }

    public String getID() {
        return ID;
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
    public int compareTo(Release o) {
        return releaseDate.compareTo(o.getReleaseDate());
    }


    @Override
    public String toString() {
        return "Release{" +
                "Name='" + name + '\'' +
                ", ID='" + ID + '\'' +
                ", releaseDate=" + releaseDate +
                ", noRelease=" + noRelease +
                '}';
    }

    public int getReleaseDiff(Release other) {
        return this.getNoRelease() - other.getNoRelease();
    }
}
