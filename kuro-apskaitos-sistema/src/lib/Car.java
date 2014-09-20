package lib;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;

public class Car extends DatabaseObject {

    private String registration_number;
    private String company_code;
    private String accountable_employee_ssn;
    private Date technical_check_expiration_date;
    private Date insurance_expiration_date;
    private String brand;

    @Override
    public String getPrimaryKey() {
        return registration_number;
    }

    @Override
    protected String getPrimaryKeyName() {
        return "registration_number";
    }

    public static String[] getAccessibleAttributes() {
        return new String[] { "registration_number", "company_code", "accountable_employee_ssn",
           "technical_check_expiration_date", "insurance_expiration_date", "brand"};
    }

    public Car(String registration_number, String company_code, String accountable_employee_ssn,
            Date technical_check_expiration_date,
            Date insurance_expiration_date,
            String brand) {
        super();
        this.registration_number = registration_number;
        this.company_code = company_code;
        this.accountable_employee_ssn = accountable_employee_ssn;
        this.technical_check_expiration_date = technical_check_expiration_date;
        this.insurance_expiration_date = insurance_expiration_date;
        this.brand = brand;
    }

    public Car(ResultSet resultSet) throws SQLException{
        this(resultSet.getString(1), resultSet.getString(2), resultSet.getString(3),
                resultSet.getDate(4), resultSet.getDate(5),
                resultSet.getString(6));
        this.isCreated = true;
    }

    public static LinkedList<Car> fetchAll() {
        HashMap<String, Object> options = new HashMap<String, Object>();
        options.put("table", "car");
        options.put("order", "registration_number");
        return convertFromDatabaseObjectList(DatabaseObjectFetcher.fetch(options));
    }

    public static LinkedList<Car> fetchListByCompanyCode(String company_code) {
        LinkedList<Car> result = null;
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT car.* FROM car WHERE car.company_code = ?;");
            ps.setString(1, company_code);
            result = Car.fetch(ps);
        } catch (SQLException ex) {
            System.err.println(ex);
        }
        return result;
    }

    public static LinkedList<Car> fetchListByAccountableEmployeeSSN(String ssn) {
        LinkedList<Car> result = null;
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT car.* FROM car WHERE car.accountable_employee_ssn = ?;");
            ps.setString(1, ssn);
            result = Car.fetch(ps);
        } catch (SQLException ex) {
            System.err.println(ex);
        }
        return result;
    }

    public static LinkedList<Car> fetchListByTripID(int trip_id) {
        LinkedList<Car> result = null;
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT car.* FROM car, car_takes_trip "
                    + " WHERE car_takes_trip.car_registration_number = car.registration_number AND car_takes_trip.trip_id = ?;");
            ps.setInt(1, trip_id);
            result = Car.fetch(ps);
        } catch (SQLException ex) {
            System.err.println(ex);
        }
        return result;
    }

    public static Car fetchByRegistrationNumber(String registration_number) {
        Car result = null;
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM car WHERE registration_number = ? LIMIT 1;");
            ps.setString(1, registration_number);
            LinkedList<Car> results = Car.fetch(ps);
            if (results != null && !results.isEmpty()) {
                result = results.getFirst();
            }
        } catch (SQLException ex) {
            System.err.println(ex);
        }
        return result;
    }

    public static LinkedList<Car> fetch(PreparedStatement ps) {
        return convertFromDatabaseObjectList(DatabaseObjectFetcher.fetch("car", ps));
    }

    public LinkedList<Fuel> fetchFuels() {
        LinkedList<Fuel> result = null;
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT fuel.* FROM fuel, car_uses_fuel "
                    + "WHERE fuel.code = car_uses_fuel.fuel_code "
                    + "AND car_uses_fuel.car_registration_number = ?;");
            ps.setString(1, this.registration_number);
            result = Fuel.fetch(ps);
        } catch (SQLException ex) {
            System.err.println(ex);
        }
        return result;
    }

    public boolean addFuel(Fuel fuel) {
        if (!fuel.isCreated) {
            return false;
        }
        try {
            PreparedStatement ps = connection.prepareStatement("INSERT INTO car_uses_fuel VALUES (?, ?);");
            ps.setString(1, this.registration_number);
            ps.setString(2, fuel.getCode());
            ps.execute();
            return true;
        } catch (SQLException ex) {
            System.err.println(ex);
        }
        return false;
    }

    public boolean removeFuel(Fuel fuel) {
        if (!fuel.isCreated) {
            return false;
        }
        try {
            PreparedStatement ps = connection.prepareStatement("DELETE FROM car_uses_fuel WHERE car_registration_number = ? AND fuel_code = ?;");
            ps.setString(1, this.registration_number);
            ps.setString(2, fuel.getCode());
            ps.execute();
            return true;
        } catch (SQLException ex) {
            System.err.println(ex);
        }
        return false;
    }

    @Override
    public void destroyDependencies() throws SQLException {
        PreparedStatement ps = connection.prepareStatement("DELETE FROM car_uses_fuel WHERE car_registration_number = ?;" +
                "DELETE FROM car_takes_trip WHERE car_registration_number = ?;");
        ps.setString(1, this.registration_number);
        ps.setString(2, this.registration_number);
        ps.execute();
    }

    @Override
    protected PreparedStatement prepareUpdateStatement() throws SQLException {
        PreparedStatement ps = connection.prepareStatement("UPDATE car "
                + "SET registration_number = ?, company_code = ?, "
                + "accountable_employee_ssn = ?, technical_check_expiration_date = ?, "
                + "insurance_expiration_date = ?, brand = ? WHERE registration_number = ?;");
        ps.setString(1, this.registration_number);
        ps.setString(2, this.company_code);
        ps.setString(3, this.accountable_employee_ssn);
        ps.setDate(4, this.technical_check_expiration_date);
        ps.setDate(5, this.insurance_expiration_date);
        ps.setString(6, this.brand);
        ps.setString(7, this.registration_number);
        return ps;
    }

    @Override
    protected PreparedStatement prepareInsertionStatement() throws SQLException {
        PreparedStatement ps = connection.prepareStatement("INSERT INTO car VALUES (?, ?, ?, ?, ?, ?);");
        ps.setString(1, this.registration_number);
        ps.setString(2, this.company_code);
        ps.setString(3, this.accountable_employee_ssn);
        ps.setDate(4, this.insurance_expiration_date);
        ps.setDate(5, this.technical_check_expiration_date);
        ps.setString(6, this.brand);
        return ps;
    }

    @Override
    public boolean validate() {
        this.errors.clear();

        if (this.registration_number == null || this.registration_number.isEmpty()) {
            this.addError("Automobilio registracijos numeris negali būti tuščias");
        } else if (!this.isCreated && Car.fetchByRegistrationNumber(this.registration_number) != null) {
            this.addError("Automobilis su tokiu registracijos numeriu jau egzistuoja");
        }

        if (this.company_code == null || this.company_code.isEmpty()) {
            this.addError("Automobilis turi priklausyti įmonei");
        } else if (Company.fetchByCode(this.company_code) == null) {
            this.addError("Įmonė neegzistuoja");
        }

        if (this.accountable_employee_ssn == null || this.accountable_employee_ssn.isEmpty()) {
            this.addError("Už automobili turi būti atsakingas asmuo");
        } else if (Employee.fetchBySsn(this.accountable_employee_ssn) == null) {
            this.addError("Darbuotojas neegzistuoja");
        }

        if (this.brand == null || this.brand.isEmpty()) {
            this.addError("Automobilio markė turi būti užpildyta");
        }

        if (this.technical_check_expiration_date == null) {
            this.addError("Techninės apžiūros pasibaigimo data turi būti užpildyta");
        }

        if (this.insurance_expiration_date == null) {
            this.addError("Draudimo pasibaigimo data turi būti užpildyta");
        }

        return !this.hasErrors();
    }


    private static LinkedList<Car> convertFromDatabaseObjectList(LinkedList<DatabaseObject> objects) {
        if (objects == null) {
            return null;
        }
        LinkedList<Car> result = new LinkedList<Car>();
        for (DatabaseObject object: objects) {
            result.push((Car) object);
        }
        return result;
    }

    public void setRegistrationNumber(String registration_number) {
        if (!this.isCreated && (this.registration_number == null || !this.registration_number.equals(registration_number))) {
            this.registration_number = registration_number;
            this.isUpdated = false;
        }
    }

    public String getAccountableEmployeeSSN() {
        return accountable_employee_ssn;
    }

    public void setAccountableEmployeeSSN(String accountable_employee_ssn) {
        if (this.accountable_employee_ssn == null || !this.accountable_employee_ssn.equals(accountable_employee_ssn)) {
            this.isUpdated = false;
            this.accountable_employee_ssn = accountable_employee_ssn;
        }
    }

    public String getCompanyCode() {
        return company_code;
    }

    public void setCompanyCode(String company_code) {
        if (this.company_code == null || !this.company_code.equals(company_code)) {
            this.isUpdated = false;
            this.company_code = company_code;
        }
    }

    public String getRegistrationNumber() {
        return registration_number;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        if (this.brand == null || !this.brand.equals(brand)) {
            this.isUpdated = false;
            this.brand = brand;
        }
    }

    public Date getTechnicalCheckExpirationDate() {
        return technical_check_expiration_date;
    }

    public void setTechnicalCheckExpirationDate(Date technical_check_expiration_date) {
        if (this.technical_check_expiration_date == null || this.technical_check_expiration_date.compareTo(insurance_expiration_date) != 0) {
            this.isUpdated = false;
            this.technical_check_expiration_date = technical_check_expiration_date;
        }
    }

    public Date getInsuranceExpirationDate() {
        return insurance_expiration_date;
    }

    public void setInsuranceExpirationDate(Date insurance_expiration_date) {
        if (this.insurance_expiration_date == null || this.insurance_expiration_date.compareTo(insurance_expiration_date) != 0) {
            this.isUpdated = false;
            this.insurance_expiration_date = insurance_expiration_date;
        }
    }
}
