package it.uniroma2.santapaola.christian.mining;

import it.uniroma2.santapaola.christian.git.Git;
import it.uniroma2.santapaola.christian.git.exception.GitHandlerException;
import it.uniroma2.santapaola.christian.jira.ReleaseTimeline;
import it.uniroma2.santapaola.christian.proportion.Proportion;
import it.uniroma2.santapaola.christian.proportion.ProportionBuilder;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * La classe Timeline si occupa si occupa di gestire, analizzare e rispondere a query riguardanti l'evoluzione temporale
 * di un progetto softare.
 * Questa classe richiede una lista contente le informazioni sulla vita dei bug conosciuti,
 * un implementazione di un algoritmo di proportion.
 * La timeline di tutte le versioni.
 * Questa classe si occupa di decidere, data una versione quali classi sono buggati e quali non lo sono, in base ai
 * bug esistenti ed al metodo di proportion scelto.
 */
public class Timeline {
    private List<BugLifeCycle> lifeCycles;
    private Proportion proportion;
    private VersionTimeline versionTimeline;
    private static final VersionComparator vc = new VersionComparator();


    public Timeline(List<Bug> bugs, ReleaseTimeline releases, Git git, ProportionBuilder.ProportionType type, String tagPattern) throws GitHandlerException {
        this.versionTimeline = new VersionTimeline(git, releases, tagPattern);
        this.proportion = ProportionBuilder.build(type, bugs);
        this.lifeCycles = bugs.stream().map((Bug bug) -> new BugLifeCycle(bug, versionTimeline, proportion)).collect(Collectors.toList());
    }

    /**
     * getBuggyClass(), ritorna un Insieme di nomi delle classi buggate al tempo di una particolare versione.
     * @param version
     * @return
     */
    public Set<String> getBuggyClass(Version version) {
        Set<String> buggySet = new HashSet<>();
        List<BugLifeCycle> bugs = lifeCycles.stream()
                .filter(bug -> vc.compare(bug.getIv(), version) <= 0 && vc.compare(bug.getFv(), version) > 0)
                .collect(Collectors.toList());
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
        return lifeCycles.stream().filter(bug -> vc.compare(from, bug.getFv()) < 0 && vc.compare(to, bug.getFv()) >= 0).collect(Collectors.toList());
    }

    public Proportion getProportion() {
        return proportion;
    }


}
