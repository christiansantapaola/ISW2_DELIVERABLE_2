package it.uniroma2.santapaola.christian.GitSubSystem;

public class DiffStat {
    private String oldPath;
    private String newPath;
    private DiffType diffType;
    private String oldBlobId;
    private String newBlobId;
    private String oldCommitId;
    private String newCommitId;
    private long oldSize;
    private long newSize;
    private long locAdded;
    private long locDeleted;

    public DiffStat(String oldPath, String newPath, DiffType diffType, String oldCommitId, String newCommitId, long locAdded, long locDeleted) {
        this.oldPath = oldPath;
        this.newPath = newPath;
        this.diffType = diffType;
        this.oldCommitId = oldCommitId;
        this.newCommitId = newCommitId;
        this.locAdded = locAdded;
        this.locDeleted = locDeleted;
    }


    public String getOldPath() {
        return oldPath;
    }

    public String getNewPath() {
        return newPath;
    }

    public DiffType getDiffType() {
        return diffType;
    }

    public String getOldCommitId() {
        return oldCommitId;
    }

    public String getNewCommitId() {
        return newCommitId;
    }

    public long getSignedLocChanged() {
        return (locAdded - locDeleted);
    }

    public long getLocChanged() {
        long loc = locAdded - locDeleted;
        if (loc >= 0) {
            return loc;
        } else {
            return -loc;
        }
    }

    public long getLocAdded() {
        return locAdded;
    }

    public long getLocDeleted() {
        return locDeleted;
    }

    public long getAddedLoc() { return locAdded - locDeleted;}

    public long getChurn() {return locAdded + locDeleted;}

    @Override
    public String toString() {
        return "DiffStat{" +
                "oldPath='" + oldPath + '\'' +
                ", newPath='" + newPath + '\'' +
                ", diffType=" + diffType +
                ", oldCommitId='" + oldCommitId + '\'' +
                ", newCommitId='" + newCommitId + '\'' +
                ", locAdded=" + locAdded +
                ", locDeleted=" + locDeleted +
                '}';
    }

}
