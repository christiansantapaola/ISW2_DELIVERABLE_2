package it.uniroma2.santapaola.christian.GitSubSystem;


import org.eclipse.jgit.revwalk.RevCommit;

import java.time.Instant;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class Commit implements Comparable<Commit>{
    private String name;
    private String author;
    private String committer;
    private String fullMessage;
    private LocalDate committerCommitTime;
    private LocalDate authorCommitTime;
    private Set<String> repositorySnapshot;
    private List<DiffStat> modifiedFiles;
    private HashSet<String> modifiedNewFiles;


    public Commit(RevCommit commit, List<String> repositorySnapshot, List<DiffStat> modifiedFiles) {
        name = commit.getName();
        author = commit.getAuthorIdent().toString();
        committer = commit.getCommitterIdent().toString();
        fullMessage = commit.getFullMessage();
        committerCommitTime = Instant.ofEpochMilli(commit.getCommitTime() * 1000L)
                .atZone(commit.getCommitterIdent().getTimeZone().toZoneId())
                .toLocalDate();
        authorCommitTime = Instant.ofEpochMilli(commit.getCommitTime() * 1000L)
                .atZone(commit.getAuthorIdent().getTimeZone().toZoneId())
                .toLocalDate();
        this.repositorySnapshot = new HashSet<>(repositorySnapshot);
        this.modifiedFiles = modifiedFiles;
        this.modifiedNewFiles = new HashSet<>();
        for (DiffStat diffStat : this.modifiedFiles) {
            modifiedNewFiles.add(diffStat.getNewPath());
        }
    }

    public String getName() { return name;}

    public String getAuthor() {
        return author;
    }

    public String getCommitter() {
        return committer;
    }

    public String getFullMessage() {
        return fullMessage;
    }

    public LocalDate getCommitterCommitTime() {
        return committerCommitTime;
    }

    public LocalDate getAuthorCommitTime() {
        return authorCommitTime;
    }

    public Set<String> getRepositorySnapshot() {return repositorySnapshot;}

    public List<String> getFilesFromSnapshot(String ext) {
        List<String> result = new LinkedList<>();
        for (String file : repositorySnapshot) {
            if (file.endsWith(ext)) {
                result.add(file);
            }
        }
        return result;
    }

    @Override
    public String toString() {
        return "Commit{" +
                "name='" + name + '\'' +
                ", author='" + author + '\'' +
                ", committer='" + committer + '\'' +
                //", fullMessage='" + fullMessage + '\'' +
                ", committerCommitTime=" + committerCommitTime +
                ", authorCommitTime=" + authorCommitTime +
                //", files=" + files +
                '}';
    }

    @Override
    public int compareTo(Commit o) {
        return this.getCommitterCommitTime().compareTo(o.getCommitterCommitTime());
    }

    public boolean wasFileModifiedInThisCommit(String file) {
        return modifiedNewFiles.contains(file);
    }

    public int getModifiedFileSize() { return modifiedNewFiles.size(); };
}
