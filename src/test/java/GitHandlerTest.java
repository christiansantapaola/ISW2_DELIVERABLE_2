import it.uniroma2.santapaola.christian.GitSubSystem.Commit;
import it.uniroma2.santapaola.christian.GitSubSystem.DiffStat;
import it.uniroma2.santapaola.christian.GitSubSystem.Exception.GitHandlerException;
import it.uniroma2.santapaola.christian.GitSubSystem.GitHandler;
import it.uniroma2.santapaola.christian.GitSubSystem.Tag;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;


public class GitHandlerTest {

    static void printCommit(RevCommit commit) {
        System.out.println("commit: " + commit);
        System.out.println("    author: " + commit.getAuthorIdent());
        System.out.println("    commit Time: " + commit.getCommitTime());
        System.out.println("    commit Ident: " + commit.getCommitterIdent());
        System.out.println("    Full Message: " + commit.getFullMessage());
    }

    @org.junit.jupiter.api.Test
    void CloneRepository() throws Exception {
        Git git = Git.cloneRepository()
                .setURI("https://github.com/apache/incubator-s2graph")
                .setDirectory(new File("repository"))
                .setCloneAllBranches(true)
                .call();
    }

    @org.junit.jupiter.api.Test
    void openRepository() throws Exception {
        FileRepositoryBuilder repositoryBuilder = new FileRepositoryBuilder();
        repositoryBuilder.setMustExist(true);
        repositoryBuilder.setGitDir(new File("repository/.git/"));
        Repository repository = repositoryBuilder.build();
        Git git = new Git(repository);
    }

    @org.junit.jupiter.api.Test
    void Log() throws Exception {
        FileRepositoryBuilder repositoryBuilder = new FileRepositoryBuilder();
        repositoryBuilder.setMustExist(true);
        repositoryBuilder.setGitDir(new File("repository/.git/"));
        Repository repository = repositoryBuilder.build();
        Git git = new Git(repository);
        ObjectId head = repository.resolve(Constants.HEAD);
        Iterable<RevCommit> commits = git.log().add(head).call();
        for (RevCommit commit : commits) {
            System.out.println("commit: " + commit);
            System.out.println("    author: " + commit.getAuthorIdent());
            System.out.println("    commit Time: " + commit.getCommitTime());
            System.out.println("    commit Ident: " + commit.getCommitterIdent());
            System.out.println("    Full Message: " + commit.getFullMessage());
        }
    }

    @org.junit.jupiter.api.Test
    void gitGrepTest() throws Exception {
        GitHandler gitHandler = new GitHandler(new File("repository/bookkeeper/.git/"));
        List<Commit> commits = gitHandler.grep("BOOKKEEPER-145");
        for (Commit commit : commits) {
            System.out.println(commit);
        }
    }

    @Test
    void gitLogTest() throws IOException, GitHandlerException {
        GitHandler gitHandler = new GitHandler(new File("repository/bookkeeper/.git/"));
        List<Commit> commits = gitHandler.log("bookkeeper-server/src/main/java/org/apache/bookkeeper/zookeeper/ZooWorker.java");
        System.out.println(commits.size());
        for (Commit commit : commits) {
            System.out.println(commit);
        }
    }

    @Test
    void gitDiff() throws Exception {
        GitHandler gitHandler = new GitHandler(new File("repository/bookkeeper/.git/"));
        String tag = "release-4.1.0";
        String c2 = "release-4.0.0";
        for (DiffStat diffStat : gitHandler.diff(c2, tag)) {
            System.out.println(diffStat);
        }
    }

    @Test
    void gitTag() throws Exception {
        GitHandler gitHandler = new GitHandler(new File("repository/bookkeeper/.git/"));
        for (Tag tag : gitHandler.getAllTags()) {
            System.out.println(tag);
            Commit commit = gitHandler.getTagCommit(tag.getId()).get();
            for (String file : commit.getRepositorySnapshot()) {
                System.out.println(file);
            }

        }
    }

    @Test
    void gitHistory() throws Exception {
        GitHandler gitHandler = new GitHandler(new File("repository/bookkeeper/.git/"));
        for (Commit commit : gitHandler.log("README.md")) {
            System.out.println(commit);
        }
        List<DiffStat> diffs = gitHandler.getFileDiffHistory("README.md");
        for (DiffStat diffStat : diffs) {
            System.out.println(diffStat);
        }
    }

    @Test
    void gitLogKeyTest() throws Exception {
        GitHandler git = new GitHandler(new File("repository/bookkeeper/.git/"));
        String key = "BOOKKEEPER-1105";
        List<Commit> commits = git.grep(key);
        for (Commit commit : commits) {
            System.out.println(commit);
            System.out.println("----------------------------------------------------------------------------------------");
            System.out.println(commit.getFullMessage());
            System.out.println("----------------------------------------------------------------------------------------");

        }
    }

    @Test
    void gitLogTesttag() throws Exception {
        GitHandler git = new GitHandler(new File("repository/bookkeeper/.git/"));
        System.out.println(" git.log(Optional.empty(), Optional.of(\"release-4.5.1\"), true);");
        List<Commit> commits = git.log(Optional.empty(), Optional.of("release-4.5.1"), true);
        System.out.println(commits.get(0).getCommitterCommitTime());
        System.out.println(commits.get(commits.size() - 1).getCommitterCommitTime());
        System.out.println("------------------------------------------------------------------------------------------");
        System.out.println("git.log(Optional.of(\"release-4.1.0\"), Optional.empty(), true);");
        commits = git.log(Optional.of("refs/tags/release-4.1.0"), Optional.empty(), true);
        System.out.println(commits.get(0).getCommitterCommitTime());
        System.out.println(commits.get(commits.size() - 1).getCommitterCommitTime());
        System.out.println("------------------------------------------------------------------------------------------");
        System.out.println("git.log(Optional.of(\"release-4.1.0\"), Optional.of(\"release-4.5.1\"), true);");
        commits = git.log(Optional.of("release-4.1.0"), Optional.of("release-4.5.1"), true);
        System.out.println(commits.get(0).getCommitterCommitTime());
        System.out.println(commits.get(commits.size() - 1).getCommitterCommitTime());


    }

}
