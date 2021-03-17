# HW 04
- Create `CarDao` and `DriverDao` interfaces, and their implementations based on Storage.
  As an example use ManufacturerDao.

- Create `CarService` and `DriverService` interfaces, and their implementations.

### CarService methods:
    - Car create(Car car);
    - Car get(Long id);
    - List<Car> getAll();
    - Car update(Car car);
    - boolean delete(Long id);
    - void addDriverToCar(Driver driver, Car car);
    - void removeDriverFromCar(Driver driver, Car car);
    - List<Car> getAllByDriver(Long driverId);

### DriverService methods:
    - Driver create(Driver driver);
    - Driver get(Long id);
    - List<Driver> getAll();
    - Driver update(Driver driver);
    - boolean delete(Long id);

- In the `main` method create instances of ALL your services and call their CRUD methods.

__Before submitting solution make sure you checked it first with__ [checklist](../checklist/04_MySQL_checklist.md)

