package it.uniroma2.santapaola.christian.mining;

import it.uniroma2.santapaola.christian.git.Tag;
import it.uniroma2.santapaola.christian.jira.Release;

/**
 * Version classe associa le release presa da jira con il corrispondente Tag preso dal repository git.
 */
public class Version {
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
