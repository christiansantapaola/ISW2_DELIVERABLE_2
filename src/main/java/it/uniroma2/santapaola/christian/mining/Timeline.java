package it.uniroma2.santapaola.christian.mining;

import it.uniroma2.santapaola.christian.git.exception.GitHandlerException;
import it.uniroma2.santapaola.christian.git.Git;
import it.uniroma2.santapaola.christian.jira.ReleaseTimeline;
import it.uniroma2.santapaola.christian.proportion.Proportion;
import it.uniroma2.santapaola.christian.proportion.ProportionBuilder;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


public class Timeline {
    private List<BugLifeCycle> lifeCycles;
    private Proportion proportion;
    private VersionTimeline versionTimeline;
    private static final VersionComparator vc = new VersionComparator();


    public Timeline(List<Bug> bugs, ReleaseTimeline releases, Git git, ProportionBuilder.ProportionType type, String tagPattern) throws GitHandlerException {
        this.versionTimeline = new VersionTimeline(git, releases, tagPattern);
        this.proportion = ProportionBuilder.build(type, bugs);
        this.lifeCycles = bugs.stream().map((Bug bug) -> new BugLifeCycle(bug, versionTimeline, proportion)).collect(Collectors.toUnmodifiableList());
    }

    public Set<String> getBuggyClass(Version version) {
        Set<String> buggySet = new HashSet<>();
        List<BugLifeCycle> bugs = lifeCycles.stream()
                .filter(bug -> vc.compare(bug.getIv(), version) <= 0 && vc.compare(bug.getFv(), version) > 0)
                .collect(Collectors.toUnmodifiableList());
        for (BugLifeCycle bug : bugs) {
            buggySet.addAll(bug.getBug().getAffectedFile());
        }
        return buggySet;
    }

    public VersionTimeline getVersionTimeline() {
        return versionTimeline;
    }


    public long getNoBugFixed(String path, Version from, Version to) {
        return lifeCycles.stream().filter(bug -> vc.compare(from, bug.getFv()) < 0 && vc.compare(to, bug.getFv()) >= 0 && bug.getBug().isFileAffected(path)).count();
    }

    public List<BugLifeCycle> getFixedBugsBetween(Version from, Version to) {
        return lifeCycles.stream().filter(bug -> vc.compare(from, bug.getFv()) < 0 && vc.compare(to, bug.getFv()) >= 0).collect(Collectors.toUnmodifiableList());
    }

    public Proportion getProportion() {
        return proportion;
    }


}
