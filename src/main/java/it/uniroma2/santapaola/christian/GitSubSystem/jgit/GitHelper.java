package it.uniroma2.santapaola.christian.GitSubSystem.jgit;

import it.uniroma2.santapaola.christian.GitSubSystem.DiffType;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.*;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.TreeWalk;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

class GitHelper {

    private static final String gitNullId = "0000000000000000000000000000000000000000";

    public static String getGitNullId() {
        return gitNullId;
    }

    public static boolean isNullId(String id) {
        return id.equals(gitNullId);
    }

    public static RevTree getTree(Repository repository, String objectId) throws IOException {
        ObjectId lastCommitId = repository.resolve(objectId);
        // a RevWalk allows to walk over commits based on some filtering
        try (RevWalk revWalk = new RevWalk(repository)) {
            RevCommit commit = revWalk.parseCommit(lastCommitId);
            // and using commit's tree find the path
            RevTree tree = commit.getTree();
            return tree;
        }
    }

    public static String getFileMode(FileMode fileMode) {
        if (fileMode.equals(FileMode.EXECUTABLE_FILE)) {
            return "Executable File";
        } else if (fileMode.equals(FileMode.REGULAR_FILE)) {
            return "Normal File";
        } else if (fileMode.equals(FileMode.TREE)) {
            return "Directory";
        } else if (fileMode.equals(FileMode.SYMLINK)) {
            return "Symlink";
        } else {
            // there are a few others, see FileMode javadoc for details
            throw new IllegalArgumentException("Unknown type of file encountered: " + fileMode);
        }
    }

    static List<DiffEntry> getListDiffEntry(Repository repository, Git git, String oldCommit, String newCommit) throws GitAPIException, IOException {
        final List<DiffEntry> diffs = git.diff()
                .setOldTree(prepareTreeParser(repository, oldCommit))
                .setNewTree(prepareTreeParser(repository, newCommit))
                .call();
        return diffs;
    }

    static AbstractTreeIterator prepareTreeParser(Repository repository, String objectId) throws IOException {
        // from the commit we can build the tree which allows us to construct the TreeParser
        //noinspection Duplicates
        try (RevWalk walk = new RevWalk(repository)) {
            RevCommit commit = walk.parseCommit(repository.resolve(objectId));
            RevTree tree = walk.parseTree(commit.getTree().getId());

            CanonicalTreeParser treeParser = new CanonicalTreeParser();
            try (ObjectReader reader = repository.newObjectReader()) {
                treeParser.reset(reader, tree.getId());
            }

            walk.dispose();

            return treeParser;
        }
    }

    static public DiffType getDiffType(DiffEntry.ChangeType changeType) {
        switch (changeType) {
            case ADD -> {
                return DiffType.ADD;
            }
            case MODIFY -> {
                return DiffType.MODIFY;
            }
            case DELETE -> {
                return DiffType.DELETE;
            }
            case COPY -> {
                return DiffType.COPY;
            }
            case RENAME -> {
                return DiffType.RENAME;
            }
            default -> {
                throw new IllegalArgumentException("unknown DiffEntry.ChangeType");
            }
        }
    }


    static public List<String> getFiles(Repository repository, RevCommit commit) throws IOException {
        ObjectId treeId = commit.getTree().getId();
        List<String> files = new LinkedList<>();
        TreeWalk treeWalk = new TreeWalk(repository);
        treeWalk.reset(treeId);
        treeWalk.setRecursive(false);
        while (treeWalk.next()) {
            if (treeWalk.isSubtree()) {
                treeWalk.enterSubtree();
            } else {
                String path = treeWalk.getPathString();
                //ObjectId objectId = treeWalk.getObjectId(0);
                //ObjectLoader loader = repository.open(objectId);
                files.add(path);
            }
        }
        return files;
    }

    static public List<String> getModifiedFiles(Repository repository, RevCommit commit) throws IOException {
        ObjectId treeId = commit.getTree().getId();
        List<String> files = new LinkedList<>();
        TreeWalk treeWalk = new TreeWalk(repository);
        treeWalk.reset(treeId);
        treeWalk.setRecursive(false);
        while (treeWalk.next()) {
            if (treeWalk.isSubtree()) {
                treeWalk.enterSubtree();
            } else {
                String path = treeWalk.getPathString();
                //ObjectId objectId = treeWalk.getObjectId(0);
                //ObjectLoader loader = repository.open(objectId);
                files.add(path);
            }
        }
        return files;
    }

    public static ObjectId getObjectId(Repository repository, String id) throws IOException {
        Ref ref = repository.getRefDatabase().findRef(id);
        final Ref repoPeeled = repository.getRefDatabase().peel(ref);
        if(repoPeeled.getPeeledObjectId() != null) {
            return repoPeeled.getPeeledObjectId();
        }
        return ref.getObjectId();
    }


}
