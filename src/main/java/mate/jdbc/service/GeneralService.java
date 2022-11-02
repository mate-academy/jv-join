package mate.jdbc.service;

import java.util.List;

public interface GeneralService<T> {
    T create(T o);

    T get(Long id);

    List<T> getAll();

    T update(T o);

    boolean delete(Long id);
}
