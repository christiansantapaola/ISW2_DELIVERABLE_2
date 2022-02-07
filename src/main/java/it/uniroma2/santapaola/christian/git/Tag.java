package it.uniroma2.santapaola.christian.git;

/**
 * La classe TAG associa il nome di un tag con il suo commit id.
 */
public class Tag {
    private String name;
    private String id;

    public Tag(String name, String id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Tag{" +
                "name='" + name + '\'' +
                ", id='" + id + '\'' +
                '}';
    }
}
