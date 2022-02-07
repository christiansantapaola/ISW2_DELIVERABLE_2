package it.uniroma2.santapaola.christian.git;


import java.time.LocalDate;

/**
 * la classe Commit tiene traccia delle informazioni di un commit.
 */
public class Commit {
    private String name;
    private String author;
    private String committer;
    private LocalDate committerCommitTime;
    private LocalDate authorCommitTime;


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
}
