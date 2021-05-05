package it.uniroma2.santapaola.christian.GitSubSystem.jgit;

import it.uniroma2.santapaola.christian.GitSubSystem.*;
import it.uniroma2.santapaola.christian.GitSubSystem.Exception.GitHandlerException;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.Edit;
import org.eclipse.jgit.lib.*;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.revwalk.filter.MessageRevFilter;
import org.eclipse.jgit.revwalk.filter.RevFilter;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.util.io.DisabledOutputStream;

import java.io.*;
import java.util.*;
import java.util.function.Consumer;

/**
 * GitSubSystem.GitHandler is a class which handle all the git subsystem.
 * All Git related operation should be contained in this class.
 */
public class GitHandler implements it.uniroma2.santapaola.christian.GitSubSystem.Git {
    private Repository repository;
    private String root;
    private Git git;
    private DiffFormatter diffFormatter;

    /**
     * Constructor of GitSubSystem.GitHandler instance from local git repository.
     *
     * @param localRepositoryGit, File pointing to existing Project/.git folder
     * @throws IOException
     */
    public GitHandler(File localRepositoryGit) throws IOException {
        FileRepositoryBuilder repositoryBuilder = new FileRepositoryBuilder();
        repositoryBuilder.setMustExist(true);
        repositoryBuilder.setGitDir(localRepositoryGit);
        repository = repositoryBuilder.build();
        root = localRepositoryGit.getParent();
        git = new Git(repository);
        diffFormatter = new DiffFormatter(DisabledOutputStream.INSTANCE);
        diffFormatter.setRepository(repository);
        diffFormatter.setContext(0);
    }

    /**
     * Constructor of GitSubSystem.GitHandler instance from a remote git repository.
     *
     * @param url,                url of the remote repository.
     * @param newLocalRepository, File object pointing to where to clone the remote repository.
     * @throws IOException
     * @throws GitHandlerException
     */
    public GitHandler(String url, File newLocalRepository) throws IOException, GitHandlerException {
        try {
            git = Git.cloneRepository()
                    .setURI(url)
                    .setDirectory(newLocalRepository)
                    .setCloneAllBranches(true)
                    .call();
            File newGitFolder = new File(newLocalRepository.getAbsolutePath() + "/.git/");
            FileRepositoryBuilder repositoryBuilder = new FileRepositoryBuilder();
            repositoryBuilder.setMustExist(true);
            repositoryBuilder.setGitDir(newGitFolder);
            repository = repositoryBuilder.build();
            root = newLocalRepository.getAbsolutePath();
            diffFormatter = new DiffFormatter(DisabledOutputStream.INSTANCE);
            diffFormatter.setRepository(repository);
            diffFormatter.setContext(0);
        } catch (GitAPIException e) {
            throw new GitHandlerException(e.getMessage(), e.getCause());
        }
    }


    /**
     * This method emulate git-log --grep=pattern
     *
     * @param pattern a String object containing a pattern.
     * @return an Iterable<RevCommit>, the RevCommit contain the information on the commit requested.
     * @throws IOException
     * @throws GitHandlerException
     */
    @Override
    public List<Commit> grep(String pattern)
            throws GitHandlerException {
        try {
            ObjectId head = repository.resolve(Constants.HEAD);
            RevFilter revFilter = MessageRevFilter.create(pattern);
            Iterable<RevCommit> commits = git.log().add(head).setRevFilter(revFilter).call();
            List<Commit> result = new ArrayList<>();
            for (RevCommit commit : commits) {
                Commit tmp = createCommit(commit, false);
                result.add(tmp);
            }
            return result;
        } catch (GitAPIException e) {
            throw new GitHandlerException(e.getMessage(), e.getCause());
        }  catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public List<Commit> grep(String pattern, boolean fastNoDiff)
            throws IOException, GitHandlerException {
        try {
            ObjectId head = repository.resolve(Constants.HEAD);
            RevFilter revFilter = MessageRevFilter.create(pattern);
            Iterable<RevCommit> commits = git.log().add(head).setRevFilter(revFilter).call();
            List<Commit> result = new ArrayList<>();
            for (RevCommit commit : commits) {
                Commit tmp = createCommit(commit, fastNoDiff);
                result.add(tmp);
            }
            return result;
        } catch (GitAPIException e) {
            throw new GitHandlerException(e.getMessage(), e.getCause());
        }
    }

    public String getRepositoryRootPath() {
        return root;
    }

    @Override
    public List<Commit> log() throws GitHandlerException {
        try {
            ObjectId head = repository.resolve(Constants.HEAD);
            Iterable<RevCommit> revCommits = git.log().add(head).all().call();
            List<Commit> commits = new LinkedList<>();
            for (RevCommit revCommit : revCommits) {
                Commit commit = createCommit(revCommit, false);
                commits.add(commit);
            }
            return commits;
        } catch (GitAPIException e) {
            throw new GitHandlerException(e.getMessage(), e.getCause());
        } catch (IOException e) {
            throw new GitHandlerException(e.getMessage(), e.getCause());
        }

    }

    @Override
    public List<Commit> log(String path) throws GitHandlerException {
        try {
            ObjectId head = repository.resolve(Constants.HEAD);
            Iterable<RevCommit> revCommits = git.log().add(head).addPath(path).call();
            List<Commit> commits = new LinkedList<>();
            for (RevCommit revCommit : revCommits) {
                Commit commit = createCommit(revCommit, false);
                commits.add(commit);
            }
            return commits;
        } catch (GitAPIException e) {
            throw new GitHandlerException(e.getMessage(), e.getCause());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public List<Commit> log(String path, boolean fastNoDiff) throws IOException, GitHandlerException {
        try {
            ObjectId head = repository.resolve(Constants.HEAD);
            Iterable<RevCommit> revCommits = git.log().add(head).addPath(path).call();
            List<Commit> commits = new LinkedList<>();
            for (RevCommit revCommit : revCommits) {
                Commit commit = createCommit(revCommit, fastNoDiff);
                commits.add(commit);
            }
            return commits;
        } catch (GitAPIException e) {
            throw new GitHandlerException(e.getMessage(), e.getCause());
        }
    }

    @Override
    public List<Commit> log(Optional<String> tagA, Optional<String> tagB) throws GitHandlerException {
        return log(tagA, tagB, true);
    }

    public List<Commit> log(Optional<String> tagA, Optional<String> tagB, boolean fastNoDiff) throws GitHandlerException {
        try {
            ObjectId head = repository.resolve(Constants.HEAD);
            Iterable<RevCommit> revCommits;
            if (tagA.isEmpty() && tagB.isEmpty()) {
                return log(fastNoDiff);
            } else if (tagA.isPresent() && tagB.isEmpty()) {
                ObjectId from = GitHelper.getObjectId(repository, tagA.get());
                revCommits = git.log().addRange(from, head).call();
            } else if (tagA.isEmpty() && tagB.isPresent()) {
                ObjectId to = GitHelper.getObjectId(repository, tagB.get());
                RevWalk walk = new RevWalk(repository);
                walk.markStart(walk.parseCommit(to));
                // walk.sort( RevSort.REVERSE ); // chronological order
                revCommits = walk;
                //revCommits = git.log().addRange(head, to).call();
            } else {
                ObjectId from = GitHelper.getObjectId(repository, tagA.get());
                ObjectId to = GitHelper.getObjectId(repository, tagB.get());
                revCommits = git.log().addRange(from, to).call();
            }
            List<Commit> commits = new ArrayList<>();
            for (RevCommit revCommit : revCommits) {
                Commit commit = createCommit(revCommit, fastNoDiff);
                commits.add(commit);
            }
            commits.sort(Commit::compareTo);
            return commits;
        } catch (GitAPIException e) {
            throw new GitHandlerException(e.getMessage(), e.getCause());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public List<Commit> log(boolean fastNoDiff) throws GitHandlerException {
        try {
            ObjectId head = repository.resolve(Constants.HEAD);
            Iterable<RevCommit> revCommits = git.log().add(head).all().call();
            List<Commit> commits = new LinkedList<>();
            for (RevCommit revCommit : revCommits) {
                Commit commit = createCommit(revCommit, fastNoDiff);
                commits.add(commit);
            }
            return commits;
        } catch (GitAPIException e) {
            throw new GitHandlerException(e.getMessage(), e.getCause());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }


    public void checkout(String tag) throws IOException, GitHandlerException {
        try {
            git.checkout().setCreateBranch(false).setName(tag).call();
        } catch (GitAPIException e) {
            throw new GitHandlerException(e.getMessage(), e.getCause());
        }
    }

    public List<DiffStat> diff(Commit c1, Commit c2) throws GitHandlerException {
        return diff(c1.getName(), c2.getName());
    }

    public List<DiffStat> diff(String c1, String c2) throws GitHandlerException {
        try {
            List<DiffEntry> diffs = GitHelper.getListDiffEntry(repository, git, c1, c2);
            List<DiffStat> result = new ArrayList<>();
            for (DiffEntry diffEntry : diffs) {
                DiffStat diffStat = getDiffStat(diffEntry, c1, c2);
                result.add(diffStat);
            }
            return result;
        } catch (GitAPIException e) {
            throw new GitHandlerException(e.getMessage(), e.getCause());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public List<DiffStat> diffTest(String c1, String c2) throws  GitHandlerException {
        try {
            String cmd = String.format("git.exe diff --stat \"%s\" \"%s\"", c1, c2);
            ProcessBuilder pb = new ProcessBuilder("git", "diff", "--stat", c1, c2);
            pb.directory(new File(getRepositoryRootPath()));
            Process process = pb.start();
            BufferedReader output = new BufferedReader(new InputStreamReader((process.getInputStream())));
            output.lines().forEach(new Consumer<String>() {
                @Override
                public void accept(String s) {
                    System.out.println(s);
                }
            });
            return null;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (Exception e) {
            throw new GitHandlerException(e.getMessage(), e.getCause());
        }
    }


    public DiffStat diff(Commit c1, Commit c2, String path) throws GitHandlerException {
        try {
            List<DiffEntry> diffs = GitHelper.getListDiffEntry(repository, git, c1.getName(), c2.getName());
            for (DiffEntry diffEntry : diffs) {
                if (diffEntry.getOldPath().equals(path) || diffEntry.getNewPath().equals(path)) {
                    DiffStat diffStat = getDiffStat(diffEntry, c1.getName(), c2.getName());
                    return diffStat;
                }
            }
            return null;
        } catch (GitAPIException e) {
            throw new GitHandlerException(e.getMessage(), e.getCause());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public List<Tag> getAllTags() throws GitHandlerException {
        List<Tag> tags = new ArrayList<>();
        try {
            List<Ref> call = git.tagList().call();
            for (Ref ref : call) {
                Tag tag = new Tag(ref.getName(), ref.getObjectId().getName());
                tags.add(tag);
            }
        } catch (GitAPIException e) {
            throw new GitHandlerException(e.getMessage(), e.getCause());
        }
        return tags;
    }


    public Optional<Commit> show(String tag) throws GitHandlerException {
        try {
            ObjectId objectId = repository.resolve(tag);
            if (objectId == null) {
                return Optional.empty();
            }
            RevWalk revWalk = new RevWalk(repository);
            RevCommit revCommit = revWalk.parseCommit(objectId);
            Commit commit = createCommit(revCommit, true);
            revWalk.close();
            return Optional.of(commit);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public Set<String> getSnapshot(Commit commit) throws GitHandlerException {
        return new HashSet<>();
    }

    @Override
    public Set<String> getChangedFiles(Commit commit) throws GitHandlerException {
        return new HashSet<>();
    }

    @Override
    public long getNoChangedFiles(Commit commit) throws GitHandlerException {
        return 0;
    }

    @Override
    public Set<String> getSnapshot(Commit commit, String pattern) throws GitHandlerException {
        return null;
    }

    @Override
    public Set<String> getChangedFiles(Commit commit, String pattern) throws GitHandlerException {
        return null;
    }

    private Commit createCommit(RevCommit revCommit, boolean fastNoDiff) throws IOException, GitHandlerException {
        List<String> snapshot = GitHelper.getFiles(repository, revCommit);
        List<DiffStat> modifiedFiles = new ArrayList<>();
        if (!fastNoDiff) {
            for (ObjectId objectId : revCommit.getParents()) {
                String parentID = objectId.getName();
                List<DiffStat> diffs = diff(parentID, revCommit.getName());
                modifiedFiles.addAll(diffs);
            }
        }
        return new Commit(revCommit, snapshot, modifiedFiles);
    }


    /**
     * @param stream
     * @return
     * @throws IOException
     */
    private long countLinesFromInputStream(InputStream stream) throws IOException {
        BufferedInputStream bufferedInputStream = new BufferedInputStream(stream);
        long lines = 0;
        int readChar = 0;
        while ((readChar = bufferedInputStream.read()) != -1) {
            if (readChar == '\n') {
                lines++;
            }
        }
        bufferedInputStream.close();
        return lines;

    }


    public long getLoc(Commit commit, String file) throws IOException {
        Ref ref = repository.findRef(commit.getName());
        RevCommit revCommit = repository.parseCommit(ref.getObjectId());
        TreeWalk treeWalk = new TreeWalk(repository);
        treeWalk.reset(revCommit.getTree());
        while (treeWalk.next()) {
            String path = treeWalk.getPathString();
            if (file.equals(path)) {
                return countLinesOfObjectID(treeWalk.getObjectId(0));
            }
        }
        return 0;
    }

    /**
     * @param fileId
     * @return
     * @throws IOException
     */
    private long countLinesOfObjectID(ObjectId fileId) throws IOException {
        if (fileId == null) return 0;
        if (GitHelper.isNullId(fileId.getName())) return 0;
        ObjectLoader loader = repository.open(fileId);
        // load the content of the file into a stream
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        loader.copyTo(stream);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(stream.toByteArray());
        long count = countLinesFromInputStream(inputStream);
        inputStream.close();
        stream.close();
        return count;
    }


    private DiffStat getDiffStat(DiffEntry diffEntry, String oldCommitId, String newCommitId) throws IOException {
        String oldPath = diffEntry.getOldPath();
        String newPath = diffEntry.getNewPath();
        DiffType diffType = GitHelper.getDiffType(diffEntry.getChangeType());
        String oldId = diffEntry.getOldId().name();
        String newId = diffEntry.getNewId().name();
        long oldLoc = countLinesOfObjectID(diffEntry.getOldId().toObjectId());
        long newLoc = countLinesOfObjectID(diffEntry.getNewId().toObjectId());
        long linesAdded = 0;
        long linesDeleted = 0;
        for (Edit edit : diffFormatter.toFileHeader(diffEntry).toEditList()) {
            linesDeleted += edit.getEndA() - edit.getBeginA();
            linesAdded += edit.getEndB() - edit.getBeginB();
        }
        DiffStat diffStat = new DiffStat(
                oldPath,
                newPath,
                diffType,
                oldCommitId,
                newCommitId,
                linesAdded,
                linesDeleted
        );
        return diffStat;
    }

    public List<DiffStat> getFileDiffHistory(String path) throws IOException, GitHandlerException {
        List<Commit> commits = log(path);
        List<DiffStat> diff = new ArrayList<>();
        for (int i = 0; i < commits.size() - 1; i++) {
            DiffStat diffStat = diff(commits.get(i), commits.get(i + 1), path);
            if (diffStat != null)
                diff.add(diffStat);
        }
        return diff;
    }

    public List<Commit> getParents(Commit commit, boolean fastNoDiff) throws GitHandlerException, IOException {
        ObjectId commitId = GitHelper.getObjectId(repository, commit.getName());
        RevCommit revCommit = repository.parseCommit(commitId);
        RevCommit[] revParents = revCommit.getParents();
        List<Commit> parents = new ArrayList<>(revParents.length);
        for (RevCommit revParent : revParents) {
            Commit parent = createCommit(revCommit, fastNoDiff);
            parents.add(commit);
        }
        return parents;
    }

    public String getHead() {
        return Constants.HEAD;
    }

    public String getEmptyTree() {
        return Constants.EMPTY_TREE_ID.getName();
    }

}
