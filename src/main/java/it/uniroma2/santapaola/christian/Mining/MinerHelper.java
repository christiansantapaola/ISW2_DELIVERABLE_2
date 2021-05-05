package it.uniroma2.santapaola.christian.Mining;

import it.uniroma2.santapaola.christian.GitSubSystem.Commit;
import it.uniroma2.santapaola.christian.GitSubSystem.Exception.GitHandlerException;
import it.uniroma2.santapaola.christian.GitSubSystem.Git;
import it.uniroma2.santapaola.christian.GitSubSystem.jgit.GitHandler;
import it.uniroma2.santapaola.christian.GitSubSystem.Tag;
import it.uniroma2.santapaola.christian.JiraSubSystem.Release;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

public class MinerHelper {
    public static Optional<String> getTagFromReleaseName(Git git, String releaseName) throws GitHandlerException {
        List<Tag> tags = git.getAllTags();
        Pattern p = Pattern.compile("^.*?" + releaseName);
        for (Tag tag : tags) {
            if (p.matcher(tag.getName()).matches()) {
                return Optional.of(tag.getName());
            }
        }
        return Optional.empty();
    }

    public static Set<String> getSnapshot(Git git, Release release) throws IOException, GitHandlerException {
        Set<String> snapshot = new HashSet<>();
        Optional<String> tag = getTagFromReleaseName(git, release.getName());
        if (tag.isEmpty()) return snapshot;
        Optional<Commit> commit = git.show(tag.get());
        if (commit.isEmpty()) return snapshot;
        return git.getSnapshot(commit.get());
    }

    public static String getRelativePathFromRoot(File root, File file) {
        return file.toURI().relativize(root.toURI()).getPath();
    }

}
