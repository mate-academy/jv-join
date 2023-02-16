package mate.jdbc;

//import java.util.List;
import mate.jdbc.lib.Injector;
//import mate.jdbc.model.Car;
//import mate.jdbc.model.Driver;
//import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.CarService;
import mate.jdbc.service.DriverService;
import mate.jdbc.service.ManufacturerService;

public class Main {
    public static void main(String[] args) {
        Injector injector = Injector.getInstance("mate.jdbc");
        CarService carService = (CarService) injector.getInstance(CarService.class);
        ManufacturerService manufacturerService = (ManufacturerService)
                injector.getInstance(ManufacturerService.class);
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);

        //1. test carService.get();
        //System.out.println(carService.get(2L));

        //2. test carService.getAllByDriver()
        //System.out.println(carService.getAllByDriver(2L));

        //3. test carService.getAll()
        //System.out.println(carService.getAll());

        //4. Test carService.create()
        //Car i3 = new Car();
        //Manufacturer bmwManufacture = manufacturerService.get(18L);
        //i3.setModel("i3");
        //i3.setManufacturer(bmwManufacture);
        //Driver mark = driverService.get(1L);
        //i3.setDrivers(List.of(mark));
        //System.out.println(carService.create(i3));

        //5. testing addDriverToCar()
        //Car i3 = carService.get(5L);
        //Driver ben = driverService.get(4L);
        //carService.addDriverToCar(ben, i3);
        //System.out.println(carService.get(5L));

        //6. testing removeDriverFromCar()
        //Car i3 = carService.get(5L);
        //Driver ben = driverService.get(4L);
        //carService.removeDriverFromCar(ben, i3);
        //System.out.println(carService.get(5L));

        //7. testing carService.update()
        //Car i3 = carService.get(5L);
        //i3.setModel("i3_rex");
        //Manufacturer mersedesManufacturer = manufacturerService.get(17L);
        //i3.setManufacturer(mersedesManufacturer);
        //Driver daniel = driverService.get(2L);
        //Driver bill = driverService.get(3L);
        //3.setDrivers(List.of(daniel, bill));
        //carService.update(i3);
        //System.out.println(carService.get(5L));

        //8. testing carService.delete
        //Car i3 = carService.get(5L);
        //carService.delete(i3.getId());
        //System.out.println(carService.getAll());
    }
}
