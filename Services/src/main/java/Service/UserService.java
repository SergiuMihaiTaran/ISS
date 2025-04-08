package Service;

import Domain.Participant;
import Domain.User;
import Repository.CompetitionsDBRepository;
import Repository.RepositoryInterface;
import Repository.UsersDBRepository;

import java.util.List;

public class UserService {
    private RepositoryInterface<Integer,User> repo;
    public UserService(RepositoryInterface<Integer,User> repo) {
        this.repo=repo;
    }
    public User searchByNameAndPassword(String name, String password) {
        List<User> users=repo.findAll();
        for (User user : users) {
            if (user.getUsername().equals(name) && user.getPassword().equals(password)) {
                return user;
            }
        }
                return null;
            }
    public User findOne(int id) {
        return repo.findOne(id);
    }

    public List<User> findAll() {
        return  repo.findAll();
    }

    public void save(User entity) {
        repo.save(entity);
    }

    public User update(User entity, User newEntity) {
        return  repo.update(entity,newEntity);
    }

    public User delete(int id) {
        return  repo.delete(id);
    }
}
