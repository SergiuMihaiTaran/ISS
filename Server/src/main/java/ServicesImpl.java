


import Domain.Bug;
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
    private RepositoryInterface<Integer,Bug> bugRepo;
    private static Logger logger = LogManager.getLogger(ServicesImpl.class);
    private Map<String, IObserver> loggedClients;

    public ServicesImpl(IUserRepository repoUser,RepositoryInterface<Integer,Bug> bugRepo){
        this.repoUser = repoUser;
        this.bugRepo = bugRepo;
        loggedClients = new ConcurrentHashMap<>();
    }
    public synchronized void login(User user, IObserver client) throws Exception {
        User userR=repoUser.searchByNameAndPassword(user.getUsername(), user.getPassword(),user.getTypeOfEmployee());
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
    public void saveBug(Bug bug) throws Exception {
        bugRepo.save(bug);
        notifyListUpdated();
    }

    @Override
    public void removeBug(Bug bug) {
        bugRepo.delete(bug.getId());
    }

    @Override
    public List<Bug> getBugsList() throws Exception {
        return bugRepo.findAll();
    }

}
