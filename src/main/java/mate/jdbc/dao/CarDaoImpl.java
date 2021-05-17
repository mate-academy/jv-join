package mate.jdbc.dao;

import mate.jdbc.lib.Dao;
import mate.jdbc.lib.exception.DataProcessingException;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.util.ConnectionUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Dao
public class CarDaoImpl implements CarDao {
    @Override
    public Car create(Car car) {
        String createCarQuery = "INSERT INTO cars (model, manufacturer_id) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement createCarStatement = connection
                        .prepareStatement(createCarQuery, Statement.RETURN_GENERATED_KEYS)) {
            createCarStatement.setString(1, car.getModel());
            createCarStatement.setLong(2, car.getManufacturer().getId());
            createCarStatement.executeUpdate();
            ResultSet generatedKey = createCarStatement.getGeneratedKeys();
            if (generatedKey.next()) {
                car.setId(generatedKey.getObject(1, Long.class));
            }
            insertDrivers(car);
            return car;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't create Car object from input data: " + car, e);
        }
    }

    @Override
    public Optional<Car> get(Long id) {
        String getCarQuery = "SELECT cars.id AS car_id, model, manufacturers.id AS manufacturer_id,"
                + " manufacturers.name AS manufacturer_name, manufacturers.country "
                + "AS manufacturer_country FROM cars JOIN manufacturers "
                + "ON cars.manufacturer_id = manufacturers.id WHERE cars.id = ? AND cars.deleted = FALSE;";
        Car currentCar = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getCarStatement = connection.prepareStatement(getCarQuery)) {
            getCarStatement.setLong(1, id);
            ResultSet dataFromDB = getCarStatement.executeQuery();
            if (dataFromDB.next()) {
                currentCar = parseCarByDataFromDB(dataFromDB);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get Car object with input ID: " + id, e);
        }
        if (currentCar != null) {
            currentCar.setDrivers(getDriversForCurrentCar(id));
        }
        return Optional.ofNullable(currentCar);
    }

    @Override
    public List<Car> getAll() {
        String getAllCarsQuery = "SELECT cars.id AS car_id, model, manufacturers.id AS manufacturer_id,"
                + " manufacturers.name AS manufacturer_name, manufacturers.country "
                + "AS manufacturer_country FROM cars JOIN manufacturers "
                + "ON cars.manufacturer_id = manufacturers.id WHERE cars.deleted = FALSE;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                Statement getAllCarsStatement = connection.createStatement()) {
            ResultSet dataFromDB = getAllCarsStatement.executeQuery(getAllCarsQuery);
            while (dataFromDB.next()) {
                cars.add(parseCarByDataFromDB(dataFromDB));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't select all cars from DB", e);
        }
        for (Car car : cars) {
            car.setDrivers(getDriversForCurrentCar(car.getId()));
        }
        return cars;
    }

    @Override
    public Car update(Car car) {
        return null;
    }

    @Override
    public boolean delete(Long id) {
        String deleteCarQuery = "UPDATE cars SET deleted = TRUE WHERE id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteCarStatement = connection
                        .prepareStatement(deleteCarQuery)) {
            deleteCarStatement.setLong(1, id);
            deleteCarStatement.executeUpdate();
            return deleteCarStatement.executeUpdate() >= 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't delete data by input ID: " + id, e);
        }
    }

    private void insertDrivers(Car car) {
        String insertDriversQuery = "INSERT INTO cars_drivers (cars_id, drivers_id) VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement insertDriversStatement = connection.prepareStatement(insertDriversQuery)) {
            insertDriversStatement.setLong(1, car.getId());
            for (Driver driver : car.getDrivers()) {
                insertDriversStatement.setLong(2, driver.getId());
                insertDriversStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't insert Drivers from input data: " + car, e);
        }
    }

    private Car parseCarByDataFromDB(ResultSet dataFromDB) {
        try {
            Manufacturer currentManufacturer = new Manufacturer(dataFromDB
                    .getString("manufacturer_name"),
                    dataFromDB.getString("manufacturer_country"));
            currentManufacturer.setId(dataFromDB.getObject("manufacturer_id", Long.class));

            Car currentCar = new Car(dataFromDB.getString("model"), currentManufacturer);
            currentCar.setId(dataFromDB.getObject("car_id", Long.class));
            return currentCar;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't parse Car object from input data: "
                    + dataFromDB, e);
        }
    }

    private List<Driver> getDriversForCurrentCar(Long carID) {
        String getDriversQuery = "SELECT drivers.id AS driver_id, drivers.name AS driver_name, "
                + "drivers.license_number AS driver_license_number FROM drivers JOIN cars_drivers "
                + "ON drivers.id = cars_drivers.drivers_id WHERE cars_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement getDriversStatement = connection.prepareStatement(getDriversQuery)) {
            getDriversStatement.setLong(1, carID);
            ResultSet dataFromDB = getDriversStatement.executeQuery();
            List<Driver> driversList = new ArrayList<>();
            while (dataFromDB.next()) {
                driversList.add(parseDriverByDataFromDB(dataFromDB));
            }
            return driversList;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get Drivers list from by input ID: ", e);
        }
    }

    private Driver parseDriverByDataFromDB(ResultSet dataFromDB) {
        Driver currentDriver;
        try {
            currentDriver = new Driver(dataFromDB.getString("driver_name"),
                    dataFromDB.getString("driver_license_number"));
            currentDriver.setId(dataFromDB.getObject("driver_id", Long.class));
            return currentDriver;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't parse Driver object from input data: "
                    + dataFromDB, e);
        }
    }
}
