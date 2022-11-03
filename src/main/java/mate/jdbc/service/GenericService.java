package mate.jdbc.service;

import java.util.List;

public interface GenericService<T> {
    T create(T driver);

    T get(Long id);

    List<T> getAll();

    T update(T driver);

    boolean delete(Long id);
}
