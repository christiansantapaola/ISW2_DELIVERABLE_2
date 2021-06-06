package it.uniroma2.santapaola.christian.JiraSubSystem;

import java.time.LocalDate;
import java.util.Iterator;
import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeSet;

public class ReleaseTimeline implements Iterable<Release> {
    private SortedSet<Release> releases;

    public ReleaseTimeline() {
        this.releases = new TreeSet<>(Release::compareTo);
    }

    public void insertRelease(String name, String ID, LocalDate releaseDate) {
        Release release = new Release(name, ID, releaseDate);
        releases.add(release);
        int noRelease = 1;
        for (Release rel : releases) {
            rel.setNoRelease(noRelease);
            noRelease++;
        }
    }

    public Optional<Release> getFirst() {
        return releases.stream().min(Release::compareTo);
    }

    public Optional<Release> getLast() {
        return releases.stream().max(Release::compareTo);
    }


    public Optional<Release> getNextRelease(LocalDate date) {
        return releases.stream().filter(release -> release.getReleaseDate().compareTo(date) >= 0).min(Release::compareTo);
    }

    public Optional<Release> getPrevRelease(LocalDate date) {
        return releases.stream().filter(release -> release.getReleaseDate().compareTo(date) <= 0).max(Release::compareTo);

    }

    public Iterator<Release> iterator() {
        return releases.iterator();
    }

    public long size() {
        return releases.size();
    }

    public Optional<Release> get(int nth) {
        return releases.stream().filter(release -> release.getNoRelease() == nth).findFirst();
    }

    public int countReleaseBeetween(LocalDate d1, LocalDate d2) {
        Optional<Release> r1 = getPrevRelease(d1);
        Optional<Release> r2 = getNextRelease(d2);
        if (r1.isPresent() && r2.isPresent()) {
            System.out.println(r1.get());
            System.out.println(r2.get());
            return r2.get().getReleaseDiff(r1.get());
        } else {
            return 0;
        }
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
