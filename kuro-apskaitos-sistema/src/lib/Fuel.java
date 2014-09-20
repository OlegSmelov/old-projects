package lib;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;

public class Fuel extends DatabaseObject {

    private String code;
    private String title;

    @Override
    public String getPrimaryKey() {
        return code;
    }

    @Override
    protected String getPrimaryKeyName() {
        return "code";
    }

    public static String[] getAccessibleAttributes() {
        return new String[] { "code", "title" };
    }

    public Fuel(String code, String title) {
        this.code = code;
        this.title = title;
    }

    public Fuel(ResultSet resultSet) throws SQLException{
        this(resultSet.getString(1), resultSet.getString(2));
        this.isCreated = true;
    }

    public static HashMap<String, Fuel> convertToHash(LinkedList<Fuel> fuels) {
        if (fuels == null) {
            return null;
        }
        HashMap<String, Fuel> result = new HashMap<String, Fuel>();
        for (Fuel fuel : fuels) {
            result.put(fuel.getCode(), fuel);
        }
        return result;
    }

    public static LinkedList<Fuel> fetchAll() {
        HashMap<String, Object> options = new HashMap<String, Object>();
        options.put("table", "fuel");
        options.put("order", "code");
        return convertFromDatabaseObjectList(DatabaseObjectFetcher.fetch(options));
    }

    public static LinkedList<Fuel> fetchListByCarRegistrationNumber(String registration_number) {
        LinkedList<Fuel> result = null;
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT fuel.* FROM fuel, car_uses_fuel "
                    + "WHERE fuel.code = car_uses_fuel.fuel_code "
                    + "AND car_uses_fuel.car_registration_number = ?;");
            ps.setString(1, registration_number);
            result = Fuel.fetch(ps);
        } catch (SQLException ex) {
            System.err.println(ex);
        }
        return result;
    }

    public static LinkedList<Fuel> fetchListByCars(LinkedList<Car> cars) {
        if (cars == null) {
            return null;
        }
        LinkedList<Fuel> result = null;
        LinkedList<String> registration_numbers = new LinkedList<String>();
        for (Car car: cars) {
            if (!registration_numbers.contains(car.getRegistrationNumber())) {
                registration_numbers.add(car.getRegistrationNumber());
            }
        }
        LinkedList<String> inQueryList = new LinkedList<String>();
        String inQuery = null;
        for (String s: registration_numbers) {
            inQueryList.push(s);
            if (inQuery == null) {
              inQuery = "?";
            } else {
              inQuery += ", ?";
            }
        }
        if (inQuery == null) {
            return null;
        }
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT fuel.* FROM fuel, car_uses_fuel"
                    + " WHERE car_uses_fuel.fuel_code = fuel.code"
                    + " AND car_uses_fuel.car_registration_number IN (" + inQuery + ");");
            int i = 1;
            for (String s: inQueryList) {
                ps.setString(i++, s);
            }
            result = Fuel.fetch(ps);
        } catch (SQLException ex) {
            System.err.println(ex);
        }
        return result;
    }

    public static Fuel fetchByCode(String code) {
        Fuel result = null;
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM fuel WHERE code = ?;");
            ps.setString(1, code);
            LinkedList<Fuel> results = Fuel.fetch(ps);
            if (results != null && !results.isEmpty()) {
                result = results.getFirst();
            }
        } catch (SQLException ex) {
            System.err.println(ex);
        }
        return result;
    }

    public static LinkedList<Fuel> fetch(PreparedStatement ps) {
        return convertFromDatabaseObjectList(DatabaseObjectFetcher.fetch("fuel", ps));
    }

    @Override
    protected void destroyDependencies() throws SQLException {
        for (Receipt receipt : Receipt.fetchListByFuelCode(this.code)) {
            receipt.destroy();
        }
        PreparedStatement ps = connection.prepareStatement("DELETE FROM car_uses_fuel WHERE fuel_code = ?;");
        ps.setString(1, this.code);
        ps.execute();

        // TODO: should we also delete cars that have no "car_uses_fuel" rows?
    }

    @Override
    protected PreparedStatement prepareUpdateStatement() throws SQLException {
        PreparedStatement ps = connection.prepareStatement("UPDATE fuel SET code = ?, title = ? WHERE code = ?");
        ps.setString(1, this.code);
        ps.setString(2, this.title);
        ps.setString(3, this.code);
        return ps;
    }

    @Override
    protected PreparedStatement prepareInsertionStatement() throws SQLException {
        PreparedStatement ps = connection.prepareStatement("INSERT INTO fuel VALUES (?, ?);");

        ps.setString(1, this.code);
        ps.setString(2, this.title);
        return ps;
    }

    @Override
    public boolean validate() {
        this.errors.clear();

        if (this.code == null || this.code.isEmpty()) {
            this.addError("Degalų kodas negali būti tuščias");
        } else if (!this.isCreated && Fuel.fetchByCode(code) != null) {
            this.addError("Degalai su tokiu kodu jau egzistuoja");
        }
        if (this.title == null || this.title.isEmpty()) {
            this.addError("Degalų pavadinimas negali būti tuščias");
        } else if (this.title.length() < 5 || this.title.length() > 30) {
            this.addError("Degalų pavadinimas per trumpas arba per ilgas");
        }

        return !this.hasErrors();
    }

    private static LinkedList<Fuel> convertFromDatabaseObjectList(LinkedList<DatabaseObject> objects) {
        if (objects == null) {
            return null;
        }
        LinkedList<Fuel> result = new LinkedList<Fuel>();
        for (DatabaseObject object: objects) {
            result.push((Fuel) object);
        }
        return result;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        if (!this.isCreated && (this.code == null || !this.code.equals(code))) {
            this.code = code;
            this.isUpdated = false;
        }
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        if (this.title == null || !this.title.equals(title)) {
            this.title = title;
            this.isUpdated = false;
        }
    }
}
