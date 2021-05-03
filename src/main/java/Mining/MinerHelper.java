package Mining;

import GitSubSystem.Commit;
import GitSubSystem.Exception.GitHandlerException;
import GitSubSystem.GitHandler;
import GitSubSystem.Tag;
import JiraSubSystem.Release;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

public class MinerHelper {
    public static Optional<String> getTagFromReleaseName(GitHandler git, String releaseName) throws GitHandlerException {
        List<Tag> tags = git.getAllTags();
        Pattern p = Pattern.compile("^.*?" + releaseName);
        for (Tag tag : tags) {
            if (p.matcher(tag.getName()).matches()) {
                return Optional.of(tag.getName());
            }
        }
        return Optional.empty();
    }

    public static Set<String> getSnapshot(GitHandler git, Release release) throws IOException, GitHandlerException {
        Set<String> snapshot = new HashSet<>();
        Optional<String> tag = getTagFromReleaseName(git, release.getName());
        if (tag.isEmpty()) return snapshot;
        Optional<Commit> commit = git.getTagCommit(tag.get());
        if (commit.isEmpty()) return snapshot;
        return commit.get().getRepositorySnapshot();
    }

    public static String getRelativePathFromRoot(File root, File file) {
        return file.toURI().relativize(root.toURI()).getPath();
    }

}
