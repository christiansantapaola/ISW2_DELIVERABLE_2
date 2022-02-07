package it.uniroma2.santapaola.christian.git.process;

import it.uniroma2.santapaola.christian.git.*;
import it.uniroma2.santapaola.christian.git.exception.GitHandlerException;

import java.io.*;
import java.time.LocalDate;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * GitHandler implementa l'interfaccia git, tramite la libreria java process.
 * Chiama il software git da cmd e esegue il parsing dell'output.
 */
public class GitHandler implements Git {
    public static final String DATE_ISO_8601 = "--date=iso8601";
    private final File repositoryRoot;
    private static final String LOG_FORMAT = "--format=\"%H;;%an;;%as;;%cn;;%cs\"";

    public GitHandler(String repository) {
        repositoryRoot = new File(repository);
    }

    public GitHandler(String url, String repository) {
        clone(url, repository);
        repositoryRoot = new File(repository);
    }

    private static void clone(String url, String path) {
        try {
            ProcessBuilder pb = new ProcessBuilder("git", "clone", url, path);
            pb.inheritIO();
            Process p = pb.start();
            p.waitFor();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }


    private List<Commit> parseLog(BufferedReader reader) {
        try {
            List<Commit> commits = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.substring(1, line.length() - 1);
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
            ProcessBuilder pb = new ProcessBuilder("git", "log", DATE_ISO_8601, LOG_FORMAT);
            pb.directory(repositoryRoot);
            Process process = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            List<Commit> commits = parseLog(reader);
            reader.close();
            return commits;
        } catch (IOException e) {
            throw new GitHandlerException(e.getMessage(), e.getCause());
        }

    }

    @Override
    public List<Commit> log(String path) throws GitHandlerException {
        try {
            ProcessBuilder pb = new ProcessBuilder("git", "log", DATE_ISO_8601, LOG_FORMAT, path);
            pb.directory(repositoryRoot);
            Process process = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            List<Commit> commits = parseLog(reader);
            reader.close();
            return commits;
        } catch (IOException e) {
            throw new GitHandlerException(e.getMessage(), e.getCause());
        }
    }

    @Override
    public List<Commit> log(Optional<String> tagA, Optional<String> tagB) throws GitHandlerException {
        try {
            if (!tagA.isPresent() && !tagB.isPresent()) {
                return log();
            } else if (tagA.isPresent() && !tagB.isPresent()) {
                ProcessBuilder pb = new ProcessBuilder("git", "log", DATE_ISO_8601, LOG_FORMAT, tagA.get() + "..." + "HEAD");
                pb.directory(repositoryRoot);
                Process process = pb.start();
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                List<Commit> commits = parseLog(reader);
                reader.close();
                return commits;
            } else if (!tagA.isPresent()) {
                ProcessBuilder pb = new ProcessBuilder("git", "log", DATE_ISO_8601, LOG_FORMAT, tagB.get());
                pb.directory(repositoryRoot);
                java.lang.Process process = pb.start();
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                List<Commit> commits = parseLog(reader);
                reader.close();
                return commits;
            } else {
                ProcessBuilder pb = new ProcessBuilder("git", "log", DATE_ISO_8601, LOG_FORMAT, tagA.get() + "..." + tagB.get());
                pb.directory(repositoryRoot);
                java.lang.Process process = pb.start();
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                List<Commit> commits = parseLog(reader);
                reader.close();
                return commits;
            }
        } catch (IOException e) {
            throw new GitHandlerException(e.getMessage(), e.getCause());
        }
    }

    @Override
    public List<Commit> grep(String pattern) throws GitHandlerException {
        try {
            ProcessBuilder pb = new ProcessBuilder("git", "log", DATE_ISO_8601, LOG_FORMAT, "--grep=" + pattern);
            pb.directory(repositoryRoot);
            java.lang.Process process = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            List<Commit> commits = parseLog(reader);
            reader.close();
            return commits;
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
            List<DiffStat> diffStats = parseDiff(reader, c1, c2);
            reader.close();
            return diffStats;
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
            List<Tag> tags = parseTag(reader);
            reader.close();
            return tags;
        } catch (IOException e) {
            throw new GitHandlerException(e.getMessage(), e.getCause());
        }

    }

    @Override
    public Optional<Commit> show(String id) throws GitHandlerException {
        try {
            ProcessBuilder pb = new ProcessBuilder("git", "log", "-1", DATE_ISO_8601, LOG_FORMAT, id);
            pb.directory(repositoryRoot);
            Process process = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            List<Commit> commits = parseLog(reader);
            reader.close();
            if (commits.isEmpty()) {
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
            return getSnapshot(commit.getName(), ".*");
        } catch (IOException e) {
            throw new GitHandlerException(e.getMessage(), e.getCause());
        }
    }

    @Override
    public Set<String> getChangedFiles(Commit commit) throws GitHandlerException{
        try {
            return getChangedFiles(commit.getName(), ".*");
        } catch (IOException e) {
            throw new GitHandlerException(e.getMessage(), e.getCause());
        }
    }


    private Set<String> getSnapshot(String id, String pattern) throws IOException {
        ProcessBuilder pb = new ProcessBuilder("git", "ls-tree", "-r", "--name-only", id);
        pb.directory(repositoryRoot);
        Process process = pb.start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        Set<String> snapshot = new HashSet<>();
        String line;
        Pattern filter = Pattern.compile(pattern);
        while ((line = reader.readLine()) != null) {
            Matcher matcher = filter.matcher(line);
            if (matcher.matches()) {
                snapshot.add(line);
            }
        }
        reader.close();
        return snapshot;
    }

    private HashSet<String> getChangedFiles(String id, String pattern) throws IOException {
        ProcessBuilder pb = new ProcessBuilder("git", "diff", "--name-only", id + "~", id);
        pb.directory(repositoryRoot);
        Process process = pb.start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        HashSet<String> result = new HashSet<>();
        // skip first line
        String line;
        Pattern filter = Pattern.compile(pattern);
        while ((line = reader.readLine()) != null) {
            Matcher matcher = filter.matcher(line);
            if (matcher.matches()) {
                result.add(line);
            }
        }
        reader.close();
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
            reader.close();
            Pattern pattern = Pattern.compile("\\s+(\\d+).*");
            Matcher matcher = pattern.matcher(statLine);
            if (matcher.matches()) {
                if (!matcher.group(1).equals("-")) {
                    return Long.parseLong(matcher.group(1));
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
        } catch (IOException e) {
            throw new GitHandlerException(e.getMessage(), e.getCause());
        }
    }

    @Override
    public Set<String> getChangedFiles(Commit commit, String pattern) throws GitHandlerException {
        try {
            return getChangedFiles(commit.getName(), pattern);
        } catch (IOException e) {
            throw new GitHandlerException(e.getMessage(), e.getCause());
        }
    }

}
