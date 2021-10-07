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
        String query = "INSERT INTO cars (model, manufacturer_id) VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement saveCarStatement = connection.prepareStatement(query,
                        Statement.RETURN_GENERATED_KEYS)) {
            saveCarStatement.setString(1, car.getModel());
            saveCarStatement.setLong(2, car.getManufacturer().getId());
            saveCarStatement.executeUpdate();
            ResultSet resultSet = saveCarStatement.getGeneratedKeys();
            if (resultSet.next()) {
                car.setId(resultSet.getObject(1, Long.class));
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't create "
                    + car + " .", throwable);
        }
        insertDrivers(car);
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        String selectQuery = "select manufacturers.country as country, cars.id "
                + "as car_id, cars.model "
                + "as car_model, manufacturers.name as manufacturer,"
                + " manufacturers.id as manufacturer_id from cars join manufacturers "
                + "ON cars.manufacturer_id = manufacturers.id where cars.id = ? "
                + "and cars.is_deleted = false";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getCarStatement = connection.prepareStatement(selectQuery)) {
            getCarStatement.setLong(1, id);
            ResultSet resultSet = getCarStatement.executeQuery();
            if (resultSet.next()) {
                car = getCarWithManufacturer(resultSet);
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't get car by id " + id, throwable);
        }
        if (car != null) {
            car.setDrivers(getDriversForCar(id));
        }
        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
        ResultSet resultSet;
        String selectQuery = "select manufacturers.country as country, cars.id "
                + "as car_id, cars.model as car_model, manufacturers.name "
                + "as manufacturer,"
                + " manufacturers.id as manufacturer_id "
                + "from cars join manufacturers ON cars.manufacturer_id = manufacturers.id "
                + "where cars.is_deleted = false";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getCarStatement = connection.prepareStatement(selectQuery)) {
            resultSet = getCarStatement.executeQuery();
            while (resultSet.next()) {
                cars.add(getCarWithManufacturer(resultSet));
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Can`t get cars list ", throwable);
        }
        for (Car car : carList) {
            car.setDrivers(getDriversForCar(car.getId()));
        }
        return carList;
    }

    @Override
    public Car update(Car car) {
        String query = "UPDATE cars SET model = ?, manufacturer_id = ?"
                + "where id = ? and is_deleted = false";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement updateCarStatement
                        = connection.prepareStatement(query)) {
            updateCarStatement.setString(1, car.getModel());
            updateCarStatement.setLong(2, car.getManufacturer().getId());
            updateCarStatement.setLong(3, car.getId());
            updateCarStatement.executeUpdate();
        } catch (SQLException throwable) {
            throw new DataProcessingException("Can`t update "
                    + car + " in DB.", throwable);
        }
        deleteDriversFromCar(car.getId());
        insertDrivers(car);
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String query = "UPDATE cars SET is_deleted = TRUE WHERE id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteCarStatement = connection.prepareStatement(query)) {
            deleteCarStatement.setLong(1, id);
            return deleteCarStatement.executeUpdate() > 0;
        } catch (SQLException throwable) {
            throw new DataProcessingException("Can`t delete car with id " + id, throwable);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        List<Car> carList = new ArrayList<>();
        String getCarsByDriverQuery = "select id, model, manufacturer_id "
                + "from cars as c JOIN cars_drivers cd ON c.id = cd.car_id "
                + "where cd.driver_id = ? and c.is_deleted = false";

        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getCarsByDriverStatement
                        = connection.prepareStatement(getCarsByDriverQuery)) {
            getCarsByDriverStatement.setLong(1, driverId);
            ResultSet resultSet = getCarsByDriverStatement.executeQuery();
            while (resultSet.next()) {
                Long id = resultSet.getObject("id", Long.class);
                Car car = get(id).get();
                carList.add(car);
            }
            return carList;
        } catch (SQLException throwable) {
            throw new DataProcessingException("Can't get a car by a driver id: "
                    + carList, throwable);
        }
        for (Car car : carList) {
            car.setDrivers(getDriversForCar(car.getId()));
        }
        return carList;
    }

    private Car getCarWithManufacturer(ResultSet resultSet) throws SQLException {
        Car car = new Car();
        Long id = resultSet.getObject("car_id", Long.class);
        car.setId(id);
        car.setModel(resultSet.getString("car_model"));
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setId(resultSet.getObject("manufacturer_id", Long.class));
        manufacturer.setName(resultSet.getString("manufacturer"));
        manufacturer.setCountry(resultSet.getString("country"));
        car.setManufacturer(manufacturer);
        return car;
    }

    private List<Driver> getDriversForCar(Long carId) {
        String getAllCarDrivers = "select id, name, license_number "
                + "from drivers as d JOIN cars_drivers cd "
                + "ON d.id = cd.driver_id where cd.car_id = ?";
        List<Driver> driverList;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getDriversForCarStatement
                        = connection.prepareStatement(getAllCarDrivers)) {
            getDriversForCarStatement.setLong(1, carId);
            ResultSet resultSet = getDriversForCarStatement.executeQuery();
            driverList = new ArrayList<>();
            while (resultSet.next()) {
                driverList.add(getDriverFromResultSet(resultSet));
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Can`t get drivers for a car with id:" + carId);
        }
        return driverList;
    }

    private Driver getDriverFromResultSet(ResultSet resultSet) throws SQLException {
        Driver driver = new Driver();
        Long id = resultSet.getObject("id", Long.class);
        String name = resultSet.getString("name");
        String licenseNumber = resultSet.getString("license_number");
        driver.setId(id);
        driver.setLicenseNumber(licenseNumber);
        driver.setName(name);
        return driver;
    }

    private void deleteDriversFromCar(Long carId) {
        String deleteQuery = "delete from cars_drivers where car_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery)) {
            deleteStatement.setLong(1, carId);
        } catch (SQLException throwable) {
            throw new DataProcessingException("Can't delete "
                    + "drivers with id: " + carId, throwable);
        }
    }

    private void insertDrivers(Car car) {
        String insertDrivers = "insert into  cars_drivers(car_id, driver_id) values(?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement insertDriverToCarStatement
                        = connection.prepareStatement(insertDrivers)) {
            insertDriverToCarStatement.setLong(1, car.getId());
            for (Driver driver : car.getDrivers()) {
                insertDriverToCarStatement.setLong(2, driver.getId());
                insertDriverToCarStatement.executeUpdate();
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Can't insert drivers into a car" + car, throwable);
        }
    }
}
