## Common mistakes (jv-join)

* Do not implement following methods in DAO layer, only in service layer. Use update method from DAO.
    - ```void addDriverToCar(Driver driver, Car car);```
    - ```void removeDriverFromCar(Driver driver, Car car);```
* Use `PreparedStatement` over `Statement`, even for a static query with no parameters in `getAll()` method. It's the best practice, and it's slightly faster.
* Be careful and do not create nested connection problems. Nested connection problem is the situation, when you open DB connection inside another connection. **This often occurs when you try to get from DB drivers for some car**.
* Remember about SQL style: use uppercase for SQL keywords in your queries.
    ```sql     
       Wrong:
        SELECT * from manufacturers WHERE is_deleted = false;                    
             
       Good:
        SELECT * FROM manufacturers WHERE is_deleted = FALSE;
    ```  
* Use aliases for table names in SQL queries with JOIN 
    ```sql     
       Wrong:
        SELECT cars.id AS car_id, manufacturers.id AS manufacturer_id
            FROM cars
            JOIN manufacturers ON cars.manufacturer_id = manufacturers.id
          WHERE...;                     
             
       Good:
        SELECT c.id AS car_id, m.id AS manufacturer_id
          FROM cars c
            JOIN manufacturers m ON c.manufacturer_id = m.id
          WHERE...;
    ``` 
* Use informative messages for exceptions.
    ```
        Wrong:
            throw new DataProcessingException("Can't get manufacturer", e);
            
        Good:
            throw new DataProcessingException("Can't get manufacturer by id " + id, e);
            throw new DataProcessingException("Can't insert manufacturer " + manufacturer, e);
    ``` 
* Do not open connection to DB on the service layer.
