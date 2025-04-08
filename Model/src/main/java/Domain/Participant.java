package Domain;

import java.util.List;

public class Participant extends Entity<Integer> {
    private String name;
    private int age;
    private List<Competition> competitions;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public List<Competition> getCompetitions() {
        return competitions;
    }

    public void setCompetitions(List<Competition> competitions) {
        this.competitions = competitions;
    }

    public Participant(String name, int age, List<Competition> competitions) {
        this.name = name;
        this.age = age;
        this.competitions = competitions;
    }
}
