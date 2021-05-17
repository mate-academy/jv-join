package mate.jdbc;

import java.util.ArrayList;
import java.util.Arrays;
import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.CarService;
import mate.jdbc.service.DriverService;
import mate.jdbc.service.ManufacturerService;

public class Main {
    private static final Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        Manufacturer seat = new Manufacturer("Seat", "Spain");
        Manufacturer acura = new Manufacturer("Acura", "Japan");
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        manufacturerService.create(seat);
        manufacturerService.create(acura);

        Driver mark = new Driver("Mark", "NU-234");
        Driver nick = new Driver("Nick", "SD-476");
        Driver pit = new Driver("Pit", "DT-761");
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        driverService.create(mark);
        driverService.create(nick);
        driverService.create(pit);

        Car seatLeon = new Car("Leon", seat);
        seatLeon.setDrivers(new ArrayList<>(Arrays.asList(mark, nick)));
        Car seatIbiza = new Car("Ibiza", seat);
        seatIbiza.setDrivers(new ArrayList<>(Arrays.asList(pit, mark)));
        Car acuraMdx = new Car("MDX", acura);
        acuraMdx.setDrivers(new ArrayList<>(Arrays.asList(nick)));
        CarService carService = (CarService) injector.getInstance(CarService.class);
        carService.create(seatLeon);
        carService.create(seatIbiza);
        carService.create(acuraMdx);

        System.out.println(carService.get(seatIbiza.getId()));
        carService.getAll().forEach(System.out::println);
        acuraMdx.setModel("mdx");
        carService.addDriverToCar(mark, acuraMdx);
        carService.removeDriverFromCar(nick, seatLeon);
        carService.getAll().forEach(System.out::println);
        System.out.println(carService.delete(acuraMdx.getId()));
        carService.getAll().forEach(System.out::println);
        carService.removeDriverFromCar(mark, seatLeon);
        carService.getAllByDriver(mark.getId()).forEach(System.out::println);
    }
}
