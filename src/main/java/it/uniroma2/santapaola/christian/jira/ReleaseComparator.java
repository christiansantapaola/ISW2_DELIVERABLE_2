package it.uniroma2.santapaola.christian.jira;

import java.util.Comparator;

public class ReleaseComparator implements Comparator<Release> {
    @Override
    public int compare(Release o1, Release o2) {
        return o1.getReleaseDate().compareTo(o2.getReleaseDate());
    }
}
