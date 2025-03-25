package Service;

import Domain.Competition;
import Repository.CompetitionsDBRepository;
import Repository.ParticipantsRepository;

public class CompetitionsService extends ServiceInterface<Integer, Competition> {
    public CompetitionsService(CompetitionsDBRepository repo) {
        super(repo);
    }
}
