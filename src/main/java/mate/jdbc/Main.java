package mate.jdbc;

import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.CarService;
import mate.jdbc.service.DriverService;
import mate.jdbc.service.ManufacturerService;

public class Main {
    private static Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        final Manufacturer renaultManufacturer = new Manufacturer("Renault", "France");
        final Manufacturer seatManufacturer = new Manufacturer("Seat", "Spain");
        final Car renaultCar = new Car("Renault", renaultManufacturer);
        final Car seatCar = new Car("Seat", seatManufacturer);
        final Driver driverJohn = new Driver("John", "000123");
        final Driver driverBob = new Driver("Bob", "111222");

        ManufacturerService manufacturerService = (ManufacturerService)
                injector.getInstance(ManufacturerService.class);
        manufacturerService.create(renaultManufacturer);
        manufacturerService.create(seatManufacturer);

        CarService carService = (CarService) injector.getInstance(CarService.class);
        carService.create(renaultCar);
        carService.create(seatCar);

        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        driverService.create(driverJohn);
        driverService.create(driverBob);

        carService.delete(2L);

        System.out.println("Model of car with id #1 is: " + carService.get(1L).getModel());

        carService.getAll().forEach(car ->
                System.out.println("All cars. Id: " + car.getId() + ", model: " + car.getModel()));

        carService.addDriverToCar(driverJohn, renaultCar);
        carService.removeDriverFromCar(driverJohn, renaultCar);

        carService.addDriverToCar(driverBob, seatCar);

        carService.getAllByDriver(27L).forEach(car ->
                System.out.println("Cars for driver. Id: "
                        + car.getId() + ", model: " + car.getModel()));

        seatCar.setModel("New Seat");
        seatCar.setManufacturer(renaultManufacturer);
        carService.update(seatCar);
    }
}
