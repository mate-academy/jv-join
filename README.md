# HW 04
- Establish connection to your Database.

- Create `ManufacturerService` interface, and its implementation.

- Create `CarDao` and `DriverDao` interfaces, and their implementations.

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

__Before submitting solution make sure you checked it first with__ [checklist](https://mate-academy.github.io/jv-program-common-mistakes/java-JDBC/join/Joins_checklist.html)

