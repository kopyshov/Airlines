package dao;

import model.Flight;
import model.Airline;
import model.Airport;
import dto.FlightDto;

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


    public void save(Flight flight) throws SQLException{
        try(PreparedStatement statement = connection.prepareStatement(FlightSQL.SAVE.QUERY)) {
            statement.setLong(1, flight.fromAirportId());
            statement.setLong(2, flight.toAirportId());
            statement.setLong(3, flight.airline());
            statement.setBigDecimal(4, flight.price());
            statement.executeUpdate();
        }
    }
    public void update(Flight flight) {
        try(PreparedStatement statement = connection.prepareStatement(FlightSQL.UPDATE.QUERY)) {
            statement.setLong(2, flight.fromAirportId());
            statement.setLong(3, flight.toAirportId());
            statement.setLong(4, flight.airline());
            statement.setBigDecimal(1, flight.price());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public List<FlightDto> getAll() throws SQLException {
        final List<FlightDto> result = new ArrayList<>();
        try(PreparedStatement statement = connection.prepareStatement(FlightSQL.GET_ALL.QUERY)) {
            final ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()) {
                FlightDto flightDto = getFlight(resultSet);
                result.add(flightDto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public List<FlightDto> getNeighborsById(Long airportId) {
        final List<FlightDto> result = new ArrayList<>();
        try(PreparedStatement statement = connection.prepareStatement(FlightSQL.GET_NEIGHBORS_BY_ID.QUERY)) {
            statement.setLong(1, airportId);
            statement.setLong(2, airportId);
            final ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()) {
                FlightDto flightDto = getFlight(resultSet);
                result.add(flightDto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public FlightDto getById(Long fromAirportId, Long toAirportId, Long airlineId) {
        FlightDto flightDto = null;
        try (PreparedStatement statement = connection.prepareStatement(FlightSQL.GET_BY_ID.QUERY)) {
            statement.setLong(1, fromAirportId);
            statement.setLong(2, toAirportId);
            statement.setLong(3, airlineId);
            final ResultSet resultSet = statement.executeQuery();

            flightDto = getFlight(resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return flightDto;
    }

    private FlightDto getFlight(ResultSet resultSet) throws SQLException {
        Airport fromAirport = new Airport(resultSet.getString(3), resultSet.getString(4));
        fromAirport.setId(resultSet.getLong(2));
        Airport toAirport = new Airport(resultSet.getString(6), resultSet.getString(7));
        toAirport.setId(resultSet.getLong(5));
        Airline airline = new Airline(resultSet.getString(9), resultSet.getString(10));
        airline.setId(resultSet.getLong(8));
        FlightDto flightDto = new FlightDto(
                resultSet.getLong(1),
                fromAirport,
                toAirport,
                airline,
                resultSet.getBigDecimal(11));
        return flightDto;
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
