package Service;

import Domain.Competition;
import Domain.Participant;
import Repository.ParticipantsRepository;
import Repository.RepositoryInterface;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ParticipantService extends ServiceInterface<Integer,Participant> {
    public ParticipantService(ParticipantsRepository repo) {
        super(repo);
    }

    public List<Participant> getParticipantsInCompetition(Competition competition) {
        ArrayList<Participant> participants = new ArrayList<>();
        for(Participant participant:findAll()){
            for(Competition comp:participant.getCompetitions()){
                if(comp.getId().equals(competition.getId())){
                    participants.add(participant);
                }
            }
        }
        return participants;
    }

}
