package it.uniroma2.santapaola.christian.jira;

import it.uniroma2.santapaola.christian.utility.JsonHelper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
/**
 *  it.uniroma2.santapaola.christian.JiraSubSystem.JiraHandler è una classe che si occupa
 *  di interfacciarsi ad un servizio jira e ritornare le informazioni richieste in
 *  un formato nativo in java.
 *
 * @author Christian Santapaola
 */
public class JiraHandler {

    private final String jiraUrl;
    private final String projectName;
    private JiraQueryBuilder queryBuilder;
    private static final String RELEASE_DATE = "releaseDate";


    /**
     * Construttore di JiraHandler
     * @param projectName, nome del progetto.
     */
    public JiraHandler(String projectName, String jiraUrl) {
        this.projectName = projectName;
        this.jiraUrl = jiraUrl;
        queryBuilder = new JiraQueryBuilder(jiraUrl,projectName);
    }


    /**
     * getter
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
     * getBugTicketID() invia una query a jira e ritorna una lista di bug ticket nell formato [projectName-ID]
     * @return List<String> contiene i bug ticket nel formato [projectName-ID]
     * @throws IOException
     * @throws JSONException
     */
    public List<Ticket> getBugTicket() throws IOException, JSONException {

        int j = 0;
        int i = 0;
        int total = 1;
        List<Ticket> result = new LinkedList<>();
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
                Ticket ticket = jsonToTicket(issues.getJSONObject(i%1000));
                result.add(ticket);
            }
        } while (i < total);
        return result;
    }


    private Ticket jsonToTicket(JSONObject json) {
        String id = json.getString("id");
        String key = json.getString("key");
        String url = json.getString("self");
        JSONObject fields = json.getJSONObject("fields");
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSxxxx");
        LocalDate resolutionDate = LocalDate.parse(fields.getString("resolutiondate"), dateFormatter);
        LocalDate creationDate = LocalDate.parse(fields.getString("created"), dateFormatter);
        return new Ticket(id, key, url, creationDate, resolutionDate);

    }

    /**
     * getRelease() ritorna tutte le release di un progetto software.
     * @return ReleaseTimeline.
     * @throws IOException
     * @throws JSONException
     */
    public ReleaseTimeline getReleases() throws IOException, JSONException{
        String url = queryBuilder.getReleasesQuery();
        ReleaseTimeline releaseTimeline = new ReleaseTimeline();
        JSONObject json = JsonHelper.readJsonFromUrl(url);
        JSONArray versions = json.getJSONArray("versions");
        for (int i = 0; i < versions.length(); i++) {
            String name = "";
            String id = "";
            if(versions.getJSONObject(i).has(RELEASE_DATE)) {
                if (versions.getJSONObject(i).has("name"))
                    name = versions.getJSONObject(i).get("name").toString();
                if (versions.getJSONObject(i).has("id"))
                    id = versions.getJSONObject(i).get("id").toString();
                String strDate = versions.getJSONObject(i).get(RELEASE_DATE).toString();
                LocalDate date = LocalDate.parse(strDate);
                releaseTimeline.insertRelease(name, id, date);
            }
        }
        return releaseTimeline;
    }

}
