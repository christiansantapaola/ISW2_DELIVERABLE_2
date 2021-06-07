package it.uniroma2.santapaola.christian.git;

import it.uniroma2.santapaola.christian.git.process.GitHandler;

import java.io.File;

public class GitFactory {

    private String url;
    private String repository;
    private String gitDbPath;
    private File gitDbPathFile;
    private GitHandlerType type;

    public GitFactory(String url, String repository) {
        this.url = url;
        this.repository = repository;
        gitDbPath = repository + "/.git/";
        gitDbPathFile = new File(gitDbPath);
        type = GitHandlerType.PROCESS;
    }

    private enum GitHandlerType {
        PROCESS,
    }

    public void setGitProcess() {
        type = GitHandlerType.PROCESS;
    }


    public Git build() {
        if (type == GitHandlerType.PROCESS) {
            if (!gitDbPathFile.exists()) {
                return new GitHandler(url, repository);
            } else {
                return new GitHandler(repository);
            }
        } else {
            throw new IllegalStateException();
        }
    }



}
