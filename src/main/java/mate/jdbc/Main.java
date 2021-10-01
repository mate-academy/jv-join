package mate.jdbc;

import java.util.List;
import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.CarService;
import mate.jdbc.service.DriverService;
import mate.jdbc.service.ManufacturerService;

public class Main {
    private static final String PACKAGE_NAME = "mate.jdbc";
    private static final Injector injector = Injector.getInstance(PACKAGE_NAME);
    private static final CarService carService;
    private static final DriverService driverService;
    private static final ManufacturerService manufacturerService;

    static {
        carService = (CarService) injector.getInstance(CarService.class);
        driverService = (DriverService) injector.getInstance(DriverService.class);
        manufacturerService = (ManufacturerService) injector.getInstance(ManufacturerService.class);
        fillManufacturersTableWithData();
        fillDriversTableWithData();
        fillCarsTableWithData();
    }

    public static void main(String[] args) {
        outputCarList(carService.getAll());

        Car hyundaiAccent = carService.get(4L);
        hyundaiAccent.setName("Hyundai Accent Ultra");
        carService.update(hyundaiAccent);
        outputCarList(carService.getAll());

        Car bentleyContinental = carService.get(1L);
        carService.delete(bentleyContinental.getId());
        outputCarList(carService.getAll());

        Car ferrariCalifornia = carService.get(2L);
        Driver anton = driverService.get(1L);
        carService.addDriverToCar(anton, ferrariCalifornia);
        outputCarList(carService.getAll());

        hyundaiAccent = carService.get(4L);
        Driver anna = driverService.get(3L);
        carService.removeDriverFromCar(anna, hyundaiAccent);
        outputCarList(carService.getAll());

        Driver kiril = driverService.get(4L);
        List<Car> allKirilCars = carService.getAllByDriver(kiril.getId());
        outputCarList(allKirilCars);
    }

    public static void fillManufacturersTableWithData() {
        manufacturerService.create(new Manufacturer("Bentley", "England"));
        manufacturerService.create(new Manufacturer("Hyundai", "South Korea"));
        manufacturerService.create(new Manufacturer("Mercedes", "Germany"));
        manufacturerService.create(new Manufacturer("Ferrari", "Italy"));
    }

    private static void fillDriversTableWithData() {
        driverService.create(new Driver("Anton", "12456789"));
        driverService.create(new Driver("Lena", "34343432"));
        driverService.create(new Driver("Anna", "9999999"));
        driverService.create(new Driver("Kiril", "6666666"));
        driverService.create(new Driver("Petro", "556565656"));
        driverService.create(new Driver("Maxim", "2222222222"));
        driverService.create(new Driver("Katia", "998888888"));
    }

    private static void fillCarsTableWithData() {
        Manufacturer bentley = manufacturerService.get(1L);
        Manufacturer hyundai = manufacturerService.get(2L);
        Manufacturer mercedes = manufacturerService.get(3L);
        Manufacturer ferrari = manufacturerService.get(4L);
        Driver anton = driverService.get(1L);
        Driver lena = driverService.get(2L);
        Driver anna = driverService.get(3L);
        Driver kiril = driverService.get(4L);
        Driver petro = driverService.get(5L);
        Driver maxim = driverService.get(6L);
        Driver katia = driverService.get(7L);

        Car bentleyContinental = new Car("BentleyContinental", bentley, List.of(anton, kiril));
        carService.create(bentleyContinental);

        Car ferrariCalifornia = new Car("Ferrari California", ferrari, List.of(maxim));
        carService.create(ferrariCalifornia);

        Car mercedesBenz = new Car("Mercedes-Benz", mercedes, List.of(lena, anna, kiril));
        carService.create(mercedesBenz);

        Car hyundaiAccent = new Car("Hyundai Accent", hyundai, List.of(petro, katia, anna, kiril));
        carService.create(hyundaiAccent);
    }

    private static void outputCarList(List<Car> cars) {
        cars.forEach(System.out::println);
        System.out.println("\n\n");
    }
}

