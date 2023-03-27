package mate.jdbc;

import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.CarService;
import mate.jdbc.service.ManufacturerService;

public class Main {
    private static final Injector injector = Injector.getInstance("mate.jdbc");
    private static final CarService carService =
            (CarService) injector.getInstance(CarService.class);
    private static final ManufacturerService manufacturerService =
            (ManufacturerService) injector.getInstance(ManufacturerService.class);

    public static void main(String[] args) {
        Manufacturer bmwManufacturer = new Manufacturer();
        bmwManufacturer.setCountry("Germany");
        bmwManufacturer.setName("bmwManufacturer");
        manufacturerService.create(bmwManufacturer);

        Car bmw = new Car();
        bmw.setModel("bmw");
        bmw.setManufacturer(bmwManufacturer);
        carService.create(bmw);
        System.out.println(carService.get(4L));
        Car newBmw = carService.get(4L);
        newBmw.setModel("X5");
        System.out.println(carService.update(newBmw));
        System.out.println(newBmw);
        System.out.println(carService.getAll());
        System.out.println(carService.getAllByDriver(5L));
    }
}
