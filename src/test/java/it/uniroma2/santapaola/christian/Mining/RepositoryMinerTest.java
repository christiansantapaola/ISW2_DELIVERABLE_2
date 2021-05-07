package it.uniroma2.santapaola.christian.Mining;

import it.uniroma2.santapaola.christian.Main;
import it.uniroma2.santapaola.christian.OutputDirectory;
import it.uniroma2.santapaola.christian.ProjectData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class RepositoryMinerTest {

     public static RepositoryMiner repositoryMiner;
     public static String file;
     public static ProjectState state;
     public static ProjectData data;
     public static OutputDirectory outputDirectory = new OutputDirectory("output/", "repository/");
     public static ProjectData bookkeeper = new ProjectData("bookkeeper", "BOOKKEEPER", "https://issues.apache.org", "https://github.com/apache/bookkeeper", "^(refs\\/tags\\/)(.*)(?<name>\\d+.\\d+.\\d+)$");
     public static ProjectData openjpa = new ProjectData("openjpa", "OPENJPA", "https://issues.apache.org", "https://github.com/apache/openjpa", "^(refs\\/tags\\/)(?<name>\\d+.\\d+.\\d+)$");



    @BeforeAll
    public static void configure() throws Exception {
        RepositoryMinerTest.repositoryMiner = Main.buildRepositoryMiner(openjpa, outputDirectory);
        RepositoryMinerTest.file = file;
        state = repositoryMiner.newProjectState();
    }

    @Test
    public void Tag() {
        state.printTag();
    }


}