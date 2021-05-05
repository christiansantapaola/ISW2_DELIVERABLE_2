package it.uniroma2.santapaola.christian.Mining;

import it.uniroma2.santapaola.christian.GitSubSystem.Commit;
import it.uniroma2.santapaola.christian.GitSubSystem.Exception.GitHandlerException;
import it.uniroma2.santapaola.christian.GitSubSystem.Git;
import it.uniroma2.santapaola.christian.GitSubSystem.GitFactory;
import it.uniroma2.santapaola.christian.JiraSubSystem.JiraHandler;
import it.uniroma2.santapaola.christian.JiraSubSystem.ReleaseTimeline;
import it.uniroma2.santapaola.christian.JiraSubSystem.Ticket;
import it.uniroma2.santapaola.christian.Mining.Exception.NoReleaseFoundException;
import it.uniroma2.santapaola.christian.Proportion.IncrementProportion;
import it.uniroma2.santapaola.christian.Proportion.Proportion;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;


/**
 * it.uniroma2.santapaola.christian.Mining.RepositoryMiner Extract Information from a git repository using information from Jira.
 */
public class RepositoryMiner {
    private Git git;
    private JiraHandler jira;
    private ReleaseTimeline releases;
    private Proportion proportion;
    private Timeline timeline;
    private List<Bug> bugs;
    private List<Ticket> tickets;
    private ProjectState state;

    /**
     * Constructor of it.uniroma2.santapaola.christian.Mining.RepositoryMiner instance.
     *
     * @param gitPath         must be a File object that point to /project/.git directory
     * @param jiraProjectName Project Name ID on JIRA issue.apache
     * @throws IOException
     */
    public RepositoryMiner(String gitPath, String gitUrl, String jiraProjectName, String jiraUrl) throws IOException, GitHandlerException {
        GitFactory gitFactory = new GitFactory(gitUrl, gitPath);
        gitFactory.setGitProcess();
        git = gitFactory.build();
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

    public Bug createBug(Ticket ticket, Commit commit) throws GitHandlerException {
        Set<String> snapshot = git.getChangedFiles(commit, ".java");
        return new Bug(commit, ticket, snapshot);
    }

    public ProjectState newProjectState() throws NoReleaseFoundException, GitHandlerException, IOException {
        return new ProjectState(jira.getProjectName(), git, timeline);
    }
}





