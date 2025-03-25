package Service;

import Domain.Entity;
import Repository.RepositoryInterface;

import java.util.List;

public class ServiceInterface<ID,E extends Entity<ID>> {
    public RepositoryInterface<ID,E> repo;

    ServiceInterface(RepositoryInterface<ID,E> repo) {
        this.repo = repo;
    }

    public E findOne(ID id) {
        return (E) repo.findOne(id);
    }

    public List<E> findAll() {
        return (List<E>) repo.findAll();
    }

    public void save(E entity) {
        repo.save(entity);
    }

    public E update(E entity, E newEntity) {
        return (E) repo.update(entity,newEntity);
    }

    public E delete(ID id) {
        return (E) repo.delete(id);
    }
}