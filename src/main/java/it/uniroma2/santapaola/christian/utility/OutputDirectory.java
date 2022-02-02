package it.uniroma2.santapaola.christian.utility;


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
}
