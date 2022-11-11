package mate.jdbc.service;

import java.util.List;

public interface GenericService<T, R> {
    T create(T o);

    T get(R id);

    List<T> getAll();

    T update(T o);

    boolean delete(R id);
}
