package it.uniroma2.santapaola.christian.GitSubSystem;

import it.uniroma2.santapaola.christian.GitSubSystem.GitProcess.GitHandler;

import java.io.File;

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
    };

    public void setGitProcess() {
        type = GitHandlerType.PROCESS;
    }


    public Git build() {
        switch (type) {
            case PROCESS:
                if (!gitDbPathFile.exists()) {
                    return new GitHandler(url, repository);
                } else {
                    return new GitHandler(repository);
                }
            default:
                throw new IllegalStateException();
        }
    }



}
