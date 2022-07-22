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
        String insertCarRequest = "INSERT cars(model, manufacturer_id) VALUES (?,?)";
        String insertDriversRequest = "INSERT cars_drivers VALUES (?,?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement createCarStatement = connection.prepareStatement(
                        insertCarRequest, Statement.RETURN_GENERATED_KEYS);
                PreparedStatement insertDriversStatement =
                        connection.prepareStatement(insertDriversRequest)) {
            createCarStatement.setString(1, car.getModel());
            createCarStatement.setLong(2, car.getManufacturer().getId());
            createCarStatement.executeUpdate();
            ResultSet generatedKeys = createCarStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                Long carId = generatedKeys.getObject(1, Long.class);
                car.setId(carId);
                insertDriversStatement.setLong(1, carId);
                for (Driver driver : car.getDrivers()) {
                    insertDriversStatement.setLong(2, driver.getId());
                    insertDriversStatement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t create car " + car, e);
        }
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        String getCarRequest = "SELECT cars.id, model, manufacturer_id, name, country "
                + "FROM cars INNER JOIN manufacturers ON manufacturer_id = manufacturers.id "
                + "WHERE cars.id = ? AND cars.is_deleted = 'false'";
        String getDriversRequest = "SELECT driver_id, name, licence_number "
                + "FROM cars_drivers INNER JOIN drivers ON driver_id = drivers.id "
                + "WHERE car_id = ? AND drivers.is_deleted = 'false'";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getCarStatement = connection.prepareStatement(getCarRequest);
                PreparedStatement getDriversStatement
                        = connection.prepareStatement(getDriversRequest)) {
            getCarStatement.setLong(1, id);
            ResultSet carResultSet = getCarStatement.executeQuery();
            if (carResultSet.next()) {
                getDriversStatement.setLong(1, id);
                ResultSet driversResultSet = getDriversStatement.executeQuery();
                return Optional.of(getCar(carResultSet, driversResultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t get car from DB by id" + id, e);
        }
        return Optional.empty();
    }

    @Override
    public List<Car> getAll() {
        List<Car> cars = new ArrayList<>();
        String getAllCarsRequest = "SELECT cars.id, model, manufacturer_id, name, country "
                + "FROM cars INNER JOIN manufacturers ON manufacturer_id = manufacturers.id "
                + "WHERE cars.is_deleted = 'false'";
        String getDriversRequest = "SELECT driver_id, name, licence_number "
                + "FROM cars_drivers INNER JOIN drivers ON driver_id = drivers.id "
                + "WHERE car_id = ? AND drivers.is_deleted = 'false'";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllCarsStatement
                        = connection.prepareStatement(getAllCarsRequest);
                PreparedStatement getDriversStatement
                        = connection.prepareStatement(getDriversRequest)) {
            ResultSet carsResultSet = getAllCarsStatement.executeQuery();
            while (carsResultSet.next()) {
                getDriversStatement.setLong(1, carsResultSet.getLong(1));
                ResultSet carDriversResultSet = getDriversStatement.executeQuery();
                cars.add(getCar(carsResultSet, carDriversResultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t get all cars ", e);
        }
        return cars;
    }

    @Override
    public Car update(Car car) {
        String updateCarRequest = "UPDATE cars SET model = ?, manufacturer_id = ? "
                + "WHERE id = ? AND is_deleted = 'false'";
        String deleteDriversRequest = "DELETE FROM cars_drivers WHERE car_id = ?";
        String insertDriversRequest = "INSERT cars_drivers VALUES (?,?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement updateCarStatement
                        = connection.prepareStatement(updateCarRequest);
                PreparedStatement deleteDriversStatement
                        = connection.prepareStatement(deleteDriversRequest);
                PreparedStatement insertDriversStatement
                        = connection.prepareStatement(insertDriversRequest)) {
            updateCarStatement.setString(1, car.getModel());
            updateCarStatement.setLong(2, car.getManufacturer().getId());
            updateCarStatement.setLong(3, car.getId());
            if (updateCarStatement.executeUpdate() > 0) {
                deleteDriversStatement.setLong(1, car.getId());
                deleteDriversStatement.executeUpdate();
                insertDriversStatement.setLong(1, car.getId());
                for (Driver driver : car.getDrivers()) {
                    insertDriversStatement.setLong(2, driver.getId());
                    insertDriversStatement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t update car " + car, e);
        }
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String deleteRequest = "UPDATE cars SET is_deleted = 'true' WHERE id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteStatement = connection.prepareStatement(deleteRequest)) {
            deleteStatement.setLong(1, id);
            return deleteStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t delete car by id" + id, e);
        }
    }

    public List<Car> getAllByDriver(Long driverId) {
        List<Car> cars = new ArrayList<>();
        String getAllByDriverRequest
                = "SELECT car_id, model, manufacturer_id, manufacturers.name, country "
                + "FROM cars_drivers INNER JOIN cars ON car_id = cars.id "
                + "INNER JOIN drivers ON driver_id = drivers.id "
                + "INNER JOIN manufacturers ON manufacturer_id = manufacturers.id "
                + "WHERE driver_id = ? "
                + "AND drivers.is_deleted = 'false' AND cars.is_deleted = 'false';";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllByDriverStatement =
                        connection.prepareStatement(getAllByDriverRequest)) {
            getAllByDriverStatement.setLong(1, driverId);
            ResultSet getAllByDriverResultSet = getAllByDriverStatement.executeQuery();
            while (getAllByDriverResultSet.next()) {
                cars.add(get(getAllByDriverResultSet.getLong("car_id")).get());
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t get all cars by driver id " + driverId, e);
        }
        return cars;
    }

    private Car getCar(ResultSet carSet, ResultSet driversSet) {
        try {
            Manufacturer manufacturer = new Manufacturer(carSet.getLong(1),
                    carSet.getString("name"),
                    carSet.getString("country"));
            List<Driver> drivers = new ArrayList<>();
            while (driversSet.next()) {
                Driver driver = new Driver(driversSet.getLong(1),
                        driversSet.getString("name"),
                        driversSet.getString("licence_number"));
                drivers.add(driver);
            }
            return new Car(carSet.getObject(1, Long.class),
                    carSet.getString("model"),
                    manufacturer, drivers);
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t get car from ResultSets ", e);
        }
    }
}
