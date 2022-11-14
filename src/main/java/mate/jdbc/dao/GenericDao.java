package mate.jdbc.dao;

import java.util.List;
import java.util.Optional;

public interface GenericDao<T, R> {
    T create(T o);

    Optional<T> get(R id);

    List<T> getAll();

    T update(T o);

    boolean delete(R id);
}
