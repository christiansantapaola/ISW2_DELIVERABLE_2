package it.uniroma2.santapaola.christian.Mining;

import it.uniroma2.santapaola.christian.GitSubSystem.Exception.GitHandlerException;
import it.uniroma2.santapaola.christian.GitSubSystem.Git;
import it.uniroma2.santapaola.christian.GitSubSystem.GitConstants;
import it.uniroma2.santapaola.christian.GitSubSystem.Tag;
import it.uniroma2.santapaola.christian.JiraSubSystem.Release;
import it.uniroma2.santapaola.christian.JiraSubSystem.ReleaseTimeline;

import java.time.LocalDate;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VersionTimeline {
    private SortedSet<Version> timeline;
    private VersionPattern pattern;
    private final Version MIN;
    private final Version MAX;



    public VersionTimeline(Git git, ReleaseTimeline releases) throws GitHandlerException {
        timeline = new TreeSet<>();
        pattern = new VersionPattern("^(refs\\/tags\\/)(.*)(?<name>\\d+.\\d+.\\d+)(.*)$");
        MIN = new Version(new Release("EmptyTree", "0", LocalDate.MIN), GitConstants.getEmptyTreeTag(), 0);
        timeline.add(MIN);
        long noVersion = 1;
        for (Tag tag : git.getAllTags()) {
            Optional<String> name = pattern.getName(tag.getName());
            if (name.isEmpty()) continue;
            Optional<Release> release = releases.get(name.get());
            if (release.isEmpty()) continue;
            Version version = new Version(release.get(), tag, noVersion);
            timeline.add(version);
            noVersion++;
        }
        MAX = new Version(new Release("HEAD", "head", LocalDate.MAX), GitConstants.getHeadTag(), noVersion);
        timeline.add(MAX);
    }

    public VersionTimeline(Git git, ReleaseTimeline releases, String tagPattern) throws GitHandlerException {
        this.timeline = new TreeSet<>(new Comparator<Version>() {
            @Override
            public int compare(Version o1, Version o2) {
                return Long.compare(o1.getNoVersion(), o2.getNoVersion());
            }
        });
        pattern = new VersionPattern(tagPattern);
        MIN = new Version(new Release("EmptyTree", "0", LocalDate.MIN), GitConstants.getEmptyTreeTag(), 0);
        timeline.add(MIN);
        long index = 1;
        for (Tag tag : git.getAllTags()) {
            Optional<String> name = pattern.getName(tag.getName());
            if (name.isEmpty()) continue;
            Optional<Release> release = releases.get(name.get());
            if (release.isEmpty()) continue;
            Version version = new Version(release.get(), tag, index);
            timeline.add(version);
            index += 1;
        }
        MAX = new Version(new Release("HEAD", "head", LocalDate.MAX), GitConstants.getHeadTag(), index);
        timeline.add(MAX);
    }


    public Version getFirst() {
        return MIN;
    }

    public Version getLast() {
        return MAX;
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
        return timeline.stream().filter(ver -> ver.compareTo(version) > 0).min(Version::compareTo);
    }

    public Optional<Version> getNext(LocalDate date) {
        return timeline.stream().filter(ver -> ver.getRelease().getReleaseDate().compareTo(date) > 0).min(Version::compareTo);
    }


    public Optional<Version> getPrev(Version version) {
        if (version.getTag().getId().equals(GitConstants.HEAD)) return Optional.empty();
        if (!timeline.contains(version)) return Optional.empty();
        return timeline.stream().filter(ver -> ver.compareTo(version) < 0).max(Version::compareTo);
    }

    public Optional<Version> getPrev(LocalDate date) {
        return timeline.stream().filter(ver -> ver.getRelease().getReleaseDate().compareTo(date) < 0).max(Version::compareTo);
    }


}
