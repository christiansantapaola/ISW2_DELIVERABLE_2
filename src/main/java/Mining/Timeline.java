package Mining;

import JiraSubSystem.Release;
import JiraSubSystem.ReleaseTimeline;
import Proportion.Proportion;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


public class Timeline {
    private List<BugLifeCycle> lifeCycles;
    private Proportion proportion;
    private ReleaseTimeline releases;


    public Timeline(List<Bug> bugs, ReleaseTimeline releases, Proportion proportion) {
        this.releases = releases;
        this.proportion = proportion;
        this.lifeCycles = bugs.stream()
                .filter(bug -> bug.getTicketCreationDate().compareTo(releases.getLast().get().getReleaseDate()) <= 0
                        &&
                        bug.getFixCommitDate().compareTo(releases.getLast().get().getReleaseDate()) <= 0 ).sorted(new Comparator<Bug>() {
            @Override
            public int compare(Bug o1, Bug o2) {
                return o1.getTicketCreationDate().compareTo(o2.getTicketCreationDate());
            }
        }).map(new Function<Bug, BugLifeCycle>() {
            @Override
            public BugLifeCycle apply(Bug bug) {
                return new BugLifeCycle(bug, releases, proportion);
            }
        }).collect(Collectors.toUnmodifiableList());

    }

    public boolean isBuggy(String path, Release release) {
        for (BugLifeCycle bugLifeCycle : lifeCycles) {
            if (bugLifeCycle.isBuggy(path, release)) {
                return true;
            }
        }
        return false;
    }

    public Optional<Release> getRelease(int nth) {
        return releases.get(nth);
    }

    public long getNoBugFixed(String path, Optional<Release> from, Optional<Release> to) {
        long noBug = 0;
        List<BugLifeCycle> filteredBugs = getFixedBugsBetween(from, to);
        for (BugLifeCycle bugLifeCycle : filteredBugs) {
            if (bugLifeCycle.getBug().isFileAffected(path)) {
                noBug++;
            }
        }
        return noBug;
    }

    public long getNoRelease() {
        return releases.size();
    }

    public List<BugLifeCycle> getFixedBugsBetween(Optional<Release> from, Optional<Release> to) {
        if (from.isEmpty() && to.isEmpty()) {
            return lifeCycles;
        } if (from.isPresent() && to.isEmpty()) {
            return lifeCycles.stream().filter(bug -> bug.getFV().compareTo(from.get()) >= 0).collect(Collectors.toUnmodifiableList());
        } if (from.isEmpty() && to.isPresent()) {
            return lifeCycles.stream().filter(bug -> bug.getFV().compareTo(to.get()) <= 0).collect(Collectors.toUnmodifiableList());
        } else {
            return lifeCycles.stream().filter(bug -> bug.getFV().compareTo(from.get()) >= 0
                    && bug.getFV().compareTo(to.get()) <= 0).collect(Collectors.toUnmodifiableList());
        }
    }

}

