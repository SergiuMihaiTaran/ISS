package dto;

import Domain.Competition;

import java.io.Serializable;

public class CompetitionDTO implements Serializable {

    private String style;
    private int distance;
    private int id;
    private int noParticipants;
    private Competition competition;
    public CompetitionDTO(String style) {
        this(style,0,0,0);
    }
    public int getNoParticipants() {
        return noParticipants;
    }
    public CompetitionDTO(Competition competition, int noParticipants) {
        this.competition = competition;
        this.noParticipants = noParticipants;
    }
    public Competition getCompetition() {
        return competition;
    }
    public CompetitionDTO(String style, int distance,int id,int noParticipants) {
        this.style = style;
        this.distance = distance;
        this.id=id;
        this.noParticipants=noParticipants;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }
    public int getId(){
        return this.id;
    }
    public int getDistance() {
        return distance;
    }

    @Override
    public String toString(){
        return "UserDTO["+ style +' '+distance+' '+id+"]";
    }
}
