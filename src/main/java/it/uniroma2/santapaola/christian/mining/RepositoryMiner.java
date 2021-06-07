package it.uniroma2.santapaola.christian.mining;

import it.uniroma2.santapaola.christian.git.Commit;
import it.uniroma2.santapaola.christian.git.CommitComparator;
import it.uniroma2.santapaola.christian.git.exception.GitHandlerException;
import it.uniroma2.santapaola.christian.git.Git;
import it.uniroma2.santapaola.christian.git.GitFactory;
import it.uniroma2.santapaola.christian.jira.JiraHandler;
import it.uniroma2.santapaola.christian.jira.ReleaseTimeline;
import it.uniroma2.santapaola.christian.jira.Ticket;
import it.uniroma2.santapaola.christian.mining.exception.NoReleaseFoundException;
import it.uniroma2.santapaola.christian.proportion.Proportion;
import it.uniroma2.santapaola.christian.proportion.ProportionBuilder;
import it.uniroma2.santapaola.christian.proportion.SimpleProportion;

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
    private static final CommitComparator cc = new CommitComparator();

    /**
     * Constructor of it.uniroma2.santapaola.christian.Mining.RepositoryMiner instance.
     *
     * @param gitPath         must be a File object that point to /project/.git directory
     * @param jiraProjectName Project Name ID on JIRA issue.apache
     * @throws IOException
     */
    public RepositoryMiner(String gitPath, String gitUrl, String jiraProjectName, String jiraUrl, String tagPattern) throws IOException, GitHandlerException {
        var gitFactory = new GitFactory(gitUrl, gitPath);
        gitFactory.setGitProcess();
        git = gitFactory.build();
        jira = new JiraHandler(jiraProjectName, jiraUrl);
        releases = jira.getReleases();
        tickets = jira.getBugTicket();
        bugs = getBugs();
        proportion = new SimpleProportion();
        timeline = new Timeline(bugs, releases, git, ProportionBuilder.getIncrementProportion(), tagPattern);
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
            Optional<Commit> commit = commits.stream().min(cc);
            if (commit.isPresent()) {
                Bug bug = createBug(ticket, commit.get());
                result.add(bug);
            }
        }
        return result;
    }

    public Bug createBug(Ticket ticket, Commit commit) throws GitHandlerException {
        Set<String> snapshot = git.getChangedFiles(commit, ".*\\.java");
        return new Bug(commit, ticket, snapshot);
    }

    public ProjectState newProjectState() throws NoReleaseFoundException, GitHandlerException, IOException {
        return new ProjectState(jira.getProjectName(), git, timeline);
    }
}





