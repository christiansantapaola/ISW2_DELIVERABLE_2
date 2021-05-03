package Mining;

import JiraSubSystem.Release;
import JiraSubSystem.ReleaseTimeline;
import Proportion.Proportion;

import java.util.Optional;

public class BugLifeCycle {
    private Bug bug;
    private Release IV;
    private Release OV;
    private Release FV;
    private int AV;

    public BugLifeCycle(Bug bug, ReleaseTimeline timeline, Proportion proportion) {
        this.bug = bug;
        this.OV = timeline.getNextRelease(bug.getTicketCreationDate()).orElseThrow();
        this.FV = timeline.getNextRelease(bug.getFixCommitDate()).orElseThrow();
        proportion.computeProportion(OV);
        int iv = proportion.computeIV(FV.getNoRelease(), FV.getNoRelease());
        this.IV = timeline.get(iv).orElseThrow();
        this.AV = FV.getReleaseDiff(IV);
    }

    public boolean isBuggy(String className, Release release) {
        if (release.compareTo(FV) > 0 || release.compareTo(IV) < 0) {
            return false;
        }
        return bug.isFileAffected(className);
    }

    public Bug getBug() {
        return bug;
    }

    public boolean isBefore(Release release) {
        return release.getReleaseDate().compareTo(FV.getReleaseDate()) > 0;
    }

    public boolean isAfter(Release release) {
        return release.getReleaseDate().compareTo(IV.getReleaseDate()) < 0;
    }



    public Release getIV() {
        return IV;
    }

    public Release getOV() {
        return OV;
    }

    public Release getFV() {
        return FV;
    }

    public int getAV() {
        return AV;
    }
}
