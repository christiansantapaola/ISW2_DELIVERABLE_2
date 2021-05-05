package it.uniroma2.santapaola.christian.GitSubSystem;


import org.eclipse.jgit.revwalk.RevCommit;

import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

public class Commit implements Comparable<Commit>{
    private String name;
    private String author;
    private String committer;
    private LocalDate committerCommitTime;
    private LocalDate authorCommitTime;


    public Commit(RevCommit commit, List<String> repositorySnapshot, List<DiffStat> modifiedFiles) {
        name = commit.getName();
        author = commit.getAuthorIdent().toString();
        committer = commit.getCommitterIdent().toString();
        committerCommitTime = Instant.ofEpochMilli(commit.getCommitTime() * 1000L)
                .atZone(commit.getCommitterIdent().getTimeZone().toZoneId())
                .toLocalDate();
        authorCommitTime = Instant.ofEpochMilli(commit.getCommitTime() * 1000L)
                .atZone(commit.getAuthorIdent().getTimeZone().toZoneId())
                .toLocalDate();
    }

    public Commit(String name, String author, LocalDate authorCommitTime,
                  String committer, LocalDate committerCommitTime,
                  List<DiffStat> modifiedFiles) {
        this.name = name;
        this.author = author;
        this.committer = committer;
        this.committerCommitTime = committerCommitTime;
        this.authorCommitTime = authorCommitTime;
    }

    public Commit(String name,
                  String author,
                  LocalDate authorCommitTime,
                  String committer,
                  LocalDate committerCommitTime) {
        this.name = name;
        this.author = author;
        this.committer = committer;
        this.committerCommitTime = committerCommitTime;
        this.authorCommitTime = authorCommitTime;
    }


    public String getName() { return name;}

    public String getAuthor() {
        return author;
    }

    public String getCommitter() {
        return committer;
    }

    public LocalDate getCommitterCommitTime() {
        return committerCommitTime;
    }

    public LocalDate getAuthorCommitTime() {
        return authorCommitTime;
    }


    @Override
    public String toString() {
        return "Commit{" +
                "name='" + name + '\'' +
                ", author='" + author + '\'' +
                ", committer='" + committer + '\'' +
                ", committerCommitTime=" + committerCommitTime +
                ", authorCommitTime=" + authorCommitTime +
                '}';
    }

    @Override
    public int compareTo(Commit o) {
        return this.getCommitterCommitTime().compareTo(o.getCommitterCommitTime());
    }
}
