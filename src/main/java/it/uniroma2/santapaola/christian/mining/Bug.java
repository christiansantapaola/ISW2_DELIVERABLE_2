package it.uniroma2.santapaola.christian.mining;

import it.uniroma2.santapaola.christian.git.Commit;
import it.uniroma2.santapaola.christian.jira.Ticket;

import java.time.LocalDate;
import java.util.Set;


/**
 * La classe bug si occupa di associare un ticket di un bug preso da jira con l'apposito commit preso da git.
 */
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
