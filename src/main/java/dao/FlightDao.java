package dao;

import model.Airline;
import model.Airport;
import model.Flight;
import org.sqlite.SQLiteException;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FlightDao {

    private final Connection connection;
    public FlightDao(final Connection connection) {
        this.connection = connection;
    }


    public void save(Long fromAirportId, Long toAirportId, Long airline, BigDecimal price) throws SQLException{
        try(PreparedStatement statement = connection.prepareStatement(FlightSQL.SAVE.QUERY)) {
            statement.setLong(1, fromAirportId);
            statement.setLong(2, toAirportId);
            statement.setLong(3, airline);
            statement.setBigDecimal(4, price);
            statement.executeUpdate();
        }
    }

    public void update(Long fromAirportId, Long toAirportId, Long airline, BigDecimal price) {
        try(PreparedStatement statement = connection.prepareStatement(FlightSQL.UPDATE.QUERY)) {
            statement.setLong(2, fromAirportId);
            statement.setLong(3, toAirportId);
            statement.setLong(4, airline);
            statement.setBigDecimal(1, price);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void delete(Long fromAirportId, Long toAirportId, Long airline) {
        try(PreparedStatement statement = connection.prepareStatement(FlightSQL.DELETE.QUERY)) {
            statement.setLong(1, fromAirportId);
            statement.setLong(2, toAirportId);
            statement.setLong(3, airline);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public List<Flight> getAll() throws SQLException {
        final List<Flight> result = new ArrayList<>();
        try(PreparedStatement statement = connection.prepareStatement(FlightSQL.GET_ALL.QUERY)) {
            final ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()) {
                Flight flight = getFlight(resultSet);
                result.add(flight);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public List<Flight> getNeighborsById(Long airportId) {
        final List<Flight> result = new ArrayList<>();
        try(PreparedStatement statement = connection.prepareStatement(FlightSQL.GET_NEIGHBORS_BY_ID.QUERY)) {
            statement.setLong(1, airportId);
            statement.setLong(2, airportId);
            final ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()) {
                Flight flight = getFlight(resultSet);
                result.add(flight);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public Flight getById(Long fromAirportId, Long toAirportId, Long airlineId) {
        Flight flight = null;
        try (PreparedStatement statement = connection.prepareStatement(FlightSQL.GET_BY_ID.QUERY)) {
            statement.setLong(1, fromAirportId);
            statement.setLong(2, toAirportId);
            statement.setLong(3, airlineId);
            final ResultSet resultSet = statement.executeQuery();

            flight = getFlight(resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return flight;
    }

    private Flight getFlight(ResultSet resultSet) throws SQLException {
        Airport fromAirport = new Airport(resultSet.getString(3), resultSet.getString(4));
        fromAirport.setId(resultSet.getLong(2));
        Airport toAirport = new Airport(resultSet.getString(6), resultSet.getString(7));
        toAirport.setId(resultSet.getLong(5));
        Airline airline = new Airline(resultSet.getString(9), resultSet.getString(10));
        airline.setId(resultSet.getLong(8));
        Flight flight = new Flight(
                resultSet.getLong(1),
                fromAirport,
                toAirport,
                airline,
                resultSet.getBigDecimal(11));
        return flight;
    }


    enum FlightSQL {
        GET_ALL("SELECT " +
                "Flight.id, " +
                "P1.id, P1.code, P1.name, " +
                "P2.id, P2.code, P2.name, " +
                "Line.id, Line.code, Line.name, " +
                "Flight.price " +
                "FROM Flight " +
                "JOIN Airport AS P1 on P1.id = Flight.from_airport_id " +
                "JOIN Airport AS P2 on P2.id = Flight.to_airport_id " +
                "JOIN Airline Line on Line.id = Flight.airline_id;"),
        GET_NEIGHBORS_BY_ID("SELECT Flight.id, P1.id, P1.code, P1.name, P2.id, P2.code, P2.name, Line.id, Line.code, Line.name, Flight.price \n" +
                "FROM Flight \n" +
                "JOIN Airport AS P1 ON P1.id = Flight.from_airport_id\n" +
                "JOIN Airport AS P2 ON P2.id = Flight.to_airport_id \n" +
                "JOIN Airline Line ON Line.id = Flight.airline_id\n" +
                "WHERE P1.id = (?) AND P2.id <> (?)"),
        GET_BY_ID("SELECT Flight.id, P1.id, P1.code, P1.name, P2.id, P2.code, P2.name, Line.id, Line.code, Line.name, Flight.price \n" +
                "FROM Flight \n" +
                "JOIN Airport AS P1 ON P1.id = Flight.from_airport_id\n" +
                "JOIN Airport AS P2 ON P2.id = Flight.to_airport_id \n" +
                "JOIN Airline Line ON Line.id = Flight.airline_id\n" +
                "WHERE P1.id = (?) AND P2.id = (?) AND Line.id = (?)"),
        SAVE("INSERT INTO Flight (from_airport_id, to_airport_id, airline_id, price) VALUES (?, ?, ?, ?)"),
        UPDATE("UPDATE Flight SET price = (?) WHERE from_airport_id = (?) AND to_airport_id = (?) AND airline_id = (?)"),
        DELETE("UPDATE Flight SET availability = 0 WHERE from_airport_id = (?) AND to_airport_id = (?) AND airline_id = (?)");


        final String QUERY;

        FlightSQL(String QUERY) {
            this.QUERY = QUERY;
        }
    }
}
