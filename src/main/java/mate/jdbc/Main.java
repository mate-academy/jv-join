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
    private static final Injector injector = Injector.getInstance("mate.jdbc");
    private static final CarService carService =
            (CarService) injector.getInstance(CarService.class);
    private static final DriverService driverService =
            (DriverService) injector.getInstance(DriverService.class);
    private static final ManufacturerService manufacturerService =
            (ManufacturerService) injector.getInstance(ManufacturerService.class);

    static {
        addManufacturers();
        addDrivers();
        addCars();
    }

    public static void main(String[] args) {
        carService.delete(4L);
        System.out.println("\n----------carService.getAllByDriver");
        carService.getAllByDriver(1L).stream().forEach(System.out::println);
        updateCar();
        addDriverToCarThenRemovedThisDriverFromCar(driverService.get(3L),carService.get(2L));
        System.out.println("\n----------carService.getAll()");
        carService.getAll().stream().forEach(System.out::println);
    }

    private static void addDriverToCarThenRemovedThisDriverFromCar(Driver driver, Car car) {
        System.out.println("\naddDriverToCarThenRemovedDriverFromCar");
        System.out.println(">>> car before adding driver " + driver);
        System.out.println(carService.get(car.getId()));
        carService.addDriverToCar(driver,car);
        System.out.println("\n>>> car after added driver " + driver);
        System.out.println(carService.get(car.getId()));
        System.out.println("\n>>> car after removing driver " + driver);
        carService.removeDriverFromCar(driver,car);
        System.out.println(carService.get(car.getId()));
    }

    private static void updateCar() {
        System.out.println("----------carService.update()");
        Car trail = carService.get(8L);
        trail.setManufacturer(manufacturerService.get(4L));
        trail.setModel("X-Trail");
        trail.setDrivers(List.of(driverService.get(1L),
                driverService.get(5L),driverService.get(6L)));
        System.out.println(carService.update(trail));
        System.out.println(carService.get(8L));
    }

    private static void addCars() {
        if (carService.getAll().isEmpty()) {
            Car colt = new Car("Colt", manufacturerService.get(1L),
                    List.of(driverService.get(6L)));
            Car l200 = new Car("L200", manufacturerService.get(1L),
                    List.of(driverService.get(1L)));
            Car outlander = new Car("Outlander", manufacturerService.get(1L),
                    List.of(driverService.get(1L), driverService.get(2L)));
            Car polo = new Car("Polo", manufacturerService.get(2L),
                    List.of(driverService.get(1L), driverService.get(2L),
                    driverService.get(3L), driverService.get(4L), driverService.get(5L),
                            driverService.get(6L)));
            Car tiguan = new Car("Tiguan", manufacturerService.get(2L),
                    List.of(driverService.get(3L), driverService.get(6L)));
            Car logan = new Car("Logan", manufacturerService.get(3L),
                    List.of(driverService.get(4L), driverService.get(5L)));
            Car leaf = new Car("Leaf", manufacturerService.get(4L),
                    List.of(driverService.get(1L), driverService.get(2L), driverService.get(3L)));
            Car corolla = new Car("Corolla", manufacturerService.get(5L),
                    List.of(driverService.get(1L), driverService.get(2L), driverService.get(3L),
                            driverService.get(6L)));
            carService.create(colt);
            carService.create(l200);
            carService.create(outlander);
            carService.create(polo);
            carService.create(tiguan);
            carService.create(logan);
            carService.create(leaf);
            carService.create(corolla);
        }
    }

    private static void addDrivers() {
        if (driverService.getAll().isEmpty()) {
            driverService.create(new Driver("Ivan", "ln123"));
            driverService.create(new Driver("Petr", "ln234"));
            driverService.create(new Driver("Semen", "ln345"));
            driverService.create(new Driver("Igor", "ln456"));
            driverService.create(new Driver("Sergey", "ln567"));
            driverService.create(new Driver("Oksana", "ln678"));
        }
    }

    private static void addManufacturers() {
        if (manufacturerService.getAll().isEmpty()) {
            manufacturerService.create(new Manufacturer("wrong name", "some country"));
            manufacturerService.create(new Manufacturer("Volkswagen", "German"));
            manufacturerService.create(new Manufacturer("Renault", "France"));
            manufacturerService.create(new Manufacturer("Nissan", "Japan"));
            manufacturerService.create(new Manufacturer("Toyota", "Japan"));
            manufacturerService.update(new Manufacturer(1L, "Mitsubishi", "Japan"));
        }
    }
}
