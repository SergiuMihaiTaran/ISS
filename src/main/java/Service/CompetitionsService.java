package Service;

import Domain.Competition;
import Domain.Participant;
import Repository.CompetitionsDBRepository;
import Repository.ParticipantsRepository;
import Repository.RepositoryInterface;

import java.util.List;

public class CompetitionsService  {
    private RepositoryInterface<Integer,Competition> repo;
    public CompetitionsService(RepositoryInterface<Integer,Competition> repo) {
        this.repo = repo;
    }
    public Competition findOne(int id) {
        return repo.findOne(id);
    }

    public List<Competition> findAll() {
        return  repo.findAll();
    }

    public void save(Competition entity) {
        repo.save(entity);
    }

    public Competition update(Competition entity, Competition newEntity) {
        return  repo.update(entity,newEntity);
    }

    public Competition delete(int id) {
        return  repo.delete(id);
    }
}
