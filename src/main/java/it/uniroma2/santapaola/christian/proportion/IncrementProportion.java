package it.uniroma2.santapaola.christian.proportion;

import it.uniroma2.santapaola.christian.mining.Bug;
import it.uniroma2.santapaola.christian.mining.Version;

import java.util.List;

/**
 * Questa classe implemtenta l'algoritmo IncrementProportion,
 * Data una versione, calcola il proportion, come il ratio tra il numero di bug risolti rispetto al numero di bug esistenti.
 */
public class IncrementProportion extends Proportion {

    private final List<Bug> bugs;

    public IncrementProportion(List<Bug> bugs) {
            this.bugs = bugs;
    }

    @Override
    public void computeProportion(Version version) {
        if (version == null) return;
        long noFixedBug = bugs.stream().filter(bug -> bug.getFixCommitDate().compareTo(version.getRelease().getReleaseDate()) <= 0).count();
        long noBug = bugs.stream().filter(bug -> bug.getTicketCreationDate().compareTo(version.getRelease().getReleaseDate()) <= 0).count();
        this.p = (double) noFixedBug / (double) noBug;
    }

}
