package it.uniroma2.santapaola.christian.GitSubSystem;

import it.uniroma2.santapaola.christian.GitSubSystem.Exception.GitHandlerException;
import it.uniroma2.santapaola.christian.GitSubSystem.jgit.GitHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class GitTest {

    private static Git git;
    private static String url;
    private static String repositoryPath;
    private static GitFactory gitFactory;

    @BeforeAll
    static void configure() {
        url = "https://github.com/apache/bookkeeper";
        repositoryPath = "repository/bookkeeper/";
        gitFactory = new GitFactory(url, repositoryPath);
        gitFactory.setGitProcess();
        git = gitFactory.build();
    }

    @org.junit.jupiter.api.Test
    void gitGrepTest() throws Exception {
        List<Commit> commits = git.grep("BOOKKEEPER-145");
        for (Commit commit : commits) {
            System.out.println(commit);
        }
    }

    @Test
    void gitLogTest() throws IOException, GitHandlerException {
        List<Commit> commits = git.log("bookkeeper-server/src/main/java/org/apache/bookkeeper/zookeeper/ZooWorker.java");
        System.out.println(commits.size());
        for (Commit commit : commits) {
            System.out.println(commit);
        }
    }

    @Test
    void gitDiff() throws Exception {
        String tag = "release-4.1.0";
        String c2 = "release-4.0.0";
        List<DiffStat> diffs = git.diff(tag, c2);
        for (DiffStat diffStat : diffs) {
            System.out.println(diffStat);
        }
    }

    @Test
    void gitTag() throws Exception {
        for (Tag tag : git.getAllTags()) {
            System.out.println(tag);
            Commit commit = git.show(tag.getId()).get();
            System.out.println(commit);
        }
    }


    @Test
    void gitLogKeyTest() throws Exception {
        String key = "BOOKKEEPER-1105";
        List<Commit> commits = git.grep(key);
        for (Commit commit : commits) {
            System.out.println(commit);

        }
    }

    @Test
    void gitLogTesttag() throws Exception {
        System.out.println(" git.log(Optional.empty(), Optional.of(\"release-4.5.1\"));");
        List<Commit> commits = git.log(Optional.empty(), Optional.of("release-4.5.1"));
        System.out.println(commits.get(0).getCommitterCommitTime());
        System.out.println(commits.get(commits.size() - 1).getCommitterCommitTime());
        System.out.println("------------------------------------------------------------------------------------------");
        System.out.println("git.log(Optional.of(\"release-4.1.0\"), Optional.empty());");
        commits = git.log(Optional.of("refs/tags/release-4.1.0"), Optional.empty());
        System.out.println(commits.get(0).getCommitterCommitTime());
        System.out.println(commits.get(commits.size() - 1).getCommitterCommitTime());
        System.out.println("------------------------------------------------------------------------------------------");
        System.out.println("git.log(Optional.of(\"release-4.1.0\"), Optional.of(\"release-4.5.1\"));");
        commits = git.log(Optional.of("release-4.1.0"), Optional.of("release-4.5.1"));
        System.out.println(commits.get(0).getCommitterCommitTime());
        System.out.println(commits.get(commits.size() - 1).getCommitterCommitTime());
    }

    @Test
    void gitGetSnapshot() throws Exception{
        Commit commit = git.show("HEAD").get();
        Set<String> files = git.getSnapshot(commit);
        for (String file : files) {
            System.out.println(file);
        }
    }

    @Test
    void gitGetChangedFile() throws Exception{
        Commit commit = git.show("HEAD").get();
        long noChangedFile = git.getNoChangedFiles(commit);
        System.out.println(noChangedFile + " files changed");
        Set<String> chFiles = git.getChangedFiles(commit);
        for (String file : chFiles) {
            System.out.println(file);
        }
        Assertions.assertEquals(noChangedFile, (long) chFiles.size());
    }


}