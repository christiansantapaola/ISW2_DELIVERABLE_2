package it.uniroma2.santapaola.christian.mining;

import it.uniroma2.santapaola.christian.jira.Release;
import it.uniroma2.santapaola.christian.proportion.Proportion;

public class BugLifeCycle {
    private Bug bug;
    private Version iv;
    private Version ov;
    private Version fv;
    private long av;
    private static final VersionComparator vc = new VersionComparator();

    public BugLifeCycle(Bug bug, VersionTimeline timeline, Proportion proportion) {
        this.bug = bug;
        this.ov = timeline.getNext(bug.getTicketCreationDate()).orElseThrow();
        this.fv = timeline.getNext(bug.getFixCommitDate()).orElseThrow();
        proportion.computeProportion(ov);
        var noIv = proportion.computeIV((int) fv.getNoVersion(), (int) ov.getNoVersion());
        this.iv = timeline.get(noIv);
        this.av = fv.getVersionDiff(this.iv);
    }

    public boolean isBuggy(String className, Version version) {
        if (vc.compare(version,fv) > 0 || vc.compare(version,iv) < 0) {
            return false;
        }
        return bug.isFileAffected(className);
    }

    public Bug getBug() {
        return bug;
    }

    public boolean isBefore(Release release) {
        return release.getReleaseDate().compareTo(fv.getRelease().getReleaseDate()) > 0;
    }

    public boolean isAfter(Release release) {
        return release.getReleaseDate().compareTo(iv.getRelease().getReleaseDate()) < 0;
    }


    public Version getIv() {
        return iv;
    }

    public Version getOv() {
        return ov;
    }

    public Version getFv() {
        return fv;
    }

    public long getAv() {
        return av;
    }
}
