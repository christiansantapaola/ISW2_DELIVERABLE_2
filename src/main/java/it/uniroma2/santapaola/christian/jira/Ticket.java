package it.uniroma2.santapaola.christian.jira;

import java.time.LocalDate;

/**
 * La classe Ticket salva le informazioni riguardanti un ticket jira.
 */
public class Ticket {
    private String id;
    private String key;
    private String jiraUrl;
    private LocalDate creationDate;
    private LocalDate resolutionDate;

    public Ticket(String id, String key, String jiraUrl, LocalDate creationDate, LocalDate resolutionDate) {
        this.id = id;
        this.key = key;
        this.jiraUrl = jiraUrl;
        this.creationDate = creationDate;
        this.resolutionDate = resolutionDate;
    }


    public String getId() {
        return id;
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
                "ID='" + id + '\'' +
                ", key='" + key + '\'' +
                ", jiraUrl='" + jiraUrl + '\'' +
                ", creationDate=" + creationDate +
                ", resolutionDate=" + resolutionDate +
                '}';
    }
}
