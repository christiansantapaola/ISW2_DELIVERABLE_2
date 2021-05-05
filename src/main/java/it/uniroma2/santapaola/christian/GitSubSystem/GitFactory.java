package it.uniroma2.santapaola.christian.GitSubSystem;

import it.uniroma2.santapaola.christian.GitSubSystem.Exception.GitHandlerException;
import it.uniroma2.santapaola.christian.GitSubSystem.GitProcess.GitHandler;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;

public class GitFactory {

    private String url;
    private String repository;
    private File repositoryFile;
    private String gitDbPath;
    private File gitDbPathFile;
    private GitHandlerType type;

    public GitFactory(String url, String repository) {
        this.url = url;
        this.repository = repository;
        repositoryFile = new File(repository);
        gitDbPath = repository + "/.git/";
        gitDbPathFile = new File(gitDbPath);
        type = GitHandlerType.PROCESS;
    }

    private enum GitHandlerType {
        PROCESS,
        JGIT
    };

    public void setGitProcess() {
        type = GitHandlerType.PROCESS;
    }

    public void setJGIT() {
        type = GitHandlerType.JGIT;
    }

    public Git build() {
        switch (type) {
            case PROCESS:
                if (!gitDbPathFile.exists()) {
                    return new GitHandler(url, repository);
                } else {
                    return new GitHandler(repository);
                }
            case JGIT:
                try {
                    if (!gitDbPathFile.exists()) {
                        return new it.uniroma2.santapaola.christian.GitSubSystem.jgit.GitHandler(url, repositoryFile);
                    } else {
                        return new it.uniroma2.santapaola.christian.GitSubSystem.jgit.GitHandler(gitDbPathFile);
                    }
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                } catch (GitHandlerException e) {
                    throw new IllegalStateException(e.getMessage());
                }
            default:
                throw new IllegalStateException();
        }
    }



}
