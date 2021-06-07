package it.uniroma2.santapaola.christian.mining;

import it.uniroma2.santapaola.christian.git.*;
import it.uniroma2.santapaola.christian.git.exception.GitHandlerException;

import java.util.*;
import java.util.regex.Pattern;

public class ProjectState {
    private HashMap<String, ClassState> state;
    private Git git;
    private Timeline projectTimeline;
    private String projectName;
    private int version;
    private long noReleaseToProcess;
    private Version curr;
    private Optional<Version> next;
    private static final Pattern isJavaClass = Pattern.compile("^.*\\.java$");
    private static final CommitComparator cc = new CommitComparator();

    public ProjectState(String projectName, Git git, Timeline projectTimeline) throws GitHandlerException {
        state = new HashMap<>();
        this.projectName = projectName;
        this.git = git;
        this.projectTimeline = projectTimeline;
        this.version = 0;
        noReleaseToProcess = projectTimeline.getVersionTimeline().getNoVersion() / 2;
        curr = projectTimeline.getVersionTimeline().getFirst();
        next = projectTimeline.getVersionTimeline().getNext(curr);
        List<DiffStat> diffs = git.diff(curr.getTag().getId(), next.get().getTag().getId());
        for (DiffStat diff : diffs) {
            if (!isJavaClass(diff.getOldPath())) continue;
            updateClassState(diff, "", 0);
        }
        curr = next.get();
        next = projectTimeline.getVersionTimeline().getNext(curr);
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

    public boolean next() throws GitHandlerException {
        if (next.isEmpty()) return false;
        if (next.get() == projectTimeline.getVersionTimeline().getLast()) return false;
        resetState();
        version++;
        List<Commit> commits = git.log(Optional.of(curr.getTag().getId()), Optional.of(next.get().getTag().getId()));
        commits.sort(cc);
        for (var i = 0; i < commits.size() - 1; i++) {
            List<DiffStat> diffs = git.diff(commits.get(i), commits.get(i+1));
            for (DiffStat diff : diffs) {
                if (!isJavaClass(diff.getOldPath())) continue;
                updateClassState(diff, commits.get(i+1).getAuthor(), diffs.stream().filter(df -> isJavaClass(df.getOldPath())).count());
            }
        }
        Set<String> buggySet = projectTimeline.getBuggyClass(next.get());
        for (var entry : state.entrySet()) {
            boolean buggy = buggySet.contains(entry.getKey());
            long noFix = projectTimeline.getNoBugFixed(entry.getKey(), curr, next.get());
            entry.getValue().setBuggy(buggy);
            entry.getValue().setNoFix(noFix);
        }
        updateLoc(commits.get(commits.size() - 1));
        clean();
        curr = next.get();
        next = projectTimeline.getVersionTimeline().getNext(curr);
        // skip bad version.
        while (next.isPresent()) {
            if (next.get().getRelease().getName().compareTo(curr.getRelease().getName()) < 0) {
                next = projectTimeline.getVersionTimeline().getNext(next.get());
            } else {
                break;
            }
        }
        return true;
    }

    private void updateClassState(DiffStat diffStat, String author, long noChangedFile) {
        var classState = state.get(diffStat.getOldPath());
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
        for (var entry : state.entrySet()) {
            entry.getValue().reset();
        }
    }

    public void updateLoc(Commit commit) throws GitHandlerException {
        List<DiffStat> diffs = git.diff(GitConstants.EMPTY_TREE_ID, commit.getName());
        for (DiffStat diff : diffs) {
            var classState = state.get(diff.getNewPath());
            if (classState == null) {
                continue;
            }
            classState.setLoc(diff.getAddedLoc());
        }
    }

    void clean() {
        List<String> toDelete = new ArrayList<>();
        for (var entry : state.entrySet()) {
            if (entry.getValue().getLoc() <= 0) {
                toDelete.add(entry.getKey());
            }
        }
        for (String key : toDelete) {
            state.remove(key);
        }

    }

    public int getVersion() {
        return version;
    }

    public long getNoReleaseToProcess() {
        return noReleaseToProcess;
    }
}
