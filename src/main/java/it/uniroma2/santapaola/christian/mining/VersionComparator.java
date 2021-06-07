package it.uniroma2.santapaola.christian.mining;

import it.uniroma2.santapaola.christian.git.GitConstants;
import it.uniroma2.santapaola.christian.jira.ReleaseComparator;

import java.util.Comparator;

public class VersionComparator implements Comparator<Version> {

    private static final ReleaseComparator rc = new ReleaseComparator();

    @Override
    public int compare(Version o1, Version o2) {
        if (o1.getTag().getId().equals(GitConstants.EMPTY_TREE_ID)) {
            if (o1.getTag().getId().equals(o2.getTag().getId())) {
                return 0;
            } else {
                return -1;
            }
        }
        if (o1.getTag().getId().equals(GitConstants.HEAD)) {
            if (o1.getTag().getId().equals(o2.getTag().getId())) {
                return 0;
            } else {
                return 1;
            }
        }
        return rc.compare(o1.getRelease(), o2.getRelease());

    }
}
