package it.uniroma2.santapaola.christian.Mining;

import it.uniroma2.santapaola.christian.JiraSubSystem.Release;
import it.uniroma2.santapaola.christian.JiraSubSystem.ReleaseTimeline;
import it.uniroma2.santapaola.christian.Proportion.Proportion;

public class BugLifeCycle {
    private Bug bug;
    private Version IV;
    private Version OV;
    private Version FV;
    private long AV;

    public BugLifeCycle(Bug bug, VersionTimeline timeline, Proportion proportion) {
        this.bug = bug;
        this.OV = timeline.getNext(bug.getTicketCreationDate()).orElseThrow();
        this.FV = timeline.getNext(bug.getFixCommitDate()).orElseThrow();
        proportion.computeProportion(OV);
        int iv = proportion.computeIV((int)FV.getNoVersion(), (int)OV.getNoVersion());
        this.IV = timeline.get(iv);
        this.AV = FV.getVersionDiff(IV);
    }

    public boolean isBuggy(String className, Version version) {
        if (version.compareTo(FV) > 0 || version.compareTo(IV) < 0) {
            return false;
        }
        return bug.isFileAffected(className);
    }

    public Bug getBug() {
        return bug;
    }

    public boolean isBefore(Release release) {
        return release.getReleaseDate().compareTo(FV.getRelease().getReleaseDate()) > 0;
    }

    public boolean isAfter(Release release) {
        return release.getReleaseDate().compareTo(IV.getRelease().getReleaseDate()) < 0;
    }


    public Version getIV() {
        return IV;
    }

    public Version getOV() {
        return OV;
    }

    public Version getFV() {
        return FV;
    }

    public long getAV() {
        return AV;
    }
}
