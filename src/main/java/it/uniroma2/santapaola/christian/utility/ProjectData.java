package it.uniroma2.santapaola.christian.utility;

public class ProjectData {
    private final String projectName;
    private final String jiraProjectName;
    private final String jiraUrl;
    private final String gitUrl;
    private final String tagPattern;

    public ProjectData(String projectName, String jiraProjectName, String jiraUrl, String gitUrl, String tagPattern) {
        this.projectName = projectName;
        this.jiraProjectName = jiraProjectName;
        this.jiraUrl = jiraUrl;
        this.gitUrl = gitUrl;
        this.tagPattern = tagPattern;
    }

    public String getProjectName() {
        return projectName;
    }

    public String getJiraProjectName() {
        return jiraProjectName;
    }

    public String getJiraUrl() {
        return jiraUrl;
    }

    public String getGitUrl() {
        return gitUrl;
    }

    public String getCSVOutput() {
        return projectName + ".csv";
    }

    public String getGitPath() {
        return projectName + "/.git/";
    }

    public String getTagPattern() { return tagPattern; }
}
