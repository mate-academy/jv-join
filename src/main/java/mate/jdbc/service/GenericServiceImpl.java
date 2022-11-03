package mate.jdbc.service;

import java.util.List;
import mate.jdbc.dao.GenericDao;

public abstract class GenericServiceImpl<T> implements GenericService<T> {

    @Override
    public T create(T driver) {
        return getDao().create(driver);
    }

    @Override
    public List<T> getAll() {
        return getDao().getAll();
    }

    @Override
    public T update(T driver) {
        return getDao().update(driver);
    }

    @Override
    public boolean delete(Long id) {
        return getDao().delete(id);
    }

    protected abstract GenericDao<T> getDao();
}
