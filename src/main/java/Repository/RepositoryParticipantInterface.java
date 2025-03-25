package Repository;

import Domain.Entity;

import java.util.List;

public interface RepositoryParticipantInterface<ID,E extends Entity<ID>,C extends Entity<ID>>  extends  RepositoryInterface<ID,E>{
    void addParticipantToCompetition(int participantId, int competitionId);

    void removeParticipantFromCompetition(int participantId, int competitionId);

    List<C> getComposition(int id);
}
