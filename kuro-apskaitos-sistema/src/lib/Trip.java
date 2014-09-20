package lib;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.LinkedList;

public class Trip extends DatabaseObject {

    private int id;
    private String company_code;
    private String description;
    private Date date_from;
    private Date date_until;
    private String start_location;
    private String end_location;

    @Override
    public Integer getPrimaryKey() {
        return new Integer(id);
    }

    @Override
    protected String getPrimaryKeyName() {
        return "id";
    }

    public static String[] getAccessibleAttributes() {
        return new String[]{"id", "company_code", "description", "date_from",
                    "date_until", "start_location", "end_location"};
    }

    public Trip(ResultSet resultSet) throws SQLException {
        this(resultSet.getInt(1), resultSet.getString(2), resultSet.getString(3),
                resultSet.getDate(4), resultSet.getDate(5), resultSet.getString(6),
                resultSet.getString(7));
        this.isCreated = true;
    }

    public Trip(int id, String company_code, String description, Date date_from, Date date_until, String start_location, String end_location) {
        super();
        this.id = id;
        this.company_code = company_code;
        this.description = description;
        this.date_from = date_from;
        this.date_until = date_until;
        this.start_location = start_location;
        this.end_location = end_location;
    }

    public static LinkedList<Trip> fetchAll() {
        HashMap<String, Object> options = new HashMap<String, Object>();
        options.put("table", "trip");
        return convertFromDatabaseObjectList(DatabaseObjectFetcher.fetch(options));
    }

    public static LinkedList<Trip> fetchByEmployeeSSN(String employee_ssn) {
        PreparedStatement ps = null;
        try {
            ps = connection.prepareStatement("SELECT trip.* FROM trip, employee_makes_trip WHERE employee_makes_trip.trip_id = trip.id AND employee_makes_trip.employee_ssn = ?;");
            ps.setString(1, employee_ssn);
        } catch (SQLException ex) {
            System.err.println(ex);
        }
        return Trip.fetch(ps);
    }

    public static Trip fetchByID(int id) {
        Trip result = null;
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM trip WHERE id = ? LIMIT 1;");
            ps.setInt(1, id);
            LinkedList<Trip> results = Trip.fetch(ps);
            if (results != null && !results.isEmpty()) {
                result = results.getFirst();
            }
        } catch (SQLException ex) {
            System.err.println(ex);
        }
        return result;
    }

    public static LinkedList<Trip> fetchByCompanyCode(String company_code) {
        LinkedList<Trip> result = null;
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM trip WHERE company_code = ?;");
            ps.setString(1, company_code);
            result = Trip.fetch(ps);
        } catch (SQLException ex) {
            System.err.println(ex);
        }
        return result;
    }

    public LinkedList<Employee> fetchEmployees() {
        LinkedList<Employee> result = null;
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT employee.* FROM employee, "
                    + "employee_makes_trip WHERE employee.ssn = employee_makes_trip.employee_ssn "
                    + "AND employee_makes_trip.trip_id = ?;");
            ps.setInt(1, this.id);
            result = Employee.fetch(ps);
        } catch (SQLException ex) {
            System.err.println(ex);
        }
        return result;
    }

    public boolean addEmployee(Employee employee) {
        if (!employee.isCreated) {
            return false;
        }
        try {
            PreparedStatement ps = connection.prepareStatement("INSERT INTO employee_makes_trip VALUES (?, ?);");
            ps.setString(1, employee.getSSN());
            ps.setInt(2, this.id);
            ps.execute();
            return true;
        } catch (SQLException ex) {
            System.err.println(ex);
        }
        return false;
    }

    public boolean removeEmployee(Employee employee) {
        if (!employee.isCreated) {
            return false;
        }
        try {
            PreparedStatement ps = connection.prepareStatement("DELETE FROM employee_makes_trip WHERE employee_ssn = ? AND trip_id = ?;");
            ps.setString(1, employee.getSSN());
            ps.setInt(2, this.id);
            ps.execute();
            return true;
        } catch (SQLException ex) {
            System.err.println(ex);
        }
        return false;
    }

    public LinkedList<Car> fetchCars() {
        LinkedList<Car> result = null;
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT car.* FROM car, "
                    + "car_takes_trip WHERE car.registration_number = car_takes_trip.car_registration_number "
                    + "AND car_takes_trip.trip_id = ?;");
            ps.setInt(1, this.id);
            result = Car.fetch(ps);
        } catch (SQLException ex) {
            System.err.println(ex);
        }
        return result;
    }

    public boolean addCar(Car car, long start_counter, long end_counter) {
        if (!car.isCreated) {
            return false;
        }
        try {
            PreparedStatement ps = connection.prepareStatement("INSERT INTO car_takes_trip VALUES (?, ?, ?, ?);");
            ps.setString(1, car.getRegistrationNumber());
            ps.setInt(2, this.id);
            ps.setLong(3, start_counter);
            ps.setLong(4, end_counter);
            ps.execute();
            return true;
        } catch (SQLException ex) {
            System.err.println(ex);
        }
        return false;
    }

    public boolean updateCarParams(Car car, long start_counter, long end_counter) {
        if (!car.isCreated) {
            return false;
        }
        try {
            PreparedStatement ps = connection.prepareStatement("UPDATE car_takes_trip SET start_counter = ?, end_counter = ? WHERE trip_id = ? AND car_registration_number = ?;");
            ps.setLong(1, start_counter);
            ps.setLong(2, end_counter);
            ps.setInt(3, this.id);
            ps.setString(4, car.getRegistrationNumber());
            ps.execute();
            return true;
        } catch (SQLException ex) {
            System.err.println(ex);
        }
        return false;
    }

    public CarTakesTrip getCarDetails(Car car) {
        if (!car.isCreated) {
            return null;
        }
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT car_takes_trip.* FROM car_takes_trip WHERE car_registration_number = ? AND trip_id = ?;");
            ps.setString(1, car.getRegistrationNumber());
            ps.setInt(2, this.id);
            ResultSet resultSet = ps.executeQuery();
            if (resultSet.next()) {
                return new CarTakesTrip(car.getRegistrationNumber(), this.id, resultSet.getLong(3), resultSet.getLong(4));
            } else {
                return null;
            }
        } catch (SQLException ex) {
            System.err.println(ex);
        }
        return null;
    }

    public LinkedList<CarTakesTrip> getCarDetails() {
        LinkedList<CarTakesTrip> result = null;
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT car_takes_trip.* FROM "
                    + "car_takes_trip WHERE car_takes_trip.trip_id = ?;");
            ps.setInt(1, this.id);
            ResultSet resultSet = ps.executeQuery();
            result = new LinkedList<CarTakesTrip>();
            while (resultSet.next()) {
                result.push(new CarTakesTrip(resultSet.getString(1), resultSet.getInt(2), resultSet.getLong(3), resultSet.getLong(4)));
            }
        } catch (SQLException ex) {
            System.err.println(ex);
        }
        return result;
    }

    public boolean removeCar(Car car) {
        if (!car.isCreated) {
            return false;
        }
        try {
            PreparedStatement ps = connection.prepareStatement("DELETE FROM car_takes_trip WHERE car_registration_number = ? AND trip_id = ?;");
            ps.setString(1, car.getRegistrationNumber());
            ps.setInt(2, this.id);
            ps.execute();
            return true;
        } catch (SQLException ex) {
            System.err.println(ex);
        }
        return false;
    }

    public static LinkedList<Trip> fetch(PreparedStatement ps) {
        return convertFromDatabaseObjectList(DatabaseObjectFetcher.fetch("trip", ps));
    }

    @Override
    protected PreparedStatement prepareUpdateStatement() throws SQLException {
        PreparedStatement ps = connection.prepareStatement("UPDATE trip SET "
                + " company_code = ?, description = ?, date_from = ?, date_until = ?,"
                + " start_location = ?,"
                + " end_location = ? WHERE id = ?");
        ps.setString(1, this.company_code);
        ps.setString(2, this.description);
        ps.setDate(3, this.date_from);
        ps.setDate(4, this.date_until);
        ps.setString(5, this.start_location);
        ps.setString(6, this.end_location);
        ps.setInt(7, this.id);
        return ps;
    }

    @Override
    protected PreparedStatement prepareInsertionStatement() throws SQLException {

        PreparedStatement ps = connection.prepareStatement("INSERT INTO trip "
                + "(company_code, description, date_from, date_until, "
                + "start_location, end_location) VALUES (?, ?, ?, ?, ?, ?);", Statement.RETURN_GENERATED_KEYS);

        ps.setString(1, this.company_code);
        ps.setString(2, this.description);
        ps.setDate(3, this.date_from);
        ps.setDate(4, this.date_until);
        ps.setString(5, this.start_location);
        ps.setString(6, this.end_location);

        return ps;
    }

    @Override
    protected void doAfterCreate(PreparedStatement ps) throws SQLException {
        ResultSet generatedKeys = ps.getGeneratedKeys();
        if (generatedKeys.next()) {
            this.id = generatedKeys.getInt(1);
        } else {
            throw new SQLException("Nepavyko gauti sugeneruoto id");
        }
    }

    @Override
    protected void destroyDependencies() throws SQLException {
        PreparedStatement ps = connection.prepareStatement("DELETE FROM car_takes_trip WHERE trip_id = ?;"
                + "DELETE FROM employee_makes_trip WHERE trip_id = ?;");
        ps.setInt(1, this.id);
        ps.setInt(2, this.id);
        ps.execute();

        for (Receipt receipt : Receipt.fetchListByTripID(this.id)) {
            receipt.destroy();
        }
    }

    private static LinkedList<Trip> convertFromDatabaseObjectList(LinkedList<DatabaseObject> objects) {
        if (objects == null) {
            return null;
        }
        LinkedList<Trip> result = new LinkedList<Trip>();
        for (DatabaseObject object : objects) {
            result.push((Trip) object);
        }
        return result;
    }

    @Override
    public boolean validate() {
        this.errors.clear();

        if (!this.isCreated && Trip.fetchByID(this.id) != null) {
            this.addError("Kelionė su unikaliu id jau egzistuoja");
        }

        if (this.company_code == null || this.company_code.isEmpty()) {
            this.addError("Kelionė turi priklausyti įmonei");
        } else if (Company.fetchByCode(this.company_code) == null) {
            this.addError("Įmonė neegzistuoja");
        }

        if (this.date_from == null || this.date_until == null) {
            this.addError("Kelionės data turi būti užpildytas");
        }

        if (this.end_location == null || this.end_location.isEmpty()) {
            this.addError("Neužpildytas kelionės tikslas");
        }
        if (this.start_location == null || this.start_location.isEmpty()) {
            this.addError("Neužpildytas kelionės pradžia");
        }

        return !this.hasErrors();
    }

    public int getID() {
        return this.id;
    }

    public void setCompanyCode(String company_code) {
        if (this.company_code == null || !this.company_code.equals(company_code)) {
            this.isUpdated = false;
            this.company_code = company_code;
        }
    }

    public String getCompanyCode() {
        return company_code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        if (this.description == null || !this.description.equals(description)) {
            this.isUpdated = false;
            this.description = description;
        }
    }

    public Date getDateFrom() {
        return date_from;
    }

    public void setDateFrom(Date date_from) {
        if (this.date_from == null || this.date_from.compareTo(date_from) != 0) {
            this.isUpdated = false;
            this.date_from = date_from;
        }
    }

    public Date getDateUntil() {
        return date_until;
    }

    public void setDateUntil(Date date_until) {
        if (this.date_until == null || this.date_until.compareTo(date_until) != 0) {
            this.isUpdated = false;
            this.date_until = date_until;
        }
    }

    public String getStartLocation() {
        return start_location;
    }

    public void setStartLocation(String start_location) {
        if (this.start_location == null || !this.start_location.equals(start_location)) {
            this.isUpdated = false;
            this.start_location = start_location;
        }
    }

    public String getEndLocation() {
        return end_location;
    }

    public void setEndLocation(String end_location) {
        if (this.end_location == null || !this.end_location.equals(end_location)) {
            this.isUpdated = false;
            this.end_location = end_location;
        }
    }
}
