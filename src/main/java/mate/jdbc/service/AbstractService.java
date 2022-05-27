package mate.jdbc.service;

import java.util.List;

public interface AbstractService<T> {
    T create(T element);

    T get(Long id);

    List<T> getAll();

    T update(T element);

    boolean delete(Long id);
}
