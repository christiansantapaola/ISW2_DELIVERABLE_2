package it.uniroma2.santapaola.christian.git;

import java.util.Comparator;

public class CommitComparator implements Comparator<Commit> {

    @Override
    public int compare(Commit o1, Commit o2) {
        return o1.getCommitterCommitTime().compareTo(o2.getCommitterCommitTime());
    }
}
