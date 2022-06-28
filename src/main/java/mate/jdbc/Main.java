package mate.jdbc;

import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.service.CarService;
import mate.jdbc.service.ManufacturerService;

public class Main {
    private static final Injector injector = Injector.getInstance("mate.jdbc");
    public static void main(String[] args) {
        ManufacturerService manufacturerService = (ManufacturerService) injector.getInstance(ManufacturerService.class);
        CarService carService = (CarService) injector.getInstance(CarService.class);
//        Car car = new Car();
//        car.setModel("McLaren MCL36");
//        car.setManufacturer(manufacturerService.get(1L));
//        carService.create(car);
        Car car = carService.get(1L);
        System.out.println(car);
    }
}
