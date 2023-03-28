package mate.jdbc.service;

import java.util.List;

public interface GenericService<T> {
    T create(T entity);

    T get(Long id);

    List<T> getAll();

    T update(T entity);

    boolean delete(Long id);
}
