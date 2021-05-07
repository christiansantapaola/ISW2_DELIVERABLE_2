package it.uniroma2.santapaola.christian.Mining;

import it.uniroma2.santapaola.christian.GitSubSystem.*;
import it.uniroma2.santapaola.christian.GitSubSystem.Exception.GitHandlerException;
import it.uniroma2.santapaola.christian.GitSubSystem.jgit.GitHandler;
import it.uniroma2.santapaola.christian.JiraSubSystem.Release;
import it.uniroma2.santapaola.christian.Mining.Exception.NoReleaseFoundException;

import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.regex.Pattern;

public class ProjectState {
    private HashMap<String, ClassState> state;
    private Git git;
    private Timeline projectTimeline;
    private String projectName;
    private int version;
    private long noRevisions;
    private long changedFileSet;
    private long maxChangedFileSet;
    private double avgChangedFileSet;
    private long noReleaseToProcess;
    private Version curr;
    private Optional<Version> next;
    private static Pattern isJavaClass = Pattern.compile("^.*\\.java$");

    public ProjectState(String projectName, Git git, Timeline projectTimeline) throws GitHandlerException {
        state = new HashMap<String, ClassState>();
        this.projectName = projectName;
        this.git = git;
        this.projectTimeline = projectTimeline;
        this.version = 0;
        this.noRevisions = 0;
        this.changedFileSet = 0;
        this.maxChangedFileSet = 0;
        this.avgChangedFileSet = 0;
        noReleaseToProcess = projectTimeline.getTimeline().getNoVersion() / 2;
        curr = projectTimeline.getTimeline().getFirst();
        next = projectTimeline.getTimeline().getNext(curr);
        List<DiffStat> diffs = git.diff(curr.getTag().getId(), next.get().getTag().getId());
        for (DiffStat diff : diffs) {
            if (!isJavaClass(diff.getOldPath())) continue;
            updateClassState(diff, "", 0);
        }
        curr = next.get();
        next = projectTimeline.getTimeline().getNext(curr);
    }

    public static boolean isJavaClass(String path) {
        return isJavaClass.matcher(path).matches();
    }

    public Set<String> keySet() {
        return state.keySet();
    }

    public ClassState getState(String path) {
        return state.get(path);
    }

    public void printTag() {
        Optional<Version> curr = Optional.ofNullable(projectTimeline.getTimeline().getFirst());
        while (curr.isPresent()) {
            System.out.println(curr.get().getTag().getName());
            curr = projectTimeline.getTimeline().getNext(curr.get());
        }
    }

    public boolean next() throws GitHandlerException {
        if (next.isEmpty()) return false;
        if (next.get() == projectTimeline.getTimeline().getLast()) return false;
        resetState();
        version++;
        List<Commit> commits = git.log(Optional.of(curr.getTag().getId()), Optional.of(next.get().getTag().getId()));
        noRevisions = commits.size();
        commits.sort(Commit::compareTo);
        System.out.println("version: " + version + ", " + "noRevision: " + noRevisions);
        System.out.println("current Version: " + curr.getTag().getName() + ", " + curr.getRelease().getName() + ", " + curr.getRelease().getReleaseDate());
        System.out.println("next Version: " + next.get().getTag().getName() + ", " + next.get().getRelease().getName() + ", " + next.get().getRelease().getReleaseDate());
        for (int i = 0; i < commits.size() - 1; i++) {
            List<DiffStat> diffs = git.diff(commits.get(i), commits.get(i+1));
            for (DiffStat diff : diffs) {
                if (!isJavaClass(diff.getOldPath())) continue;
                updateClassState(diff, commits.get(i+1).getAuthor(), diffs.stream().filter(df -> isJavaClass(df.getOldPath())).count());
            }
        }
        Set<String> buggySet = projectTimeline.getBuggyClass(next.get());
        System.out.println("buggy ratio: " +  buggySet.size() + " / " + state.keySet().size());
        for (String file : state.keySet()) {
            boolean buggy = buggySet.contains(file);
            long noFix = projectTimeline.getNoBugFixed(file, curr, next.get());
            state.get(file).setBuggy(buggy);
            state.get(file).setNoFix(noFix);
        }
        updateLoc(commits.get(commits.size() - 1));
        clean();
        curr = next.get();
        next = projectTimeline.getTimeline().getNext(curr);
        // skip bad version.
        while (next.isPresent()) {
            if (next.get().getRelease().getName().compareTo(curr.getRelease().getName()) < 0) {
                next = projectTimeline.getTimeline().getNext(next.get());
            } else {
                break;
            }
        }
        return true;
    }

    private void updateClassState(DiffStat diffStat, String author, long noChangedFile) {
        ClassState classState = state.get(diffStat.getOldPath());
        if (classState == null) {
            classState = new ClassState(projectName, diffStat.getNewPath());
            state.put(diffStat.getNewPath(), classState);
        }
        if (diffStat.getDiffType() == DiffType.COPY || diffStat.getDiffType() == DiffType.RENAME) {
            state.remove(diffStat.getOldPath());
            state.put(diffStat.getNewPath(), classState);
            return;
        }
        classState.updateLoc(diffStat.getLocAdded(), diffStat.getLocDeleted());
        classState.updateChgFileSet(noChangedFile);
        classState.addAuthor(author);
        classState.updateRevision();
    }

    public void resetState() {
        for (String file : state.keySet()) {
            ClassState classState = state.get(file);
            classState.reset();
        }
    }

    public void updateLoc(Commit commit) throws GitHandlerException {
        List<DiffStat> diffs = git.diff(GitConstants.EMPTY_TREE_ID, commit.getName());
        long skipped = 0;
        for (DiffStat diff : diffs) {
            ClassState classState = state.get(diff.getNewPath());
            if (classState == null) {
                skipped++;
                continue;
            }
            classState.setLoc(diff.getAddedLoc());
        }
    }

    void clean() {
        List<String> toDelete = new ArrayList<>();
        long noClean = 0;
        for (String key : state.keySet()) {
            if (state.get(key).getLoc() <= 0) {
                noClean++;
                toDelete.add(key);
            }
        }
        for (String key : toDelete) {
            state.remove(key);
        }

    }

    public int getVersion() {
        return version;
    }

    public long getNoRevisions() {
        return noRevisions;
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

    public Set<ClassState> getState() {
        Set<ClassState> classStates = new HashSet<>();
        for (String file : state.keySet()) {
            classStates.add(state.get(file));
        }
        return classStates;
    }

    public long getNoReleaseToProcess() {
        return noReleaseToProcess;
    }
}
