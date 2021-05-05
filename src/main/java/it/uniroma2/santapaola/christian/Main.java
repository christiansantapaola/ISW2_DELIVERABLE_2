package it.uniroma2.santapaola.christian;

import it.uniroma2.santapaola.christian.GitSubSystem.Exception.GitHandlerException;
import it.uniroma2.santapaola.christian.Mining.ClassState;
import it.uniroma2.santapaola.christian.Mining.Exception.NoReleaseFoundException;
import it.uniroma2.santapaola.christian.Mining.ProjectState;
import it.uniroma2.santapaola.christian.Mining.RepositoryMiner;
import it.uniroma2.santapaola.christian.utility.CSVWriter;

import java.io.File;
import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        OutputDirectory outputDirectory = new OutputDirectory("output/", "repository/");
        ProjectData bookkeeper = new ProjectData("bookkeeper", "BOOKKEEPER", "https://issues.apache.org", "https://github.com/apache/bookkeeper");
        //ProjectData openjpa = new ProjectData("openjpa", "OPENJPA", "https://issues.apache.org", "https://github.com/apache/openjpa");

        try {
            doProjectAnalysis(bookkeeper, outputDirectory);
            System.gc();
            //doProjectAnalysis(openjpa, outputDirectory);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        } catch (GitHandlerException | NoReleaseFoundException e) {
            e.printStackTrace();
            return;
        }
    }

    private static RepositoryMiner buildRepositoryMiner(ProjectData projectData, OutputDirectory outputDirectory) throws IOException, GitHandlerException {
        return new RepositoryMiner(
                outputDirectory.getRepository() + projectData.getProjectName(),
                projectData.getGitUrl(),
                projectData.getJiraProjectName(),
                projectData.getJiraUrl());
    }

    private static String getBuggy(boolean buggy) {
        if (buggy) {
            return "YES";
        } else {
            return "NO";
        }
    }

    public static void doProjectAnalysis(ProjectData projectData, OutputDirectory outputDirectory) throws IOException, GitHandlerException, NoReleaseFoundException {
        RepositoryMiner miner = buildRepositoryMiner(projectData, outputDirectory);
        String[] fields = {"Version", "File Name", "LOC", "LOC_touched",
                "NR", "NFix", "NAuth",
                "LOC_added", "MAX_LOC_added", "AVG_LOC_ADDED",
                "Churn", "MAX_churn", "AVG_Churn",
                "ChgSetSize", "MAX_ChgSetSize", "AVG_ChgSetSize",
                "AGE", "WeightedAge", "Buggy"};
        CSVWriter csvWriter = new CSVWriter(new File(outputDirectory.getOutput() + projectData.getCSVOutput()), fields);
        csvWriter.writeFieldName();
        ProjectState projectState = miner.newProjectState();
        int i=0;
        while (projectState.next()) {
            long t1 = System.nanoTime();
            for (String file : projectState.keySet()) {
                ClassState classState = projectState.getState(file);
                if (projectState.getVersion() <= projectState.getNoReleaseToProcess() || classState.isBuggy()) {
                    String[] row = {
                            Integer.toString(projectState.getVersion()),
                            classState.getClassName(),
                            Long.toString(classState.getLoc()),
                            Long.toString(classState.getTouchedLoc()),
                            Long.toString(classState.getNoRevision()),
                            Long.toString(classState.getNoFix()),
                            Long.toString(classState.getNoAuth()),
                            Long.toString(classState.getAddedLoc()),
                            Long.toString(classState.getMaxAddedLoc()),
                            Double.toString(classState.getAvgAddedLoc()),
                            Long.toString(classState.getChurn()),
                            Long.toString(classState.getMaxChurn()),
                            Double.toString(classState.getAvgChurn()),
                            Long.toString(projectState.getChangedFileSet()),
                            Long.toString(projectState.getMaxChangedFileSet()),
                            Double.toString(projectState.getAvgChangedFileSet()),
                            Long.toString(classState.getAge()),
                            Double.toString(classState.getWeightedAge()),
                            getBuggy(classState.isBuggy())
                    };
                    csvWriter.writeLine(row);
                    System.gc();
                }
                System.gc();
            }
            long t2 = System.nanoTime();
            System.out.println("iteration: " + i);
            System.out.println("time: " + ((t2 - t1)) + "");
            System.out.flush();
            i++;
            csvWriter.flush();
        }
    }
}
