package it.uniroma2.santapaola.christian.mining;

import it.uniroma2.santapaola.christian.git.Git;
import it.uniroma2.santapaola.christian.git.GitConstants;
import it.uniroma2.santapaola.christian.git.Tag;
import it.uniroma2.santapaola.christian.git.exception.GitHandlerException;
import it.uniroma2.santapaola.christian.jira.Release;
import it.uniroma2.santapaola.christian.jira.ReleaseTimeline;

import java.time.LocalDate;
import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * VersionTimeline si occupa di gestire l'estrazione e le query riguardanti le versioni di un progetto software.
 * Questa classe unisce le informazioni di release prese dal servizio jira con la ricerca di versioni e commit legata
 * al servizio di versioning git.
 */

public class VersionTimeline {
    private SortedSet<Version> timeline;
    private VersionPattern pattern;
    private final Version min;
    private final Version max;
    private static final VersionComparator vc = new VersionComparator();


    /**
     *
     * @param git: Gestore di repository git
     * @param releases: le release del software prese dal servizio jira.
     * @param tagPattern: Pattern per parsare le versioni del software.
     * @throws GitHandlerException
     */
    public VersionTimeline(Git git, ReleaseTimeline releases, String tagPattern) throws GitHandlerException {
        this.timeline = new TreeSet<>((Version o1, Version o2) -> Long.compare(o1.getNoVersion(), o2.getNoVersion()));
        pattern = new VersionPattern(tagPattern);
        min = new Version(new Release("EmptyTree", "0", LocalDate.MIN), GitConstants.getEmptyTreeTag(), 0);
        timeline.add(min);
        long index = 1;
        for (Tag tag : git.getAllTags()) {
            Optional<Version> version = tagToVersion(tag, releases, pattern, index);
            if (!version.isPresent()) continue;
            timeline.add(version.get());
            index += 1;
        }
        max = new Version(new Release("HEAD", "head", LocalDate.MAX), GitConstants.getHeadTag(), index);
        timeline.add(max);
    }

    /**
     * dato un Tag git, ritorna la versione associata.
     * @param tag: Tag del repository GIT
     * @param releases: le release prese dal software jira
     * @param pattern: pattern per parsare la versione del software.
     * @param index: indice di versione.
     * @return La versione associata al tag, se esiste.
     */
    private static Optional<Version> tagToVersion(Tag tag, ReleaseTimeline releases, VersionPattern pattern, long index) {
        Optional<String> name = pattern.getName(tag.getName());
        if (!name.isPresent()) return Optional.empty();
        Optional<Release> release = releases.get(name.get());
        if (!release.isPresent()) return Optional.empty();
        return Optional.of(new Version(release.get(), tag, index));
    }


    public Version getFirst() {
        return min;
    }

    public Version getLast() {
        return max;
    }

    public long getNumVersion() {
        return timeline.size();
    }


    /**
     * get() ottiene la versione data il suo indice.
     * @param nth
     * @return
     */
    public Version get(int nth) {
        int pos = 0;
        for(Version version : timeline) {
            if (pos == nth) {
                return version;
            }
            pos++;
        }
        throw new IllegalArgumentException();
    }

    /**
     * Ottiene la versione successiva alla versione data.
     * @param version
     * @return La versione successiva alla versione input
     */
    public Optional<Version> getNext(Version version) {
        if (version.getTag().getId().equals(GitConstants.HEAD)) return Optional.empty();
        if (!timeline.contains(version)) return Optional.empty();
        return timeline.stream().filter(ver -> vc.compare(ver, version) > 0).min(vc);
    }

    /**
     * Ottiene la versione successiva alla data input.
     * @param date
     * @return La versione successiva alla data input
     */
    public Optional<Version> getNext(LocalDate date) {
        return timeline.stream().filter(ver -> ver.getRelease().getReleaseDate().compareTo(date) > 0).min(vc);
    }

}
