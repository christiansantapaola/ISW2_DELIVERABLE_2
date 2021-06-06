package it.uniroma2.santapaola.christian.Mining;

import it.uniroma2.santapaola.christian.GitSubSystem.Commit;
import it.uniroma2.santapaola.christian.JiraSubSystem.Ticket;

import java.time.LocalDate;
import java.util.Set;


public class Bug {

    private Commit commit;
    private Ticket ticket;
    private Set<String> affectedFile;

    public Bug(Commit commit, Ticket ticket, Set<String> affectedFile) {
        this.ticket = ticket;
        this.commit = commit;
        this.affectedFile = affectedFile;
    }


    public String getBugID() {
        return ticket.getKey();
    }

    public LocalDate getFixCommitDate() {
        return commit.getCommitterCommitTime();
    }

    public LocalDate getTicketCreationDate() {
        return ticket.getCreationDate();
    }

    public boolean isFileAffected(String path) {
        return affectedFile.contains(path);
    }

    @Override
    public String toString() {
        return "Bug{" +
                "commit=" + commit +
                ", ticket=" + ticket +
                '}';
    }

    public Set<String> getAffectedFile() {
        return affectedFile;
    }
}
