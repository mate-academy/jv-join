package mate.jdbc;

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

        ManufacturerService manufacturerService
                = (ManufacturerService) injector.getInstance(ManufacturerService.class);
        Manufacturer chevrolet = new Manufacturer("Chevrolet", "USA");
        Manufacturer honda = new Manufacturer("Honda", "Japan");
        Manufacturer ford = new Manufacturer("Ford", "USA");
        manufacturerService.create(chevrolet);
        manufacturerService.create(honda);
        manufacturerService.create(ford);

        CarService carService = (CarService) injector.getInstance(CarService.class);

        Car camaro = new Car(chevrolet.getId(), "camaro 2.0");
        camaro = carService.create(camaro);
        Car camaro2020 = new Car(chevrolet.getId(), "camaro 2.0 2020");
        camaro2020 = carService.create(camaro2020);
        Car civic = new Car(honda.getId(), "civic");
        civic = carService.create(civic);
        Car mustang = new Car(ford.getId(), "mustang 2014");
        mustang = carService.create(mustang);

        System.out.println("\nCARS LIST \n");
        carService.getAll().forEach(System.out::println);

        Car car = carService.get(civic.getId());
        car.setManufacturerId(honda.getId());
        car.setModel("civic 2019");
        carService.update(car);

        System.out.println("\nUpdated LIST OF CARS: \n");
        carService.getAll().forEach(System.out::println);

        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        Driver dmytro = new Driver("Dmytro", "DMT5430725430RLJG");
        Driver maxine = new Driver("Maxine", "MXTT55432745866539CS");
        Driver dan = new Driver("Denis", "DNS785999214146NS");
        dmytro = driverService.create(dmytro);
        maxine = driverService.create(maxine);
        dan = driverService.create(dan);

        carService.addDriverToCar(dmytro, camaro);
        carService.addDriverToCar(dmytro, civic);
        carService.addDriverToCar(dmytro, mustang);

        carService.addDriverToCar(maxine, civic);
        carService.addDriverToCar(maxine, mustang);
        carService.addDriverToCar(dan, mustang);

        carService.removeDriverFromCar(dmytro, camaro);
        System.out.println("\nCars that maxine use");
        carService.getAllByDriver(maxine.getId()).forEach(System.out::println);

        System.out.println("\n Cars that dmytro use");
        carService.getAllByDriver(dmytro.getId()).forEach(System.out::println);

        System.out.println("\n Cars List");
        carService.getAll().forEach(System.out::println);

        /*
        Cars List
        Car{id=1, manufacturerId=1, model='camaro 2.0'
                , manufacturer=Manufacturer{id=1, name='Chevrolet', country='USA'}
                        , drivers=[Driver{id=1, name='Dmytro', licenseNumber='DMT5430725430RLJG'}]}
        Car{id=2, manufacturerId=1, model='camaro 2.0 2020',
                manufacturer=Manufacturer{id=1, name='Chevrolet', country='USA'}, drivers=[]}
        Car{id=3, manufacturerId=2, model='civic'
                , manufacturer=Manufacturer{id=2, name='Honda', country='Japan'}
                        , drivers=[Driver{id=1, name='Dmytro', licenseNumber='DMT5430725430RLJG'}
                              , Driver{id=2, name='Maxine', licenseNumber='MXTT55432745866539CS'}]}
        Car{id=4, manufacturerId=3, model='mustang 2014'
                , manufacturer=Manufacturer{id=3, name='Ford', country='USA'}
                        , drivers=[Driver{id=1, name='Dmytro', licenseNumber='DMT5430725430RLJG'}
                            , Driver{id=2, name='Maxine', licenseNumber='MXTT55432745866539CS'}
                            , Driver{id=3, name='Denis', licenseNumber='DNS785999214146NS'}]}
         */
    }
}
