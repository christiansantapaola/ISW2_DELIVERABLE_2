package it.uniroma2.santapaola.christian.JiraSubSystem;

import it.uniroma2.santapaola.christian.utility.CSVWriter;
import it.uniroma2.santapaola.christian.utility.JsonHelper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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

        Integer j = 0, i = 0, total = 1;
        List<Ticket> result = new LinkedList<Ticket>();
        //Get JSON API for closed bugs w/ AV in the project
        do {
            //Only gets a max of 1000 at a time, so must do this multiple times  f bugs >1000
            j = i + 1000;
            String url = queryBuilder.getSearchQuery(i, j);
            JSONObject json = JsonHelper.readJsonFromUrl(url);
            JSONArray issues = json.getJSONArray("issues");
            total = json.getInt("total");
            for (; i < total && i < j; i++) {
                //Iterate through each bug
                //String key = issues.getJSONObject(i%1000).get("key").toString();
                //result.add(key);
                Ticket ticket = jsonToTicket(issues.getJSONObject(i%1000));
                result.add(ticket);
            }
        } while (i < total);
        return result;
    }

    private Release jsonToRelease(JSONObject json) {
        String id = json.getString("id");
        String name = json.getString("name");
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate releaseDate = LocalDate.parse(json.getString("releaseDate"), dateFormatter);
        Release release = new Release(name, id, releaseDate);
        return release;
    }

    private Ticket jsonToTicket(JSONObject json) {
        String id = json.getString("id");
        String key = json.getString("key");
        String url = json.getString("self");
        JSONObject fields = json.getJSONObject("fields");
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSxxxx");
        LocalDate resolutionDate = LocalDate.parse(fields.getString("resolutiondate"), dateFormatter);
        LocalDate creationDate = LocalDate.parse(fields.getString("created"), dateFormatter);
        Ticket ticket = new Ticket(id, key, url, creationDate, resolutionDate);
        return ticket;
    }

    /**
     * getRelease() return a list of all release of the given project.
     * @return List<Release>
     * @throws IOException
     * @throws JSONException
     */
    public ReleaseTimeline getReleases() throws IOException, JSONException{
        String url = queryBuilder.getReleasesQuery();
        ReleaseTimeline releaseTimeline = new ReleaseTimeline();
        JSONObject json = JsonHelper.readJsonFromUrl(url);
        JSONArray versions = json.getJSONArray("versions");
        List<Release> releases = new ArrayList<Release>(versions.length());
        for (int i = 0; i < versions.length(); i++) {
            String name = "";
            String id = "";
            LocalDateTime localDateTime;
            if(versions.getJSONObject(i).has("releaseDate")) {
                if (versions.getJSONObject(i).has("name"))
                    name = versions.getJSONObject(i).get("name").toString();
                if (versions.getJSONObject(i).has("id"))
                    id = versions.getJSONObject(i).get("id").toString();
                String strDate = versions.getJSONObject(i).get("releaseDate").toString();
                LocalDate date = LocalDate.parse(strDate);
                releaseTimeline.insertRelease(name, id, date);
            }
        }
        return releaseTimeline;
    }

    static public void ReleasesToCSV(File csvOutput, List<Release> releases) throws IOException {
        String[] fieldName = {"Index", "Version ID", "Version Name", "Date"};
        CSVWriter csvWriter = new CSVWriter(csvOutput, fieldName);
        if (releases.size() < 6)
            return;
        Integer index = 1;
        csvWriter.writeFieldName();
        for (Release release : releases) {
            String[] values = {index.toString(), release.getID(), release.getName(), release.getReleaseDate().toString()};
            csvWriter.writeLine(values);
            index++;
        }
        csvWriter.flush();
    }

}
