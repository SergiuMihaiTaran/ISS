


import Domain.Competition;
import Domain.Participant;
import Domain.User;
import Repository.IUserRepository;
import Repository.RepositoryInterface;
import Repository.RepositoryParticipantInterface;
import Service.IObserver;
import Service.IServices;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;


public class ServicesImpl implements IServices {
    private IUserRepository repoUser;
    private RepositoryParticipantInterface<Integer, Participant, Competition> repoParticipant;
    private RepositoryInterface<Integer, Competition> repoCompetition;
    private static Logger logger = LogManager.getLogger(ServicesImpl.class);
    private Map<String, IObserver> loggedClients;

    public ServicesImpl(IUserRepository repoUser, RepositoryParticipantInterface<Integer, Participant, Competition> repoParticipant, RepositoryInterface<Integer, Competition> repoCompetition) {
        this.repoUser = repoUser;
        this.repoParticipant = repoParticipant;
        this.repoCompetition = repoCompetition;
        loggedClients = new ConcurrentHashMap<>();
    }
    public synchronized void login(User user, IObserver client) throws Exception {
        User userR=repoUser.searchByNameAndPassword(user.getUsername(), user.getPassword());
        logger.info(userR.getUsername()+" "+userR.getPassword());
        if (userR!=null){
            if(loggedClients.get(user.getUsername())!=null)
                throw new Exception("User already logged in.");
            loggedClients.put(user.getUsername(), client);

        }else
            throw new Exception("Authentication failed.");
    }
    private final int defaultThreadsNo=4;
    @Override
    public synchronized void logout(User user, IObserver client) {
        System.out.println(user.getUsername()+" "+user.getPassword());
        IObserver loggedClient=loggedClients.remove(user.getUsername());
        if(loggedClient==null)
            throw new ExceptionInInitializerError("No logged client found.");
    }

    @Override
    public synchronized List<Competition> getCompetitionsList() {
        List<Competition> originals = repoCompetition.findAll();
        List<Competition> copies = new ArrayList<>();

        for (Competition original : originals) {
            Competition c = new Competition( original.getDistance(),original.getStyle());
            c.setId(original.getId());
            copies.add(c);
        }

        return copies;


    }
    private  void notifyListUpdated(){
        ExecutorService executor= Executors.newFixedThreadPool(defaultThreadsNo);
        for(IObserver user:loggedClients.values()){
            executor.execute(() -> {
                try {
                    logger.debug("Notifying List Updated");
                    user.listUpdated();


                } catch (Exception e) {
                    logger.error("Error notifying friend " + e);
                }
            });
        }
    }
    @Override
    public synchronized List<Participant> getParticipantsInCompetition(Competition competition) {
        return repoParticipant.getParticipantsInCompetition(competition);

    }

    @Override
    public synchronized void saveParticipant(Participant participant) throws Exception {
        repoParticipant.save(participant);
        notifyListUpdated();
    }

}
