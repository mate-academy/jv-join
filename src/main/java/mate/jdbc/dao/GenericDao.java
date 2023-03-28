package mate.jdbc.dao;

import java.util.List;
import java.util.Optional;

public interface GenericDao<T> {
    T create(T entity);

    Optional<T> get(Long id);

    List<T> getAll();

    T update(T entity);

    boolean delete(Long id);
}
