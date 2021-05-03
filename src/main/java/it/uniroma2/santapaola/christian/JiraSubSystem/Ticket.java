package it.uniroma2.santapaola.christian.JiraSubSystem;

import java.time.LocalDate;

public class Ticket {
    private String ID;
    private String key;
    private String jiraUrl;
    private LocalDate creationDate;
    private LocalDate resolutionDate;

    public Ticket(String ID, String key, String jiraUrl, LocalDate creationDate, LocalDate resolutionDate) {
        this.ID = ID;
        this.key = key;
        this.jiraUrl = jiraUrl;
        this.creationDate = creationDate;
        this.resolutionDate = resolutionDate;
    }


    public String getID() {
        return ID;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public String getKey() {
        return key;
    }

    public String getJiraUrl() {
        return jiraUrl;
    }

    public LocalDate getResolutionDate() {
        return resolutionDate;
    }


    @Override
    public String toString() {
        return "Ticket{" +
                "ID='" + ID + '\'' +
                ", key='" + key + '\'' +
                ", jiraUrl='" + jiraUrl + '\'' +
                ", creationDate=" + creationDate +
                ", resolutionDate=" + resolutionDate +
                '}';
    }
}
