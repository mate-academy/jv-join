package mate.jdbc.service;

import java.util.List;

public interface GenericService<T> {
    T create(T elem);

    T get(Long id);

    List<T> getAll();

    T update(T elem);

    boolean delete(Long id);
}
