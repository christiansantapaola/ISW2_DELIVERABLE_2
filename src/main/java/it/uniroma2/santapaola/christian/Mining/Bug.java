package it.uniroma2.santapaola.christian.Mining;

import it.uniroma2.santapaola.christian.GitSubSystem.Commit;
import it.uniroma2.santapaola.christian.JiraSubSystem.Ticket;

import java.time.LocalDate;


public class Bug {

    private Commit commit;
    private Ticket ticket;

    public Bug(Commit commit, Ticket ticket) {
        this.ticket = ticket;
        this.commit = commit;
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
        return commit.wasFileModifiedInThisCommit(path);
    }

    @Override
    public String toString() {
        return "Bug{" +
                "commit=" + commit +
                ", ticket=" + ticket +
                '}';
    }

}
