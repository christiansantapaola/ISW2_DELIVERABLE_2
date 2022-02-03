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

public class VersionTimeline {
    private SortedSet<Version> timeline;
    private VersionPattern pattern;
    private final Version min;
    private final Version max;
    private static final VersionComparator vc = new VersionComparator();


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

    public long getNoVersion() {
        return timeline.size();
    }

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

    public Optional<Version> getNext(Version version) {
        if (version.getTag().getId().equals(GitConstants.HEAD)) return Optional.empty();
        if (!timeline.contains(version)) return Optional.empty();
        return timeline.stream().filter(ver -> vc.compare(ver, version) > 0).min(vc);
    }

    public Optional<Version> getNext(LocalDate date) {
        return timeline.stream().filter(ver -> ver.getRelease().getReleaseDate().compareTo(date) > 0).min(vc);
    }

}
