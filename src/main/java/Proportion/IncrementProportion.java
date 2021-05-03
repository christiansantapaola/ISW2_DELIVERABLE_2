package Proportion;

import JiraSubSystem.Release;
import JiraSubSystem.ReleaseTimeline;
import Mining.Bug;

import java.util.List;
import java.util.stream.Stream;

public class IncrementProportion extends Proportion {

    private final List<Bug> bugs;
    private Release release;

    public IncrementProportion(List<Bug> bugs, Release release) {
            this.bugs = bugs;
            this.release = release;
    }
    @Override
    public void computeProportion() {
        Long noFixedBug = bugs.stream().filter(bug -> bug.getFixCommitDate().compareTo(release.getReleaseDate()) <= 0).count();
        Long noBug = bugs.stream().filter(bug -> bug.getTicketCreationDate().compareTo(release.getReleaseDate()) <= 0).count();
        this.proportion = noFixedBug.doubleValue() / noBug.doubleValue();
        //System.out.println(noFixedBug.toString() + " / " + noBug.toString() + " = " + proportion.toString() );
    }

    @Override
    public void computeProportion(Release release) {
        if (this.release == null) {
            this.release = release;
            computeProportion();
        }
        if (release.getReleaseDate().compareTo(this.release.getReleaseDate()) == 0) {
            return;
        } else {
            this.release = release;
            computeProportion();
        }
    }

}
