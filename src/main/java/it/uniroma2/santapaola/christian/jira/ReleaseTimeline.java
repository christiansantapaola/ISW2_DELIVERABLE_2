package it.uniroma2.santapaola.christian.jira;

import java.time.LocalDate;
import java.util.Iterator;
import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * La classe ReleaseTimeline si occupa di gestire le releases di un progetto softare prese dal servizio jira.
 */
public class ReleaseTimeline implements Iterable<Release> {
    private SortedSet<Release> releases;
    private static final ReleaseComparator rc = new ReleaseComparator();

    public ReleaseTimeline() {
        this.releases = new TreeSet<>(rc);
    }

    public void insertRelease(String name, String id, LocalDate releaseDate) {
        Release release = new Release(name, id, releaseDate);
        releases.add(release);
        int noRelease = 1;
        for (Release rel : releases) {
            rel.setNoRelease(noRelease);
            noRelease++;
        }
    }

    public Iterator<Release> iterator() {
        return releases.iterator();
    }

    public Optional<Release> get(int nth) {
        return releases.stream().filter(release -> release.getNoRelease() == nth).findFirst();
    }


    public Optional<Release> get(String releaseName) {
        for (Release release : releases) {
            if (release.getName().equals(releaseName)) {
                return Optional.of(release);
            }
        }
        return Optional.empty();
    }
}
