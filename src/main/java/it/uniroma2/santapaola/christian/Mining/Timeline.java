package it.uniroma2.santapaola.christian.Mining;

import it.uniroma2.santapaola.christian.GitSubSystem.Exception.GitHandlerException;
import it.uniroma2.santapaola.christian.GitSubSystem.Git;
import it.uniroma2.santapaola.christian.JiraSubSystem.ReleaseTimeline;
import it.uniroma2.santapaola.christian.Proportion.Proportion;
import it.uniroma2.santapaola.christian.Proportion.ProportionBuilder;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;


public class Timeline {
    private List<Bug> bugs;
    private List<BugLifeCycle> lifeCycles;
    private Proportion proportion;
    private VersionTimeline timeline;


    public Timeline(List<Bug> bugs, ReleaseTimeline releases, Git git, ProportionBuilder.ProportionType type, String tagPattern) throws GitHandlerException {
        this.bugs = bugs;
        this.timeline = new VersionTimeline(git, releases, tagPattern);
        this.proportion = ProportionBuilder.build(type, bugs);
        this.lifeCycles = bugs.stream().map(new Function<Bug, BugLifeCycle>() {
            @Override
            public BugLifeCycle apply(Bug bug) {
                return new BugLifeCycle(bug, timeline, proportion);
            }
        }).collect(Collectors.toUnmodifiableList());
    }

    public Set<String> getBuggyClass(Version version) {
        Set<String> buggySet = new HashSet<>();
        List<BugLifeCycle> bugs = lifeCycles.stream()
                .filter(bug -> bug.getIV().compareTo(version) <= 0 && bug.getFV().compareTo(version) > 0)
                .collect(Collectors.toUnmodifiableList());
        for (BugLifeCycle bug : bugs) {
            buggySet.addAll(bug.getBug().getAffectedFile());
        }
        return buggySet;
    }

    public VersionTimeline getTimeline() {
        return timeline;
    }


    public long getNoBugFixed(String path, Version from, Version to) {
        return lifeCycles.stream().filter(bug -> from.compareTo(bug.getFV()) < 0 && to.compareTo(bug.getFV()) >= 0 && bug.getBug().isFileAffected(path)).count();    }

    public List<BugLifeCycle> getFixedBugsBetween(Version from, Version to) {
        List<BugLifeCycle> result = lifeCycles.stream().filter(bug -> from.compareTo(bug.getFV()) < 0 && to.compareTo(bug.getFV()) >= 0).collect(Collectors.toUnmodifiableList());
        return result;
    }

    public Proportion getProportion() {
        return proportion;
    }


}
