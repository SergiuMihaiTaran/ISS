package Repository;

import Domain.User;

public interface IUserRepository extends RepositoryInterface<Integer, User> {
    public User searchByNameAndPassword(String name, String password);
}
