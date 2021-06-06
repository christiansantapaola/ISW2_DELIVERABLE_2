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
        if (args.length != 2) {
            System.err.println("USAGE: command <output-folder> <repository-folder>");
            return;
        }
        String output = args[0];
        String repository = args[1];
        OutputDirectory outputDirectory = new OutputDirectory(output, repository);
        ProjectData bookkeeper = new ProjectData("bookkeeper", "BOOKKEEPER", "https://issues.apache.org", "https://github.com/apache/bookkeeper", "^(refs\\/tags\\/)(.*)(?<name>\\d+.\\d+.\\d+)$");
        ProjectData openjpa = new ProjectData("openjpa", "OPENJPA", "https://issues.apache.org", "https://github.com/apache/openjpa", "^(refs\\/tags\\/)(?<name>\\d+.\\d+.\\d+)$");

        try {
            doProjectAnalysis(bookkeeper, outputDirectory);
            System.gc();
            doProjectAnalysis(openjpa, outputDirectory);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static RepositoryMiner buildRepositoryMiner(ProjectData projectData, OutputDirectory outputDirectory) throws IOException, GitHandlerException {
        return new RepositoryMiner(
                outputDirectory.getRepository() + projectData.getProjectName(),
                projectData.getGitUrl(),
                projectData.getJiraProjectName(),
                projectData.getJiraUrl(),
                projectData.getTagPattern());
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
            long countBuggy = 0;
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
                            Long.toString(classState.getChangedFileSet()),
                            Long.toString(classState.getMaxChangedFileSet()),
                            Double.toString(classState .getAvgChangedFileSet()),
                            Long.toString(classState.getAge()),
                            Double.toString(classState.getWeightedAge()),
                            getBuggy(classState.isBuggy())
                    };
                    if (classState.isBuggy()) {
                      countBuggy++;
                    }
                    csvWriter.writeLine(row);
                }
            }
            long t2 = System.nanoTime();
            //System.out.println("iteration: " + i);
            //System.out.println(countBuggy + " / " + projectState.keySet().size());
            //System.out.println("time: " + ((t2 - t1)) + "");
            //System.out.flush();
            i++;
            csvWriter.flush();
        }
    }
}
