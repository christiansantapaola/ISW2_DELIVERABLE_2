package Mining;

import GitSubSystem.DiffStat;
import JiraSubSystem.Release;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;

import java.io.File;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;

class RepositoryMinerTest {

     public static RepositoryMiner repositoryMiner;
     public static String file;


    @BeforeAll
    public static void configure() throws Exception {
        String pathname = "repository/bookkeeper/.git/";
        String jiraProjectManager = "BOOKKEEPER";
        String jiraUrl = "https://issues.apache.org";
        String file = "README.md";
        RepositoryMinerTest.repositoryMiner = new RepositoryMiner(pathname, jiraProjectManager, jiraUrl);
        RepositoryMinerTest.file = file;
    }

}