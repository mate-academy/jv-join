package mate.jdbc;

import java.util.ArrayList;
import java.util.List;
import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.CarService;
import mate.jdbc.service.DriverService;
import mate.jdbc.service.ManufacturerService;

public class Main {
    private static final Injector injector = Injector.getInstance("mate.jdbc");
    private static final CarService carService =
            (CarService) injector.getInstance(CarService.class);
    private static final DriverService driverService =
            (DriverService) injector.getInstance(DriverService.class);
    private static final ManufacturerService manufacturerService =
            (ManufacturerService) injector.getInstance(ManufacturerService.class);
    private static final Long INDEX_FOR_TEST = 2L;

    public static void main(String[] args) {
        // Preparing for testing.
        Manufacturer bmw = new Manufacturer("Bmw", "Germany");
        Manufacturer infinity = new Manufacturer("Infinity", "USA");
        // create manufacturers in DB
        Manufacturer bmwInDataBase = manufacturerService.create(bmw);
        Manufacturer infinityInDataBase = manufacturerService.create(infinity);
        List<Manufacturer> manufacturerList = new ArrayList<>();
        manufacturerList.add(bmwInDataBase);
        manufacturerList.add(infinityInDataBase);
        System.out.println("Creating manufacturers in DataBase:");
        manufacturerList.forEach(System.out::println);
        Driver bob = new Driver("Bob", "0000");
        Driver alice = new Driver("Alice", "1111");
        Driver john = new Driver("John", "2222");
        // create drivers in DB
        Driver driverBobInDataBase = driverService.create(bob);
        Driver driverAliceInDataBase = driverService.create(alice);
        Driver driverJohnInDataBase = driverService.create(john);
        List<Driver> driverList = new ArrayList<>();
        driverList.add(driverBobInDataBase);
        driverList.add(driverAliceInDataBase);
        driverList.add(driverJohnInDataBase);
        System.out.println("Creating drivers in DataBase:");
        driverList.forEach(System.out::println);
        // Let's start testing
        Car x6ModelCar = new Car("X6", bmwInDataBase, List.of(driverBobInDataBase,
                driverAliceInDataBase));
        Car x5ModelCar = new Car("X5", bmwInDataBase, List.of(driverAliceInDataBase,
                driverJohnInDataBase));
        Car q50C = new Car("Q50", infinityInDataBase, List.of(driverBobInDataBase,
                driverJohnInDataBase));
        // create cars in DB
        Car firstCarInDataBase = carService.create(x6ModelCar);
        Car secondCarInDataBase = carService.create(x5ModelCar);
        Car thirdCarInDataBase = carService.create(q50C);
        List<Car> carList = new ArrayList<>();
        carList.add(firstCarInDataBase);
        carList.add(secondCarInDataBase);
        carList.add(thirdCarInDataBase);
        System.out.println("Creating cars in DataBase:");
        carList.forEach(System.out::println);
        // get
        System.out.println("Testing method for getting car by id: 2 from DataBase:");
        Car carAfterGet = carService.get(INDEX_FOR_TEST);
        System.out.println(carAfterGet);
        // getAll
        System.out.println("Testing method for getting all cars from DataBase:");
        carService.getAll().forEach(System.out::println);
        // update
        System.out.println("Testing update method on car by id: 2. Change model:");
        carAfterGet.setModel("X3");
        Car updatedCar = carService.update(carAfterGet);
        System.out.println(updatedCar);
        // addDriverToCar
        System.out.println("Testing addDriverToCar method. Add driver Bob to second Car in "
                + "DataBase:");
        carService.addDriverToCar(driverBobInDataBase, updatedCar);
        System.out.println(updatedCar);
        // removeDriverFromCar
        System.out.println("Testing removeDriverFromCar method. Remove driver Alice from second "
                + "car:");
        carService.removeDriverFromCar(driverAliceInDataBase, updatedCar);
        System.out.println(updatedCar);
        // getAllByDriver
        System.out.println("Testing getAllByDriver method. Get all car by driver Alice's id (2):");
        List<Car> allCarsWithFirstDriver = carService.getAllByDriver(INDEX_FOR_TEST);
        allCarsWithFirstDriver.forEach(System.out::println);
        // delete
        System.out.println("Testing delete method:");
        System.out.println(carService.delete(INDEX_FOR_TEST));
    }
}
