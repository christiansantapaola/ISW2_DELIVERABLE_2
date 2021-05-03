package it.uniroma2.santapaola.christian;

import java.io.File;

public class OutputDirectory {
    private String output;
    private String repository;

    public OutputDirectory(String output, String repository) {
        this.output = output;
        this.repository = repository;
    }

    public String getOutput() {
        return output;
    }

    public String getRepository() {
        return repository;
    }

    public boolean ExistsRepositoryFile(String rpath) {
        return new File(repository + rpath).exists();
    }

}
