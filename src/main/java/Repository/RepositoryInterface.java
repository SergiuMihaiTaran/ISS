package Repository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import Domain.Entity;

import java.rmi.Remote;
import java.util.List;

public interface RepositoryInterface<ID,E extends Entity<ID>> {
    E findOne(ID id);
    List<E> findAll();
    void save(E entity);
    E update(E entity,E newEntity);
    E delete(ID id);

}
