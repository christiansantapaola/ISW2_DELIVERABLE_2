package it.uniroma2.santapaola.christian.Mining;

import org.junit.jupiter.api.BeforeAll;

class RepositoryMinerTest {

     public static RepositoryMiner repositoryMiner;
     public static String file;


    @BeforeAll
    public static void configure() throws Exception {
        String gitUrl = "https://github.com/apache/bookkeeper";
        String pathname = "repository/bookkeeper/.git/";
        String jiraProjectManager = "BOOKKEEPER";
        String jiraUrl = "https://issues.apache.org";
        String file = "README.md";
        RepositoryMinerTest.repositoryMiner = new RepositoryMiner(gitUrl, pathname, jiraProjectManager, jiraUrl);
        RepositoryMinerTest.file = file;
    }

}