package Service;

import Domain.User;
import Repository.UsersDBRepository;

import java.util.List;

public class UserService extends ServiceInterface<Integer, User>{

    public UserService(UsersDBRepository repo) {
        super(repo);
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
}
