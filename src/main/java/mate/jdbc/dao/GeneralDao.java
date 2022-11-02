package mate.jdbc.dao;

import java.util.List;
import java.util.Optional;

public interface GeneralDao<T> {
    T create(T driver);

    Optional<T> get(Long id);

    List<T> getAll();

    T update(T driver);

    boolean delete(Long id);
}
