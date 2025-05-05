package Service;

import Domain.Bug;
import Domain.Competition;
import Domain.Participant;
import Domain.User;

import java.util.List;

public interface IServices {
    void login(User user ,IObserver client) throws Exception;
    void logout(User user ,IObserver client) throws Exception;
    void saveBug(Bug bug) throws Exception;
    void removeBug(Bug bug) throws Exception;
    List<Bug> getBugsList() throws Exception;

}
