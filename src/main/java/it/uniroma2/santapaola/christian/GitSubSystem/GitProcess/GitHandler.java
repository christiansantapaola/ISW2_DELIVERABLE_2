package it.uniroma2.santapaola.christian.GitSubSystem.GitProcess;

import it.uniroma2.santapaola.christian.GitSubSystem.*;
import it.uniroma2.santapaola.christian.GitSubSystem.Exception.GitHandlerException;

import java.io.*;
import java.time.LocalDate;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GitHandler implements Git {
    private File gitFilePathDotGit;
    private File repositoryRoot;
    private final static String logFormat = "--format=\"%H;;%an;;%as;;%cn;;%cs\"";

    public GitHandler(String repository) {
        gitFilePathDotGit = new File(repository + "/.git/");
        repositoryRoot = new File(repository);
    }

    public GitHandler(String url, String repository) {
        clone(url, repository);
        repositoryRoot = new File(repository);
        gitFilePathDotGit = new File(repository + "/.git/");
    }

    private static void clone(String url, String path) {
        try {
            ProcessBuilder pb = new ProcessBuilder("git", "clone", url, path);
            pb.inheritIO();
            Process p = pb.start();
            try {
                p.waitFor();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }


    private List<Commit> parseLog(BufferedReader reader) {
        try {
            List<Commit> commits = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] split = line.split(";;");
                Commit commit = new Commit(split[0], split[1], LocalDate.parse(split[2]), split[3], LocalDate.parse(split[4]));
                commits.add(commit);
            }
            return commits;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public List<Commit> log() throws GitHandlerException {
        try {
            ProcessBuilder pb = new ProcessBuilder("git", "log", "--date=iso8601", logFormat);
            pb.directory(repositoryRoot);
            Process process = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            return parseLog(reader);
        } catch (IOException e) {
            throw new GitHandlerException(e.getMessage(), e.getCause());
        }

    }

    @Override
    public List<Commit> log(String path) throws GitHandlerException {
        try {
            ProcessBuilder pb = new ProcessBuilder("git", "log", "--date=iso8601", logFormat, path);
            System.out.println(repositoryRoot);
            pb.directory(repositoryRoot);
            Process process = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            return parseLog(reader);
        } catch (IOException e) {
            throw new GitHandlerException(e.getMessage(), e.getCause());
        }
    }

    @Override
    public List<Commit> log(Optional<String> tagA, Optional<String> tagB) throws GitHandlerException {
        try {
            if (tagA.isEmpty() && tagB.isEmpty()) {
                return log();
            }
            if (tagA.isPresent() && tagB.isEmpty()) {
                ProcessBuilder pb = new ProcessBuilder("git", "log", "--date=iso8601", logFormat, tagA.get() + "..." + "HEAD");
                pb.directory(repositoryRoot);
                Process process = pb.start();
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                return parseLog(reader);
            }
            if (tagA.isEmpty() && tagB.isPresent()) {
                ProcessBuilder pb = new ProcessBuilder("git", "log", "--date=iso8601", logFormat, tagB.get());
                pb.directory(repositoryRoot);
                Process process = pb.start();
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                return parseLog(reader);
            } else {
                ProcessBuilder pb = new ProcessBuilder("git", "log", "--date=iso8601", logFormat, tagA.get() + "..." + tagB.get());
                pb.directory(repositoryRoot);
                Process process = pb.start();
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                return parseLog(reader);
            }
        } catch (IOException e) {
            throw new GitHandlerException(e.getMessage(), e.getCause());
        }
    }

    @Override
    public List<Commit> grep(String pattern) throws GitHandlerException {
        try {
            ProcessBuilder pb = new ProcessBuilder("git", "log", "--date=iso8601",logFormat, "--grep=" + pattern);
            pb.directory(repositoryRoot);
            Process process = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            return parseLog(reader);
        } catch (IOException e) {
            throw new GitHandlerException(e.getMessage(), e.getCause());
        }

    }

    private List<DiffStat> parseDiff(BufferedReader reader, String oldCommit, String newCommit) throws IOException {
        List<DiffStat> diffs = new ArrayList<>();
        Pattern pattern = Pattern.compile("^(\\d+)\\s+(\\d+)\\s+([^\\s]+)(\\s=>\\s([^\\s]+))?$");
        String line;
        while ((line = reader.readLine()) != null) {
            Matcher matcher = pattern.matcher(line);
            if (!matcher.matches()) {
                //System.err.println("ERROR: \"" + line + "\" does not match!");
                continue;
            }
            long addedLoc = Long.parseLong(matcher.group(1));
            long deletedLoc = Long.parseLong(matcher.group(2));
            String oldPath = matcher.group(3);
            String newPath;
            if (matcher.group(5) != null) {
                newPath = matcher.group(5);
            } else {
                newPath = oldPath;
            }
            DiffType type;
            if (!oldPath.equals(newPath)) {
                type = DiffType.RENAME;
            } else if (addedLoc == 0 && deletedLoc > 0) {
                type = DiffType.DELETE;
            } else if (addedLoc > 0 && deletedLoc == 0) {
                type = DiffType.ADD;
            } else {
                type = DiffType.MODIFY;
            }
            DiffStat diffStat = new DiffStat(oldPath, newPath, type, oldCommit, newCommit, addedLoc, deletedLoc);
            diffs.add(diffStat);
        }
        return diffs;
    }

    @Override
    public List<DiffStat> diff(Commit c1, Commit c2) throws GitHandlerException {
        return diff(c1.getName(), c2.getName());
    }

    @Override
    public List<DiffStat> diff(String c1, String c2) throws GitHandlerException {
        try {
            ProcessBuilder pb = new ProcessBuilder("git", "diff", "--numstat", c1, c2);
            pb.directory(repositoryRoot);
            Process process = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            return parseDiff(reader, c1, c2);
        } catch (IOException e) {
            throw new GitHandlerException(e.getMessage(), e.getCause());
        }
    }

    private List<Tag> parseTag(BufferedReader reader) throws IOException {
        List<Tag> tags = new ArrayList<>();
        String line;
        while ((line = reader.readLine()) != null) {
            String[] row = line.split(" ");
            if (row.length < 2) continue;
            String name = row[1];
            String id = row[0];
            Tag tag = new Tag(name, id);
            tags.add(tag);
        }
        return tags;
    }

    @Override
    public List<Tag> getAllTags() throws GitHandlerException {
        try {
            ProcessBuilder pb = new ProcessBuilder("git", "show-ref", "--tags");
            pb.directory(repositoryRoot);
            Process process = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            return parseTag(reader);
        } catch (IOException e) {
            throw new GitHandlerException(e.getMessage(), e.getCause());
        }

    }

    @Override
    public Optional<Commit> show(String id) throws GitHandlerException {
        try {
            ProcessBuilder pb = new ProcessBuilder("git", "log", "-1", "--date=iso8601", logFormat);
            pb.directory(repositoryRoot);
            Process process = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            List<Commit> commits = parseLog(reader);
            if (commits.size() < 1) {
                return Optional.empty();
            } else {
                return Optional.of(commits.get(0));
            }
        } catch (IOException e) {
            throw new GitHandlerException(e.getMessage(), e.getCause());
        }
    }

    @Override
    public Set<String> getSnapshot(Commit commit) throws GitHandlerException {
        try {
            return getSnapshot(commit.getName(), "*");
        } catch (IOException | InterruptedException e) {
            throw new GitHandlerException(e.getMessage(), e.getCause());
        }
    }

    @Override
    public Set<String> getChangedFiles(Commit commit) throws GitHandlerException{
        try {
            return getChangedFiles(commit.getName(), "*");
        } catch (IOException | InterruptedException e) {
            throw new GitHandlerException(e.getMessage(), e.getCause());
        }
    }


    private Set<String> getSnapshot(String id, String pattern) throws InterruptedException, IOException {
        ProcessBuilder pb = new ProcessBuilder("git", "ls-tree", "-r", "--name-only", id);
        pb.directory(repositoryRoot);
        Process process = pb.start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        //process.waitFor();
        Set<String> snapshot = new HashSet<>();
        String line;
        Pattern filter = Pattern.compile(pattern);
        while ((line = reader.readLine()) != null) {
            Matcher matcher = filter.matcher(line);
            if (matcher.matches()) {
                snapshot.add(line);
            }
        }
        return snapshot;
    }

    private HashSet<String> getChangedFiles(String id, String pattern) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder("git", "log", "-1", "--name-only", "--oneline", id);
        pb.directory(repositoryRoot);
        Process process = pb.start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        HashSet<String> result = new HashSet<>();
        String line = reader.readLine();
        Pattern filter = Pattern.compile(pattern);
        while ((line = reader.readLine()) != null) {
            Matcher matcher = filter.matcher(line);
            if (matcher.matches()) {
                result.add(line);
            }
        }
        return result;
    }

    @Override
    public long getNoChangedFiles(Commit commit) throws GitHandlerException {
        try {
            ProcessBuilder pb = new ProcessBuilder("git", "log", "-1", "--format=oneline", "--shortstat", commit.getName());
            pb.directory(repositoryRoot);
            Process process = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String commitLine = reader.readLine();
            if (commitLine == null) throw new GitHandlerException();
            String statLine = reader.readLine();
            if (statLine == null) return 0;
            Pattern pattern = Pattern.compile("\\s+(\\d+).*");
            Matcher matcher = pattern.matcher(statLine);
            if (matcher.matches()) {
                if (!matcher.group(1).equals("-")) {
                    long noFileChanged = Long.parseLong(matcher.group(1));
                    return noFileChanged;
                } else {
                    return 0;
                }
            } else {
                throw new GitHandlerException();
            }
        } catch (IOException e) {
            throw new GitHandlerException(e.getMessage(), e.getCause());
        }
    }


    @Override
    public Set<String> getSnapshot(Commit commit, String pattern) throws GitHandlerException {
        try {
            return getSnapshot(commit.getName(), pattern);
        } catch (IOException | InterruptedException e) {
            throw new GitHandlerException(e.getMessage(), e.getCause());
        }
    }

    @Override
    public Set<String> getChangedFiles(Commit commit, String pattern) throws GitHandlerException {
        try {
            return getChangedFiles(commit.getName(), pattern);
        } catch (IOException | InterruptedException e) {
            throw new GitHandlerException(e.getMessage(), e.getCause());
        }
    }

}
