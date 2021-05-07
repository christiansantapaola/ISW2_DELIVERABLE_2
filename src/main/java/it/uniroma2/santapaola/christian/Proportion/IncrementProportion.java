package it.uniroma2.santapaola.christian.Proportion;

import it.uniroma2.santapaola.christian.JiraSubSystem.Release;
import it.uniroma2.santapaola.christian.Mining.Bug;
import it.uniroma2.santapaola.christian.Mining.Version;

import java.util.List;

public class IncrementProportion extends Proportion {

    private final List<Bug> bugs;

    public IncrementProportion(List<Bug> bugs) {
            this.bugs = bugs;
    }

    @Override
    public void computeProportion(Version version) {
        if (version == null) return;
        Long noFixedBug = bugs.stream().filter(bug -> bug.getFixCommitDate().compareTo(version.getRelease().getReleaseDate()) <= 0).count();
        Long noBug = bugs.stream().filter(bug -> bug.getTicketCreationDate().compareTo(version.getRelease().getReleaseDate()) <= 0).count();
        this.proportion = noFixedBug.doubleValue() / noBug.doubleValue();
    }

}
