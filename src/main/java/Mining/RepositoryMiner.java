package Mining;

import GitSubSystem.Commit;
import GitSubSystem.Exception.GitHandlerException;
import GitSubSystem.GitHandler;
import JiraSubSystem.JiraHandler;
import JiraSubSystem.ReleaseTimeline;
import JiraSubSystem.Ticket;
import Mining.Exception.NoReleaseFoundException;
import Proportion.IncrementProportion;
import Proportion.Proportion;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;


/**
 * Mining.RepositoryMiner Extract Information from a git repository using information from Jira.
 */
public class RepositoryMiner {
    private GitHandler git;
    private JiraHandler jira;
    private ReleaseTimeline releases;
    private Proportion proportion;
    private Timeline timeline;
    private List<Bug> bugs;
    private List<Ticket> tickets;
    private ProjectState state;

    /**
     * Constructor of Mining.RepositoryMiner instance.
     *
     * @param gitPath         must be a File object that point to /project/.git directory
     * @param jiraProjectName Project Name ID on JIRA issue.apache
     * @throws IOException
     */
    public RepositoryMiner(String gitPath, String jiraProjectName, String jiraUrl) throws IOException, GitHandlerException {
        git = new GitHandler(new File(gitPath));
        jira = new JiraHandler(jiraProjectName, jiraUrl);
        releases = jira.getReleases();
        tickets = jira.getBugTicket();
        bugs = getBugs();
        proportion = new IncrementProportion(bugs, releases.getLast().get());
        proportion.computeProportion();
        timeline = new Timeline(bugs, releases, proportion);
    }

    /**
     * constructor of Mining.RepositoryMiner.
     *
     * @param url             url pointing the the remote git repository
     * @param newRepoPath     File object pointing to where to clone the remote repository.
     * @param jiraProjectName Project Name ID on Jira issue.apache
     * @throws IOException
     * @throws GitHandlerException
     */
    public RepositoryMiner(String url, String newRepoPath, String jiraProjectName, String jiraUrl) throws IOException, GitHandlerException {
        git = new GitHandler(url, new File(newRepoPath));
        jira = new JiraHandler(jiraProjectName, jiraUrl);
        releases = jira.getReleases();
        tickets = jira.getBugTicket();
        bugs = getBugs();
        proportion = new IncrementProportion(bugs, releases.getLast().get());
        proportion.computeProportion();
        timeline = new Timeline(bugs, releases, proportion);

    }

    /**
     * getBugs extract all the commit of the fixed bug using the JIRA issues ID.
     *
     * @return List<RevCommit> list of all commit.
     * @throws IOException
     * @throws GitHandlerException
     */
    public List<Bug> getBugs() throws IOException, GitHandlerException {
        List<Bug> result = new LinkedList<>();
        for (Ticket ticket : tickets) {
            List<Commit> commits = git.grep(ticket.getKey() + "[^0-9]");
            Optional<Commit> commit = commits.stream().min(Commit::compareTo);
            if (commit.isPresent()) {
                Bug bug = createBug(ticket, commit.get());
                result.add(bug);
            }
        }
        return result;
    }

    public Bug createBug(Ticket ticket, Commit commit) {
        return new Bug(commit, ticket);
    }

    public ProjectState newProjectState() throws NoReleaseFoundException, GitHandlerException, IOException {
        return new ProjectState(jira.getProjectName(), git, timeline);
    }
}





