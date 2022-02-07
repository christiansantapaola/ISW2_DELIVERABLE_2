package it.uniroma2.santapaola.christian.jira;


/**
 * La classe JiraQueryBuilder si occupa di costruire query per il servizio jira.
 */
public class JiraQueryBuilder {
    private String jiraUrl;
    private String projectName;


    public JiraQueryBuilder(String jiraUrl, String projectName) {
        this.jiraUrl = jiraUrl;
        this.projectName = projectName;
    }


    public String getSearchQuery(int startAt, int maxResult) {
        return jiraUrl + "/jira/rest/api/2/search?jql=project=%22"
                + projectName + "%22AND%22issueType%22=%22Bug%22AND(%22status%22=%22closed%22OR"
                + "%22status%22=%22resolved%22)AND%22resolution%22=%22"
                + "fixed" + "%22&fields=key,resolutiondate,versions,created&startAt="
                + startAt + "&maxResults=" + maxResult;
    }

    public String getReleasesQuery() {
        return jiraUrl + "/jira/rest/api/2/project/" + projectName;
    }
}
