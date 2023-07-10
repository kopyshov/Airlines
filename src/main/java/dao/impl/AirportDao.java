package dao.impl;

import dao.IAirportDAO;
import model.Airport;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AirportDao implements IAirportDAO {

    private final Connection connection;

    public AirportDao(final Connection connection) {
        this.connection = connection;
    }

    public void save(Airport airport) throws SQLException {
        try(PreparedStatement statement = connection.prepareStatement(AirportSQL.SAVE.QUERY)) {
            statement.setString(1, airport.getCode());
            statement.setString(2, airport.getName());
            statement.executeUpdate();
        }
    }

    public List<Airport> getAll() throws SQLException {
        final List<Airport> result = new ArrayList<>();
        try(PreparedStatement statement = connection.prepareStatement(AirportSQL.GET_ALL.QUERY)) {
            final ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()) {
                Airport airport = new Airport();
                airport.setId(resultSet.getLong(1));
                airport.setCode(resultSet.getString(2));
                airport.setName(resultSet.getString(3));
                result.add(airport);
            }
            resultSet.close();
        }
        return result;
    }

    public Optional<Airport> getByCode(String code) throws SQLException {
        final Airport result = new Airport();
        result.setCode(code);
        try(PreparedStatement statement = connection.prepareStatement(AirportSQL.GET_BY_CODE.QUERY)) {
            statement.setString(1, code);
            final ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) {
                result.setId(resultSet.getLong(1));
                result.setCode(resultSet.getString(2));
                result.setName(resultSet.getString(3));
                resultSet.close();
            }  else {
                resultSet.close();
                return Optional.empty();
            }

        }
        return Optional.of(result);
    }


    enum AirportSQL {
        GET_ALL("SELECT * FROM Airport"),
        GET_BY_CODE("SELECT * FROM Airport WHERE code = (?)"),
        SAVE("INSERT INTO Airport (code, name) VALUES (?, ?)"),
        DELETE("UPDATE Airport SET availability = 0 WHERE id = (?)");
        final String QUERY;
        AirportSQL(String QUERY) {
            this.QUERY = QUERY;
        }
    }
}
