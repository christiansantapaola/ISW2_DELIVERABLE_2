import it.uniroma2.santapaola.christian.JiraSubSystem.JiraHandler;
import it.uniroma2.santapaola.christian.JiraSubSystem.Release;
import it.uniroma2.santapaola.christian.JiraSubSystem.Ticket;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

class JiraHandlerTest {

    private static JiraHandler jiraHandler;

    @BeforeAll
    public static void configure() {
        jiraHandler = new JiraHandler("BOOKKEEPER", "https://issues.apache.org");
    }

    @org.junit.jupiter.api.Test
    void getID() throws IOException {
        List<Ticket> IDList = jiraHandler.getBugTicket();
        for (Ticket ID : IDList) {
            System.out.println(ID);
        }
    }

    @Test
    void getReleases() throws Exception {
        for (Release release: jiraHandler.getReleases()) {
            System.out.println(release);
        }
    }

    @Test
    public void printNoRelease() {
        Double d = 0.95 * (9 - 7);
        System.out.println(Math.round(1.4));
        System.out.println(Math.round(1.5));
        System.out.println(Math.round(1.6));
    }


}