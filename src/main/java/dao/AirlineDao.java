package dao;

import model.Airline;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AirlineDao implements DAO<Airline, String> {

    private final Connection connection;

    public AirlineDao(final Connection connection) {
        this.connection = connection;
    }

    public void save(Airline airline) throws SQLException {
        try(PreparedStatement statement = connection.prepareStatement(AirlineSQL.SAVE.QUERY)) {
            statement.setString(1, airline.getCode());
            statement.setString(2, airline.getName());
            statement.executeUpdate();
        }
    }

    @Override
    public void delete(Airline airline) throws SQLException {
        try(PreparedStatement statement = connection.prepareStatement(AirlineSQL.DELETE.QUERY)) {
            statement.setLong(1, airline.getId());
            statement.executeUpdate();
        }
    }

    public List<Airline> getAll() throws SQLException {
        final List<Airline> result = new ArrayList<>();
        try(PreparedStatement statement = connection.prepareStatement(AirlineSQL.GET_ALL.QUERY)) {
            final ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()) {
                Airline airline = new Airline();
                airline.setId(resultSet.getLong(1));
                airline.setCode(resultSet.getString(2));
                airline.setName(resultSet.getString(3));
                result.add(airline);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    public Optional<Airline> getByCode(String code) throws SQLException {
        final Airline result = new Airline();
        result.setCode(code);
        try(PreparedStatement statement = connection.prepareStatement(AirlineSQL.GET_BY_CODE.QUERY)) {
            statement.setString(1, code);
            final ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) {
                result.setId(resultSet.getLong(1));
                result.setCode(resultSet.getString(2));
                result.setName(resultSet.getString(3));
            }  else {
                return Optional.empty();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.of(result);
    }

    enum AirlineSQL {
        GET_ALL("SELECT * FROM Airline"),
        GET_BY_CODE("SELECT * FROM Airline WHERE code = (?)"),
        SAVE("INSERT INTO Airline (code, name) VALUES (?, ?)"),
        DELETE("UPDATE Airline SET availability = 0 WHERE id = (?)");
        final String QUERY;
        AirlineSQL(String QUERY) {
            this.QUERY = QUERY;
        }
    }
}
