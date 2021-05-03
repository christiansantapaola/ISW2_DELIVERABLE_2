package Mining;

import GitSubSystem.DiffStat;

import java.util.*;

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
    private long age;
    private boolean buggy;

    public ClassState(String projectName, String className) {
        this.projectName = projectName;
        this.className = className;
        authors = new HashSet<>();
        addedLocHistory = new ArrayList<Long>();
        ageHistory = new ArrayList<Long>();
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
        this.touchedLoc = addedLoc + deletedLoc;
        this.addedLoc += addedLoc;
        maxAddedLoc = Math.max(maxAddedLoc, addedLoc);
        avgAddedLoc = ((avgAddedLoc * noRevision) + addedLoc) / (noRevision + 1);
        addedLocHistory.add(this.addedLoc);
        ageHistory.add(age * this.addedLoc);

    }

    public void updateChurn(long addedLoc, long deletedLoc) {
        long tmpChurn = addedLoc - deletedLoc;
        churn += tmpChurn;
        maxChurn = Math.max(maxChurn, churn);
        avgChurn = ((avgChurn * noRevision) + tmpChurn) / (noRevision + 1);
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

    public void nextVersion() {
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
        double addedLoc = 0.0;
        for (int i = 0; i < addedLocHistory.size(); i++) {
            addedLoc += addedLocHistory.get(i);
        }
        double weightedAge = ageAddedLoc / addedLoc;
        if (Double.isNaN(weightedAge)) {
            return 0.0;
        } else {
            return weightedAge;
        }
    }

    public long getTouchedLoc() {
        return touchedLoc;
    }
}
