package Service;

import Domain.Competition;
import Domain.Participant;
import Repository.ParticipantsRepository;
import Repository.RepositoryInterface;
import Repository.RepositoryParticipantInterface;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ParticipantService {
    private RepositoryParticipantInterface<Integer,Participant,Competition> repo;
    public ParticipantService(RepositoryParticipantInterface<Integer,Participant,Competition> repo) {
        this.repo = repo;
    }
    public Participant findOne(int id) {
        return repo.findOne(id);
    }

    public List<Participant> findAll() {
        return  repo.findAll();
    }

    public void save(Participant entity) {
        repo.save(entity);
    }

    public Participant update(Participant entity, Participant newEntity) {
        return  repo.update(entity,newEntity);
    }

    public Participant delete(int id) {
        return  repo.delete(id);
    }
    public List<Participant> getParticipantsInCompetition(Competition competition) {

        return repo.getParticipantsInCompetition(competition);
    }

}
