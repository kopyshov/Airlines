package dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface DAO<Entity, Key> {
    void save(Entity entity) throws SQLException;
    void delete(Entity entity) throws SQLException;
    Optional<Entity> getByCode(Key code) throws SQLException;
    List<Entity> getAll() throws SQLException;
}
