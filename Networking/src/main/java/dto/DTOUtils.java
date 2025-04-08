package dto;

import Domain.Competition;
import Domain.Participant;
import Domain.User;

import java.util.ArrayList;
import java.util.List;

public class DTOUtils {
    public static User getFromDTO(UserDTO usdto){
        String name=usdto.getName();
        String pass=usdto.getPasswd();
       User user= new User(name, pass,"");

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
        return new UserDTO(user.getUsername(), pass);
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
}
