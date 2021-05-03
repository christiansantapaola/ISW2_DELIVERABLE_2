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

    public DiffStat(String oldPath, String newPath, DiffType diffType, String oldBlobId, String newBlobId, String oldCommitId, String newCommitId, long oldSize, long newSize, long locAdded, long locDeleted) {
        this.oldPath = oldPath;
        this.newPath = newPath;
        this.diffType = diffType;
        this.oldBlobId = oldBlobId;
        this.newBlobId = newBlobId;
        this.oldCommitId = oldCommitId;
        this.newCommitId = newCommitId;
        this.oldSize = oldSize;
        this.newSize = newSize;
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

    public long getOldSize() {
        return oldSize;
    }

    public long getNewSize() {
        return newSize;
    }

    public String getOldBlobId() {
        return oldBlobId;
    }

    public String getNewBlobId() {
        return newBlobId;
    }

    public String getOldCommitId() {
        return oldCommitId;
    }

    public String getNewCommitId() {
        return newCommitId;
    }

    public long getSignedLocChanged() {
        return newSize - oldSize;
    }

    public long getLocChanged() {
        long loc = newSize - oldSize;
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

    @Override
    public String toString() {
        return "DiffStat{" +
                "oldPath='" + oldPath + '\'' +
                ", newPath='" + newPath + '\'' +
                ", diffType=" + diffType +
                ", oldBlobId='" + oldBlobId + '\'' +
                ", newBlobId='" + newBlobId + '\'' +
                ", oldCommitId='" + oldCommitId + '\'' +
                ", newCommitId='" + newCommitId + '\'' +
                ", oldSize=" + oldSize +
                ", newSize=" + newSize +
                ", locAdded=" + locAdded +
                ", locDeleted=" + locDeleted +
                '}';
    }

}
