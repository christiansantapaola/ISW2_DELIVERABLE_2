package it.uniroma2.santapaola.christian.mining;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ClassState {
    private String projectName;
    private String className;
    private Set<String> authors;
    private int version;
    private List<Long> addedLocHistory;
    private List<Long> ageHistory;
    private long loc;
    private long noRevision;
    private long noFix;
    private long touchedLoc;
    private long addedLoc;
    private long maxAddedLoc;
    private double avgAddedLoc;
    private long churn;
    private long maxChurn;
    private double avgChurn;
    private long changedFileSet;
    private long maxChangedFileSet;
    private double avgChangedFileSet;
    private long age;
    private boolean buggy;

    public ClassState(String projectName, String className) {
        this.projectName = projectName;
        this.className = className;
        authors = new HashSet<>();
        addedLocHistory = new ArrayList<>();
        ageHistory = new ArrayList<>();
        age = 1;
    }


    public String getProjectName() {
        return projectName;
    }

    public String getClassName() {
        return className;
    }

    public int getVersion() {
        return version;
    }


    public long getNoRevision() {
        return noRevision;
    }

    public long getNoFix() {
        return noFix;
    }

    public long getNoAuth() {
        return authors.size();
    }

    public long getAddedLoc() {
        return addedLoc;
    }

    public long getMaxAddedLoc() {
        return maxAddedLoc;
    }

    public double getAvgAddedLoc() {
        return avgAddedLoc;
    }

    public long getChurn() {
        return churn;
    }

    public long getMaxChurn() {
        return maxChurn;
    }

    public double getAvgChurn() {
        return avgChurn;
    }

    public long getAge() {
        return age;
    }

    public boolean isBuggy() {
        return buggy;
    }

    public void updateLoc(long addedLoc, long deletedLoc) {
        this.touchedLoc += addedLoc + deletedLoc;
        this.addedLoc += addedLoc;
        maxAddedLoc = Math.max(maxAddedLoc, addedLoc);
        avgAddedLoc = ((avgAddedLoc * noRevision) + addedLoc) / (noRevision + 1);
        this.churn += addedLoc - deletedLoc;
        maxChurn = Math.max(maxChurn, churn);
        avgChurn = ((avgChurn * noRevision) + churn) / (noRevision + 1);
        addedLocHistory.add(this.addedLoc);
        ageHistory.add(age * this.addedLoc);
    }

    public void updateChgFileSet(long noChangedFile) {
        changedFileSet += noChangedFile;
        maxChangedFileSet = Math.max(maxChangedFileSet, noChangedFile);
        avgChangedFileSet = ((avgChangedFileSet * noRevision) + noChangedFile) / (noRevision + 1);
    }

    public void setBuggy(boolean buggy) {
        this.buggy = buggy;
    }

    public void updateRevision() {
        noRevision++;
    }

    public void addAuthor(String author) {
        authors.add(author);
    }

    public void resetAuthor() {
        authors.clear();
    }

    public void reset() {
        noRevision = 0;
        touchedLoc = 0;
        addedLoc = 0;
        maxAddedLoc = 0;
        avgAddedLoc = 0;
        churn = 0;
        maxChurn = 0;
        avgChurn = 0;
        resetAuthor();
        buggy = false;
        noFix = 0;
        changedFileSet = 0;
        maxChangedFileSet = 0;
        avgChangedFileSet = 0;
        age++;
        version++;
    }

    public void setNoFix(long noFix) {
        this.noFix = noFix;
    }

    public void setLoc(long loc) {
        this.loc = loc;
    }

    public long getLoc() {
        return loc;
    }


    public double getWeightedAge() {
        double ageAddedLoc = 0.0;
        for (int i = 0; i < ageHistory.size(); i++) {
            ageAddedLoc += (double) ageHistory.get(i);
        }
        double totalAddedLoc = 0.0;
        for (int i = 0; i < addedLocHistory.size(); i++) {
            totalAddedLoc += addedLocHistory.get(i);
        }
        if (totalAddedLoc == 0.0) return 0.0;
        double weightedAge = ageAddedLoc / totalAddedLoc;
        if (Double.isNaN(weightedAge)) {
            return 0.0;
        } else {
            return weightedAge;
        }
    }

    public long getTouchedLoc() {
        return touchedLoc;
    }

    @Override
    public String toString() {
        return "ClassState{" +
                "projectName='" + projectName + '\'' +
                ", className='" + className + '\'' +
                ", authors=" + authors +
                ", version=" + version +
                ", addedLocHistory=" + addedLocHistory +
                ", ageHistory=" + ageHistory +
                ", loc=" + loc +
                ", noRevision=" + noRevision +
                ", noFix=" + noFix +
                ", touchedLoc=" + touchedLoc +
                ", addedLoc=" + addedLoc +
                ", maxAddedLoc=" + maxAddedLoc +
                ", avgAddedLoc=" + avgAddedLoc +
                ", churn=" + churn +
                ", maxChurn=" + maxChurn +
                ", avgChurn=" + avgChurn +
                ", age=" + age +
                ", buggy=" + buggy +
                '}';
    }

    public long getChangedFileSet() {
        return changedFileSet;
    }

    public long getMaxChangedFileSet() {
        return maxChangedFileSet;
    }

    public double getAvgChangedFileSet() {
        return avgChangedFileSet;
    }
}
