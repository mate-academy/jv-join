package mate.jdbc.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import mate.jdbc.exception.DataProcessingException;
import mate.jdbc.lib.Dao;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.util.ConnectionUtil;

@Dao
public class CarDaoImpl implements CarDao {

    @Override
    public Car create(Car car) {
        String createCarQuery = "INSERT INTO cars (model, manufacturer_id) VALUES (?, ?)";
        try (
                Connection connection = ConnectionUtil.getConnection();
                PreparedStatement createCarStatement = connection.prepareStatement(createCarQuery,
                        Statement.RETURN_GENERATED_KEYS)
        ) {
            createCarStatement.setString(1, car.getModel());
            createCarStatement.setLong(2, car.getManufacturer().getId());
            createCarStatement.execute();
            ResultSet resultSet = createCarStatement.getGeneratedKeys();
            if (resultSet.next()) {
                car.setId(resultSet.getObject(1, Long.class));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't create car: "
                    + car, e);
        }
        creteCarRelations(car);
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        String query = "SELECT "
                + "cars.id AS car_id, "
                + "cars.model AS car_model, "
                + "m.id AS manufacturer_id, "
                + "m.name AS manufacturer_name, "
                + "m.country AS manufacturer_country "
                + "FROM cars "
                + "JOIN manufacturers m "
                + "ON cars.manufacturer_id = m.id "
                + "WHERE cars.id = ? AND cars.is_deleted = FALSE AND m.is_deleted = FALSE;";
        Car car = null;
        try (
                Connection connection = ConnectionUtil.getConnection();
                PreparedStatement readStatement = connection.prepareStatement(query)
        ) {
            readStatement.setLong(1, id);
            ResultSet resultSet = readStatement.executeQuery();
            if (resultSet.next()) {
                car = parseCar(resultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get car by id " + id, e);
        }
        if (car != null) {
            car.setDrivers(getDriversOfCar(id));
        }
        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
        String query = "SELECT "
                + "cars.id AS car_id, "
                + "cars.model AS car_model, "
                + "m.id AS manufacturer_id, "
                + "m.name AS manufacturer_name, "
                + "m.country AS manufacturer_country "
                + "FROM cars "
                + "JOIN manufacturers m "
                + "ON cars.manufacturer_id = m.id "
                + "WHERE cars.is_deleted = FALSE AND m.is_deleted = FALSE;";
        List<Car> cars = new ArrayList<>();
        try (
                Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)
        ) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                cars.add(parseCar(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get a list of cars from cars table.", e);
        }
        for (Car c : cars) {
            c.setDrivers(getDriversOfCar(c.getId()));
        }
        return cars;
    }

    @Override
    public Car update(Car car) {
        String updateCarQuery =
                "UPDATE cars "
                        + "SET model = ?, manufacturer_id = ? "
                        + "WHERE id = ? AND is_deleted = FALSE";
        String deleteRelationsQuery =
                "DELETE FROM cars_drivers "
                        + "where car_id = ?;";
        try (
                Connection connection = ConnectionUtil.getConnection();
                PreparedStatement updateCarStatement = connection.prepareStatement(updateCarQuery);
                PreparedStatement deleteRelationsStatement
                        = connection.prepareStatement(deleteRelationsQuery)
        ) {
            updateCarStatement.setString(1, car.getModel());
            updateCarStatement.setLong(2, car.getManufacturer().getId());
            updateCarStatement.setLong(3, car.getId());
            updateCarStatement.executeUpdate();

            deleteRelationsStatement.setLong(1, car.getId());
            deleteRelationsStatement.execute();
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't update "
                    + car, e);
        }
        creteCarRelations(car);
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String query = "UPDATE cars SET is_deleted = TRUE WHERE id = ?";
        try (
                Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)
        ) {
            statement.setLong(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't delete car with id " + id, e);
        }
    }

    public List<Car> getCarsByDriver(Long driverId) {
        String getByDriverQuery = "SELECT "
                + "cars.id AS car_id, "
                + "cars.model AS car_model, "
                + "m.id AS manufacturer_id, "
                + "m.name AS manufacturer_name, "
                + "m.country AS manufacturer_country "
                + "FROM cars "
                + "JOIN manufacturers m "
                + "ON cars.manufacturer_id = m.id "
                + "JOIN cars_drivers cd "
                + "ON cars.id = cd.car_id "
                + "WHERE cars.is_deleted = FALSE AND m.is_deleted = FALSE AND cd.driver_id = ?;";
        List<Car> cars = new ArrayList<>();
        try (
                Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(getByDriverQuery)
        ) {
            statement.setLong(1, driverId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                cars.add(parseCar(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get cars by driver id = " + driverId, e);
        }
        for (Car car : cars) {
            car.setDrivers(getDriversOfCar(car.getId()));
        }
        return cars;
    }

    private Car parseCar(ResultSet resultSet) throws SQLException {
        return new Car(
                resultSet.getObject("car_id", Long.class),
                resultSet.getString("car_model"),
                new Manufacturer(
                        resultSet.getObject("manufacturer_id", Long.class),
                        resultSet.getString("manufacturer_name"),
                        resultSet.getString("manufacturer_country")
                        )
        );
    }

    private List<Driver> getDriversOfCar(Long id) {
        String query = "SELECT id, name, license_number "
                + "FROM drivers "
                + "JOIN cars_drivers cd "
                + "ON cd.driver_id = drivers.id "
                + "WHERE cd.car_id = ? AND drivers.is_deleted = FALSE;";
        try (
                Connection connection = ConnectionUtil.getConnection();
                PreparedStatement joinStatement = connection.prepareStatement(query)
        ) {
            joinStatement.setLong(1, id);
            ResultSet resultSet = joinStatement.executeQuery();
            List<Driver> drivers = new ArrayList<>();
            while (resultSet.next()) {
                drivers.add(new Driver(
                        resultSet.getObject(1, Long.class),
                        resultSet.getString(2),
                        resultSet.getString(3)
                ));
            }
            return drivers;
        } catch (SQLException e) {
            throw new DataProcessingException("cant get drivers for car, id = " + id, e);
        }
    }

    private void creteCarRelations(Car car) {
        String createRelationsQuery =
                "INSERT INTO cars_drivers (driver_id, car_id) "
                + "VALUES (?, ?)";
        try (
                Connection connection = ConnectionUtil.getConnection();
                PreparedStatement createRelationStatement =
                        connection.prepareStatement(createRelationsQuery)
        ) {
            for (Driver driver : car.getDrivers()) {
                createRelationStatement.setLong(1, driver.getId());
                createRelationStatement.setLong(2,car.getId());
                createRelationStatement.execute();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("cant create car-drivers relation for car:" + car, e);
        }
    }
}
