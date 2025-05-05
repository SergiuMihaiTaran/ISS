package dto;

import Domain.*;

import java.util.ArrayList;
import java.util.List;

public class DTOUtils {
    public static User getFromDTO(UserDTO usdto){
        String name=usdto.getName();
        String pass=usdto.getPasswd();
        TypeOfEmployee typeOfEmployee=usdto.getType();
       User user= new User(name, pass,"", typeOfEmployee);

        return user;

    }
    public static Competition getFromDTO(CompetitionDTO dto){

        Competition competition=new Competition(dto.getDistance(), dto.getStyle());
        competition.setId(dto.getId());
        return competition;

    }
    public static CompetitionDTO getDTO(Competition competition){
        return new CompetitionDTO(competition.getStyle(),competition.getDistance(),competition.getId(),0);
    }
    public static UserDTO getDTO(User user){

        String pass=user.getPassword();
        return new UserDTO(user.getUsername(), pass,user.getTypeOfEmployee());
    }
    public static List<CompetitionDTO> getDTO(List<Competition> competitions){
        List<CompetitionDTO> competitionDTOS=new ArrayList<>();
        for(var comp:competitions){
            competitionDTOS.add(getDTO(comp));
        }
        return competitionDTOS;
    }
    public static List<Competition> getFromDTO(List<CompetitionDTO> competitions){
        List<Competition> competitionDTOS=new ArrayList<>();
        for(var comp:competitions){
            competitionDTOS.add(getFromDTO(comp));
        }
        return competitionDTOS;
    }
    public static Participant getFromDTO(ParticipantDTO dto){

        Participant participant=new Participant(dto.getName(), dto.getAge(),getFromDTO(dto.getComps()));
        participant.setId(dto.getId());
        return participant;

    }
    public static ParticipantDTO getDTO(Participant participant){
        return new ParticipantDTO(participant.getName(),participant.getAge(),getDTO(participant.getCompetitions()));
    }
    public static List<ParticipantDTO> getDTOp(List<Participant> participants){
        List<ParticipantDTO> participantDTOS=new ArrayList<>();
        for(var comp:participants){
            participantDTOS.add(getDTO(comp));
        }
        return participantDTOS;
    }
    public static List<Participant> getFromDTOp(List<ParticipantDTO> participants){
        List<Participant> participantDTOS=new ArrayList<>();
        for(var comp:participants){
            participantDTOS.add(getFromDTO(comp));
        }
        return participantDTOS;
    }
    public static Bug getFromDTOb(BugDTO dto) {
        Bug bug = new Bug(dto.getDescription(), dto.getName());
        bug.setId(dto.getId());
        return bug;
    }

    public static BugDTO getDTOb(Bug bug) {
        return new BugDTO(bug.getDescription(), bug.getName());
    }

    public static List<BugDTO> getDTOb(List<Bug> bugs) {
        List<BugDTO> bugDTOS = new ArrayList<>();
        for (Bug bug : bugs) {
            bugDTOS.add(getDTOb(bug));
        }
        return bugDTOS;
    }

    public static List<Bug> getFromDTOb(List<BugDTO> bugDTOS) {
        List<Bug> bugs = new ArrayList<>();
        for (BugDTO dto : bugDTOS) {
            bugs.add(getFromDTOb(dto));
        }
        return bugs;
    }
}
