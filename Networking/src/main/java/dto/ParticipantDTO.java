package dto;

import Domain.Competition;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ParticipantDTO implements Serializable {

    private String name;
    private int age;
    private List<CompetitionDTO> comps;
    private int id;
    public ParticipantDTO(String name) {
        this(name,0,new ArrayList<>(),0);
    }
    public ParticipantDTO(String name,int age,List<CompetitionDTO> comps) {
        this(name,age,comps,0);
    }
    public ParticipantDTO(String name, int age, List<CompetitionDTO> comps, int id) {
        this.name = name;
        this.age = age;
        this.comps=comps;
        this.id=id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
public int getId(){
        return id;
}
    public int getAge() {
        return age;
    }
    public List<CompetitionDTO> getComps(){return comps;}
    @Override
    public String toString(){
        return "UserDTO["+ name +' '+ age +' '+ comps+"]";
    }
}
