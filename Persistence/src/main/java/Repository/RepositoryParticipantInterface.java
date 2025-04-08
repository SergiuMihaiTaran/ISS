package Repository;

import Domain.Competition;
import Domain.Entity;
import Domain.Participant;

import java.util.List;

public interface RepositoryParticipantInterface<ID,E extends Entity<ID>,C extends Entity<ID>>  extends  RepositoryInterface<ID,E>{
    void addParticipantToCompetition(int participantId, int competitionId);

    void removeParticipantFromCompetition(int participantId, int competitionId);
    public List<Participant> getParticipantsInCompetition(C entity);

    List<C> getComposition(int id);
}
