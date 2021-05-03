package it.uniroma2.santapaola.christian.Mining;

import it.uniroma2.santapaola.christian.GitSubSystem.Commit;
import it.uniroma2.santapaola.christian.GitSubSystem.DiffStat;
import it.uniroma2.santapaola.christian.GitSubSystem.DiffType;
import it.uniroma2.santapaola.christian.GitSubSystem.Exception.GitHandlerException;
import it.uniroma2.santapaola.christian.GitSubSystem.GitHandler;
import it.uniroma2.santapaola.christian.JiraSubSystem.Release;
import it.uniroma2.santapaola.christian.Mining.Exception.NoReleaseFoundException;

import java.io.IOException;
import java.util.*;

public class ProjectState {
    private HashMap<String, ClassState> state;
    private GitHandler git;
    private Timeline projectTimeline;
    private String projectName;
    private int version;
    private long noRevisions;
    private long changedFileSet;
    private long maxChangedFileSet;
    private double avgChangedFileSet;
    private long noReleaseToProcess;

    public ProjectState(String projectName, GitHandler git, Timeline projectTimeline) throws NoReleaseFoundException, GitHandlerException, IOException {
        state = new HashMap<String, ClassState>();
        this.projectName = projectName;
        this.git = git;
        this.projectTimeline = projectTimeline;
        this.version = 0;
        this.noRevisions = 0;
        this.changedFileSet = 0;
        this.maxChangedFileSet = 0;
        this.avgChangedFileSet = 0;
        noReleaseToProcess = projectTimeline.getNoRelease() / 2;
    }

    public Set<String> keySet() {
        return state.keySet();
    }

    public ClassState getState(String path) {
        return state.get(path);
    }

    public boolean next() throws GitHandlerException, IOException {
            for (String file : state.keySet()) {
                state.get(file).nextVersion();
            }
            changedFileSet = 0;
            maxChangedFileSet = 0;
            avgChangedFileSet = 0;
            noRevisions = 0;
            Optional<Release> curr = projectTimeline.getRelease(version);
            Optional<Release> next = projectTimeline.getRelease(version + 1);
            if (next.isEmpty()) return false;
            Optional<String> currGitTag;
            if (curr.isPresent()) {
                currGitTag = MinerHelper.getTagFromReleaseName(git, curr.get().getName());
            } else {
                currGitTag = Optional.empty();
            }
            Optional<String> nextGitTag;
            if (next.isPresent()) {
                nextGitTag = MinerHelper.getTagFromReleaseName(git, next.get().getName());
            } else {
                nextGitTag = Optional.empty();
            }
            List<Commit> revisions = git.log(currGitTag, nextGitTag, false);
            for (int i = 0; i < revisions.size() - 1; i++) {
                List<DiffStat> diffs = git.diff(revisions.get(i), revisions.get(i + 1));
                for (DiffStat diffStat : diffs) {
                    updateClassState(diffStat, revisions.get(i + 1).getAuthor());
                }
            }

            noRevisions = 0;
            for (Commit revision : revisions) {
                changedFileSet += revision.getModifiedFileSize();
                maxChangedFileSet = Math.max(maxChangedFileSet, revision.getModifiedFileSize());
                avgChangedFileSet = ((avgChangedFileSet * noRevisions) + ((double) revision.getModifiedFileSize())) / ((double) (noRevisions + 1));
                noRevisions++;
            }

            for (String file : state.keySet()) {
                boolean buggy = projectTimeline.isBuggy(file, next.get());
                long noFix = projectTimeline.getNoBugFixed(file, curr, next);
                state.get(file).setBuggy(buggy);
                state.get(file).setNoFix(noFix);
            }
            version++;
            return true;
    }

    private void updateClassState(DiffStat diffStat, String author) {
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
        classState.updateChurn(diffStat.getLocAdded(), diffStat.getLocDeleted());
        classState.addAuthor(author);
        classState.updateRevision();
        classState.setLoc(diffStat.getNewSize());

    }

    private void resetClassState(String classPath) {
        ClassState classState = state.get(classPath);
        if (classState == null) return;
        classState.resetAuthor();
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
