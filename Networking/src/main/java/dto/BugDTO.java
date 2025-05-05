package dto;

import Domain.Bug;
import java.io.Serializable;

public class BugDTO implements Serializable {
    private String description;
    private String name;
    private int id;
    private Bug bug;

    public BugDTO(String description, String name) {
        this(description, name, 0);
    }

    public BugDTO(String description, String name, int id) {
        this.description = description;
        this.name = name;
        this.id = id;
    }

    public BugDTO(Bug bug) {
        this.description = bug.getDescription();
        this.name = bug.getName();
        this.id = bug.getId();
        this.bug = bug;
    }

    public Bug getBug() {
        return bug;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "BugDTO[" + description + " " + name + " " + id + "]";
    }
}
