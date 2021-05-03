package Mining;

import GitSubSystem.Commit;
import GitSubSystem.DiffStat;
import GitSubSystem.DiffType;
import JiraSubSystem.Release;

import java.util.*;

public class FileHistory {
    private List<Commit> commits;
    private List<DiffStat> diffs;
    private String path;

    public FileHistory(List<Commit> commits, List<DiffStat> diffs, String path) {
        this.commits = commits;
        this.diffs = diffs;
        this.path = path;
    }

    public List<Commit> getCommits() {
        return commits;
    }

    public List<DiffStat> getDiffs() {
        return diffs;
    }

    public String getPath() {
        return path;
    }

    private boolean checkName(String name, DiffStat diffStat) {
        return diffStat.getOldPath().equals(name) || diffStat.getNewPath().equals(name);
    }

    public boolean isFileName(String name) {
        for (DiffStat diffStat : diffs) {
            if (diffStat.getOldPath().equals(name) || diffStat.getNewPath().equals(name)) {
                return true;
            }
        }
        return false;
    }

    public boolean isFileName(String name, Optional<Release> from, Optional<Release> to) {
        List<DiffStat> diffs;
        if (from.isEmpty() && to.isEmpty()) {
            diffs = this.diffs;
        } else if (from.isPresent() && to.isEmpty()) {
            diffs = getDiffStatAfter(from.get());
        } else if (from.isEmpty() && to.isPresent()) {
            diffs = getDiffStatUntil(to.get());
        } else {
            diffs = getDiffStatBetween(from, to);
        }
        for (DiffStat diffStat : diffs) {
            if (diffStat.getOldPath().equals(name) || diffStat.getNewPath().equals(name)) {
                return true;
            }
        }
        return false;
    }

    private List<Commit> getCommitUntil(Release release) {
        List<Commit> filteredCommits = new ArrayList<>();
        for (Commit commit : commits) {
            if (commit.getCommitterCommitTime().compareTo(release.getReleaseDate()) <= 0) {
                filteredCommits.add(commit);
            }
        }
        return filteredCommits;
    }

    private List<DiffStat> getDiffStatUntil(Release release) {
        List<Commit> filteredCommits = getCommitUntil(release);
        return getDiffStats(filteredCommits);
    }

    private List<Commit> getCommitAfter(Release release) {
        List<Commit> filteredCommits = new ArrayList<>();
        for (Commit commit : commits) {
            if (commit.getCommitterCommitTime().compareTo(release.getReleaseDate()) >= 0) {
                filteredCommits.add(commit);
            }
        }
        return filteredCommits;
    }

    public List<DiffStat> getDiffStatAfter(Release release) {
        List<Commit> filteredCommits = getCommitAfter(release);
        return getDiffStats(filteredCommits);
    }

    private List<DiffStat> getDiffStats(List<Commit> filteredCommits) {
        HashMap<String, Commit> commitHashMap = new HashMap<>();
        for (Commit commit : filteredCommits) {
            commitHashMap.put(commit.getName(), commit);
        }
        List<DiffStat> filteredDiffStat = new ArrayList<>();
        for (DiffStat diffStat : getDiffs()) {
            if (commitHashMap.containsKey(diffStat.getOldCommitId()) && commitHashMap.containsKey(diffStat.getNewCommitId())) {
                filteredDiffStat.add(diffStat);
            }
        }
        return filteredDiffStat;
    }


    public List<Commit> getCommitBetween(Optional<Release> from, Optional<Release> to) {
        if (from.isEmpty() && to.isEmpty()) {
            return commits;
        } else if (from.isEmpty() && to.isPresent()) {
            return getCommitUntil(to.get());
        } else if (from.isPresent() && to.isEmpty()) {
            return getCommitAfter(from.get());
        } else {
            List<Commit> filteredCommits = new ArrayList<>();
            for (Commit commit : commits) {
                if (commit.getCommitterCommitTime().compareTo(from.get().getReleaseDate()) <= 0
                        && commit.getCommitterCommitTime().compareTo(to.get().getReleaseDate()) >= 0) {
                    filteredCommits.add(commit);
                }
            }
            return filteredCommits;
        }
    }

    public List<DiffStat> getDiffStatBetween(Optional<Release> from, Optional<Release> to) {
        List<Commit> filteredCommits = getCommitBetween(from, to);
        return getDiffStats(filteredCommits);
    }

    public long getLOCTouched(Optional<Release> from, Optional<Release> to) {
        List<DiffStat> diffStats = getDiffStatBetween(from, to);
        long touchedLOC = 0;
        for (DiffStat diffStat : diffStats) {
            if (diffStat.getDiffType() == DiffType.ADD) {
                touchedLOC += diffStat.getLocChanged();
            } else if (diffStat.getDiffType() == DiffType.DELETE) {
                touchedLOC += diffStat.getLocChanged();
            } else if (diffStat.getDiffType() == DiffType.MODIFY) {
                touchedLOC += diffStat.getLocChanged();
            }
        }
        return touchedLOC;
    }

    public long getNoRevision(Optional<Release> from, Optional<Release> to) {
        return getCommitBetween(from, to).size();
    }

    public long getNoAuthors(Optional<Release> from, Optional<Release> to) {
        long noAuth = 0;
        Set<String> authors = new HashSet<>();
        for (Commit commit : getCommitBetween(from, to)) {
            if (authors.contains(commit.getAuthor())) {
                authors.add(commit.getAuthor());
                noAuth++;
            }
        }
        return noAuth;
    }

    public long getLOCAdded(Optional<Release> from, Optional<Release> to) {
        long locAdded = 0;
        for (DiffStat diffStat : getDiffStatBetween(from, to)) {
            if (diffStat.getDiffType() == DiffType.ADD) {
                locAdded += diffStat.getLocChanged();
            }
        }
        return locAdded;
    }

    public long getMaxLocAdded(Optional<Release> from, Optional<Release> to) {
        long maxLocAdded = 0;
        for (DiffStat diffStat : getDiffStatBetween(from, to)) {
            if (diffStat.getDiffType() == DiffType.ADD) {
                if (diffStat.getLocChanged() > maxLocAdded) {
                    maxLocAdded = diffStat.getLocChanged();
                }
            }
        }
        return maxLocAdded;
    }

    public long getChurn(Optional<Release> from, Optional<Release> to) {
        long churn = 0;
        for (DiffStat diffStat : getDiffStatBetween(from, to)) {
            churn += diffStat.getSignedLocChanged();
        }
        return churn;
    }

    public long getMaxChurn(Optional<Release> from, Optional<Release> to) {
        long maxChurn = 0;
        long churn = 0;
        for (DiffStat diffStat : getDiffStatBetween(from, to)) {
            churn += diffStat.getSignedLocChanged();
            if (churn > maxChurn) {
                maxChurn = churn;
            }
        }
        return maxChurn;

    }

    public long getAverageChurn(Optional<Release> from, Optional<Release> to) {
        long churn = getChurn(from, to);
        long noRevision = getDiffStatBetween(from, to).size();
        return churn / noRevision;
    }
}
