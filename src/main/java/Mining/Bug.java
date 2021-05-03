package Mining;

import GitSubSystem.Commit;
import JiraSubSystem.Release;
import JiraSubSystem.Ticket;
import Proportion.Proportion;

import java.time.LocalDate;
import java.util.*;


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