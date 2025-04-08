package Service;

import Domain.Competition;
import Domain.Participant;
import Domain.User;

import java.util.List;

public interface IServices {
    void login(User user ,IObserver client) throws Exception;
    void logout(User user ,IObserver client) throws Exception;
    List<Competition> getCompetitionsList() throws Exception;
    List<Participant>getParticipantsInCompetition(Competition competition) throws Exception;
    void saveParticipant(Participant participant) throws Exception;

}
