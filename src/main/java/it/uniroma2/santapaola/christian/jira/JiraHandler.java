package it.uniroma2.santapaola.christian.jira;

import it.uniroma2.santapaola.christian.utility.JsonHelper;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
/**
 *  it.uniroma2.santapaola.christian.JiraSubSystem.JiraHandler is a class which interface with JIRA.
 *  This class will handle query to jira given a project, parse the output and return it in a
 *  format usable in java.
 *
 * @author Christian Santapaola
 */
public class JiraHandler {

    private final String jiraUrl;
    private final String projectName;
    private JiraQueryBuilder queryBuilder;
    private static final String RELEASE_DATE = "releaseDate";


    /**
     * Constructor of it.uniroma2.santapaola.christian.JiraSubSystem.JiraHandler
     * @param projectName, is the name of the project on JIRA.
     */
    public JiraHandler(String projectName, String jiraUrl) {
        this.projectName = projectName;
        this.jiraUrl = jiraUrl;
        queryBuilder = new JiraQueryBuilder(jiraUrl,projectName);
    }


    /**
     * getter of projectName
     * @return String ProjectName
     */
    public String getProjectName() {
        return projectName;
    }

    /**
     * getter
     * @return String jiraUrl
     */
    public String getJiraUrl() { return jiraUrl; }

    /**
     * getBugTicketID() send a query to JIRA and return a list of bug identifier in the format [projectName-ID]
     * @return List<String> containg the bug identifier in the format [projectName-ID]
     * @throws IOException
     * @throws JSONException
     */
    public List<Ticket> getBugTicket() throws IOException, JSONException {

        var j = 0;
        var i = 0;
        var total = 1;
        List<Ticket> result = new LinkedList<>();
        //Get JSON API for closed bugs w/ AV in the project
        do {
            //Only gets a max of 1000 at a time, so must do this multiple times  f bugs >1000
            j = i + 1000;
            var url = queryBuilder.getSearchQuery(i, j);
            var json = JsonHelper.readJsonFromUrl(url);
            var issues = json.getJSONArray("issues");
            total = json.getInt("total");
            for (; i < total && i < j; i++) {
                //Iterate through each bug
                Ticket ticket = jsonToTicket(issues.getJSONObject(i%1000));
                result.add(ticket);
            }
        } while (i < total);
        return result;
    }


    private Ticket jsonToTicket(JSONObject json) {
        var id = json.getString("id");
        var key = json.getString("key");
        var url = json.getString("self");
        var fields = json.getJSONObject("fields");
        var dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSxxxx");
        var resolutionDate = LocalDate.parse(fields.getString("resolutiondate"), dateFormatter);
        var creationDate = LocalDate.parse(fields.getString("created"), dateFormatter);
        return new Ticket(id, key, url, creationDate, resolutionDate);

    }

    /**
     * getRelease() return a list of all release of the given project.
     * @return List<Release>
     * @throws IOException
     * @throws JSONException
     */
    public ReleaseTimeline getReleases() throws IOException, JSONException{
        String url = queryBuilder.getReleasesQuery();
        var releaseTimeline = new ReleaseTimeline();
        JSONObject json = JsonHelper.readJsonFromUrl(url);
        var versions = json.getJSONArray("versions");
        for (var i = 0; i < versions.length(); i++) {
            var name = "";
            var id = "";
            if(versions.getJSONObject(i).has(RELEASE_DATE)) {
                if (versions.getJSONObject(i).has("name"))
                    name = versions.getJSONObject(i).get("name").toString();
                if (versions.getJSONObject(i).has("id"))
                    id = versions.getJSONObject(i).get("id").toString();
                var strDate = versions.getJSONObject(i).get(RELEASE_DATE).toString();
                var date = LocalDate.parse(strDate);
                releaseTimeline.insertRelease(name, id, date);
            }
        }
        return releaseTimeline;
    }

}
