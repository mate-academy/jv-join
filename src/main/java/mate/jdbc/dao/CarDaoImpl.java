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
import mate.jdbc.util.ConnectionUtil;

@Dao
public class CarDaoImpl implements CarDao {
    @Override
    public Car create(Car car) {
        String query = "insert into cars (model,manufactured_id) values (?,?)";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement saveCarStatement = connection.prepareStatement(query,
                         Statement.RETURN_GENERATED_KEYS)) {
            saveCarStatement.setString(1, car.getModel());
            saveCarStatement.setLong(2, car.getManufactured_id());
            saveCarStatement.executeUpdate();
            ResultSet resultSet = saveCarStatement.getGeneratedKeys();
            if (resultSet.next()) {
                car.setId(resultSet.getObject(1, Long.class));
            }
            return car;
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't create "
                    + car + ". ", throwable);
        }
    }

    @Override
    public Optional<Car> get(Long id) {
        Car car = null;
        String query = "select c.id as id, name, model, manufactured_id "
                + "from cars as c join manufacturers as m "
                + "on c.manufactured_id = m.id "
                + "where c.id = ? and c.is_deleted = false";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement getCarrStatement = connection.prepareStatement(query)) {
            getCarrStatement.setLong(1, id);
            ResultSet resultSet = getCarrStatement.executeQuery();
            if (resultSet.next()) {
                car = getCar(resultSet);
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't get car by id " + id, throwable);
        }
        if (car != null) {
            car.setDrivers(getDrivers(car));
        }
        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
        String query = "select c.id as id, name, model, manufactured_id "
                + "from cars as c join manufacturers as m "
                + "on c.manufactured_id = m.id "
                + "where c.is_deleted = false";
        List<Car> cars = new ArrayList<>();
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement getAllCarsStatement = connection.prepareStatement(query)) {
            ResultSet resultSet = getAllCarsStatement.executeQuery();
            while (resultSet.next()) {
                car = getCar(resultSet);
                cars.add(car);
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't get a list of cars from carsDB.",
                    throwable);
        }
        cars.forEach(c -> c.setDrivers(getDrivers(c)));
        return cars;
    }

    @Override
    public Car update(Car car) {
        String query = "update cars "
                + "set model = ?, manufactured_id = ? "
                + "where id = ? and is_deleted = false";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement updateDriverStatement
                         = connection.prepareStatement(query)) {
            updateDriverStatement.setString(1, car.getModel());
            updateDriverStatement.setLong(2, car.getManufactured_id());
            updateDriverStatement.setLong(3, car.getId());
            updateDriverStatement.executeUpdate();
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't update "
                    + car + " in carsDB.", throwable);
        }
        deleteOldRelations(car);
        List<Driver> drivers = car.getDrivers();
        if (drivers != null) {
            for (Driver driver:drivers) {
                createNewRelation(car,driver);
            }
        }
        car.setDrivers(getDrivers(car));
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String query = "update cars set is_deleted = true where id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement deleteCarStatement = connection.prepareStatement(query)) {
            deleteCarStatement.setLong(1, id);
            return deleteCarStatement.executeUpdate() > 0;
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't delete car with id " + id, throwable);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String query = "select id, model, manufactured_id FROM cars as c "
                + "join cars_drivers as cd on c.id = cd.id_car where "
                + "cd.id_driver = ? and c.is_deleted = false;";
        List<Car> cars = new ArrayList<>();
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement getAllCarsStatement = connection.prepareStatement(query)) {
            getAllCarsStatement.setLong(1,driverId);
            ResultSet resultSet = getAllCarsStatement.executeQuery();
            while (resultSet.next()) {
                car = getCar(resultSet);
                cars.add(car);
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't get a list of cars from carsDB.",
                    throwable);
        }
        cars.forEach(c -> c.setDrivers(getDrivers(c)));
        return cars;
    }

    private List<Driver> getDrivers(Car car) {
        List<Driver> drivers = new ArrayList<>();
        String query = "select * from drivers as d "
                + "join cars_drivers as cd on d.id = cd.id_driver "
                + "where cd.id_car = ? and cd.is_deleted = false;";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement getDriversByCar = connection.prepareStatement(query)) {
            getDriversByCar.setLong(1, car.getId());
            ResultSet resultSet = getDriversByCar.executeQuery();
            while (resultSet.next()) {
                drivers.add(getDriver(resultSet));
            }
            return drivers;
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't get a list of drivers from driverDB.",
                    throwable);
        }
    }

    private Car getCar(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getObject("id", Long.class);
        String model = resultSet.getString("model");
        Long manufacturedId = resultSet.getObject(4,Long.class);
        Car car = new Car();
        car.setModel(model);
        car.setManufactured_id(manufacturedId);
        car.setId(id);
        return car;
    }

    private Driver getDriver(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getObject("id", Long.class);
        String name = resultSet.getString("name");
        String licenseNumber = resultSet.getString("license_Number");
        Driver driver = new Driver();
        driver.setName(name);
        driver.setLicenseNumber(licenseNumber);
        driver.setId(id);
        return driver;
    }

    private void deleteOldRelations(Car car) {
        String query = "update cars_drivers "
                + "set is_deleted = true "
                + "where id_car = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement deleteCarsDriversStatement = connection
                         .prepareStatement(query)) {
            deleteCarsDriversStatement.setLong(1, car.getId());
            deleteCarsDriversStatement.executeUpdate();
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't delete relation from cars_driversDB.",
                    throwable);
        }
    }

    private void createNewRelation(Car car, Driver driver) {
        String query = "insert into cars_drivers(id_car,id_driver) "
                + "values(?,?);";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement createCarsDriversStatement = connection
                         .prepareStatement(query)) {
            createCarsDriversStatement.setLong(1, car.getId());
            createCarsDriversStatement.setLong(2, driver.getId());

            createCarsDriversStatement.executeUpdate();
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't create new relation to cars_driversDB.",
                    throwable);
        }
    }
}
