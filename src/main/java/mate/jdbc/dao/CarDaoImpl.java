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
    private static final String TABLE = "cars";
    private static final String COLUMN_MODEL = "model";
    private static final String COLUMN_MANUFACTURER_ID = "manufacturer_id";
    private static final String COLUMN_IS_DELETED = "is_deleted";

    @Override
    public Car create(Car car) {
        String queryCreateCar = "INSERT INTO " + TABLE + " ("
                + COLUMN_MODEL + ", " + COLUMN_MANUFACTURER_ID + ") VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement createCarStatement =
                         connection.prepareStatement(queryCreateCar,
                                 Statement.RETURN_GENERATED_KEYS)) {
            createCarStatement.setString(1, car.getModel());
            createCarStatement.setLong(2, car.getManufacturer().getId());
            createCarStatement.executeUpdate();
            ResultSet createCarResultSet = createCarStatement.getGeneratedKeys();
            if (createCarResultSet.next()) {
                car.setId(createCarResultSet.getObject(1, Long.class));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't create car " + car, e);
        }
        assignDriversToTheCar(car);
        return car;
    }

    @Override
    public Car update(Car car) {
        String queryUpdateCar = "UPDATE " + TABLE + " SET " + COLUMN_MODEL + " = ?, "
                + COLUMN_MANUFACTURER_ID + " = ? WHERE id = ? AND "
                + COLUMN_IS_DELETED + " = FALSE";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement updateCarStatement =
                        connection.prepareStatement(queryUpdateCar)) {
            updateCarStatement.setString(1, car.getModel());
            updateCarStatement.setLong(2, car.getManufacturer().getId());
            updateCarStatement.setLong(3, car.getId());
            updateCarStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can't update info for the car " + car, e);
        }
        updateDriversFor(car);
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String queryDeleteCar = "UPDATE " + TABLE + " SET "
                + COLUMN_IS_DELETED + " = TRUE WHERE id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteCarStatement =
                        connection.prepareStatement(queryDeleteCar)) {
            deleteCarStatement.setLong(1, id);
            return deleteCarStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't delete the Car with id = " + id, e);
        }
    }

    @Override
    public Optional<Car> get(Long id) {
        String queryGetCar =
                "SELECT cars.id AS id, model, manufacturer_id, name, country FROM cars "
                + "JOIN manufacturers ON cars.manufacturer_id = manufacturers.id"
                + " WHERE cars.id = ? AND cars.is_deleted = FALSE";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getCarStatement = connection.prepareStatement(queryGetCar)) {
            getCarStatement.setLong(1, id);
            ResultSet carResultSet = getCarStatement.executeQuery();
            if (carResultSet.next()) {
                car = getCar(carResultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get the car by id " + id, e);
        }

        if (car != null) {
            car.setDrivers(getAllDriversAssignedCar(car.getId()));
        }
        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
        String queryGetAllCars =
                "SELECT cars.id AS id, model, manufacturer_id, name, country FROM cars "
                + "JOIN manufacturers ON cars.manufacturer_id = manufacturers.id"
                + " WHERE cars.is_deleted = FALSE";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement allCarsStatement = connection.prepareStatement(queryGetAllCars)) {
            ResultSet carsResultSet = allCarsStatement.executeQuery();
            while (carsResultSet.next()) {
                cars.add(getCar(carsResultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get list of cars from the DB", e);
        }

        setDriversForCars(cars);
        return cars;
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String queryAllCarsByDriver =
                "SELECT cars.id, model, manufacturer_id, manufacturers.name, manufacturers.country "
                + "FROM cars JOIN cars_drivers ON cars.id = id_car JOIN manufacturers "
                + "ON manufacturer_id = manufacturers.id "
                + "WHERE id_driver = ? AND cars.is_deleted = FALSE";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement allCarsByDriverStatement =
                        connection.prepareStatement(queryAllCarsByDriver)) {
            allCarsByDriverStatement.setLong(1, driverId);
            ResultSet carsResultSet = allCarsByDriverStatement.executeQuery();
            while (carsResultSet.next()) {
                cars.add(getCar(carsResultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get cars by the driver with id "
                    + driverId, e);
        }

        setDriversForCars(cars);
        return cars;
    }

    private void assignDriversToTheCar(Car car) {
        String query = "INSERT INTO cars_drivers (id_car, id_driver) VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement createCarsDriversStatement =
                        connection.prepareStatement(query)) {
            for (Driver driver: car.getDrivers()) {
                createCarsDriversStatement.setLong(1, car.getId());
                createCarsDriversStatement.setLong(2, driver.getId());
                createCarsDriversStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't assign drivers for the car " + car, e);
        }
    }

    private void updateDriversFor(Car car) {
        String queryRemovePrevDrivers = "DELETE FROM cars_drivers WHERE id_car = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement removeDriversStatement =
                        connection.prepareStatement(queryRemovePrevDrivers)) {
            removeDriversStatement.setLong(1, car.getId());
            removeDriversStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can't update drivers for the car " + car, e);
        }
        assignDriversToTheCar(car);
    }

    private Car getCar(ResultSet carResultSet) throws SQLException {
        Long id = carResultSet.getObject("id", Long.class);
        String model = carResultSet.getString(COLUMN_MODEL);
        Long manufacturerId = carResultSet.getObject(COLUMN_MANUFACTURER_ID, Long.class);
        String nameManufacturer = carResultSet.getString("name");
        String countryManufacturer = carResultSet.getString("country");
        Manufacturer manufacturer =
                new Manufacturer(manufacturerId, nameManufacturer, countryManufacturer);
        return new Car(id, model, manufacturer, new ArrayList<>());
    }

    private List<Driver> getAllDriversAssignedCar(Long id) {
        String queryGetAllDriversAssignedCar =
                "SELECT drivers.id, drivers.name, drivers.license_number FROM cars "
                        + "JOIN cars_drivers ON cars.id = cars_drivers.id_car "
                        + "JOIN drivers ON cars_drivers.id_driver = drivers.id "
                        + "WHERE cars.id = ?";
        List<Driver> result = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllCarDriversStatement =
                        connection.prepareStatement(queryGetAllDriversAssignedCar)) {
            getAllCarDriversStatement.setLong(1, id);
            ResultSet driversIdResultSet = getAllCarDriversStatement.executeQuery();
            while (driversIdResultSet.next()) {
                Long driverId = driversIdResultSet.getLong("drivers.id");
                String driverName = driversIdResultSet.getString("drivers.name");
                String driverLicense = driversIdResultSet.getString("drivers.license_number");
                result.add(new Driver(driverId, driverName, driverLicense));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get drivers for the car by id " + id, e);
        }
        return result;
    }

    private void setDriversForCars(List<Car> cars) {
        for (Car car: cars) {
            car.setDrivers(getAllDriversAssignedCar(car.getId()));
        }
    }
}
