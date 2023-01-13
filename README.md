# HW 04
### Create class `Car`

`  import java.util.List;

public class Car {
private Long id;
private String model;
private Manufacturer manufacturer;
private List<Driver> drivers;
}`


### Create `CarService` interface with implementation.

### CarService methods:
    - Car create(Car car);
    - Car get(Long id);
    - List<Car> getAll();
    - Car update(Car car);
    - boolean delete(Long id);
    - List<Car> getAllByDriver(Long driverId);
Do not implement following methods in DAO layer, only in service layer. Use update method from DAO.
  🔥These methods should be only on the service layer:🔥
    
    - void addDriverToCar(Driver driver, Car car);
    - void removeDriverFromCar(Driver driver, Car car);
    

- Test your solution in the `main` method.
- Don't forget about a table's and columns' namings.
-  

__Before submitting solution make sure you checked it first with__ [checklist](https://mate-academy.github.io/jv-program-common-mistakes/java-JDBC/join/Joins_checklist.html)

![diagram](taxi_models_diagram.jpeg)

![diagram](join-db-diagram.png)

