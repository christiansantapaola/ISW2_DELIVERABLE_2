package it.uniroma2.santapaola.christian.Mining;

import it.uniroma2.santapaola.christian.GitSubSystem.GitConstants;
import it.uniroma2.santapaola.christian.GitSubSystem.Tag;
import it.uniroma2.santapaola.christian.JiraSubSystem.Release;

public class Version implements Comparable<Version> {
    private Release release;
    private Tag tag;
    private long noVersion;

    public Version(Release release, Tag tag, long noVersion) {
        this.release = release;
        this.tag = tag;
        this.noVersion = noVersion;
    }



    public long getNoVersion() {
        return noVersion;
    }


    public Release getRelease() {
        return release;
    }

    public Tag getTag() {
        return tag;
    }

    @Override
    public int compareTo(Version o) {
        if (tag.getId().equals(GitConstants.EMPTY_TREE_ID)) {
            if (tag.getId().equals(o.tag.getId())) {
                return 0;
            } else {
                return -1;
            }
        }
        if (tag.getId().equals(GitConstants.HEAD)) {
            if (tag.getId().equals(o.tag.getId())) {
                return 0;
            } else {
                return 1;
            }
        }
        return release.compareTo(o.getRelease());
    }

    public long getVersionDiff(Version v) {
        return v.getNoVersion() - this.getNoVersion();
    }

    @Override
    public String toString() {
        return "Version{" +
                "release=" + release +
                ", tag=" + tag +
                ", noVersion=" + noVersion +
                '}';
    }
}
